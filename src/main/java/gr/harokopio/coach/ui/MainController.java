package gr.harokopio.coach.ui;

import gr.harokopio.coach.io.TcxActivityReader;
import gr.harokopio.coach.model.Activity;
import gr.harokopio.coach.model.SportType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {

    @FXML private TableView<ActivityRow> activitiesTable;
    @FXML private Label totalsLabel;
    @FXML private Label statusLabel;

    @FXML private TextField weightField;
    @FXML private TextField ageField;
    @FXML private ChoiceBox<String> sexChoice;
    @FXML private ChoiceBox<String> methodChoice;
    @FXML private Label profileStatusLabel;

    // Daily goal UI
    @FXML private TextField dailyGoalField;
    @FXML private Label dailyGoalStatusLabel;
    @FXML private TableView<DailyGoalRow> dailyGoalTable;

    private final ObservableList<ActivityRow> rows = FXCollections.observableArrayList();
    private final List<Activity> loadedActivities = new ArrayList<>();

    private final ObservableList<DailyGoalRow> goalRows = FXCollections.observableArrayList();

    private static final DateTimeFormatter START_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Profile defaults
    private double weightKg = 60.0;
    private int ageYears = 22;
    private Sex sex = Sex.FEMALE;
    private CalorieMethod method = CalorieMethod.SIMPLE;

    private int dailyGoalKcal = 500;

    private enum Sex { FEMALE, MALE }
    private enum CalorieMethod { SIMPLE, HR_BASED }

    // ======= DailyGoalRow class (for table) =======
    public static class DailyGoalRow {
        private final String date;
        private final int calories;
        private final int goal;
        private final String achieved;
        private final int remaining;

        public DailyGoalRow(String date, int calories, int goal, String achieved, int remaining) {
            this.date = date;
            this.calories = calories;
            this.goal = goal;
            this.achieved = achieved;
            this.remaining = remaining;
        }

        public String getDate() { return date; }
        public int getCalories() { return calories; }
        public int getGoal() { return goal; }
        public String getAchieved() { return achieved; }
        public int getRemaining() { return remaining; }
    }

    @FXML
    private void initialize() {
        activitiesTable.setItems(rows);

        sexChoice.setItems(FXCollections.observableArrayList("Female", "Male"));
        sexChoice.setValue("Female");

        methodChoice.setItems(FXCollections.observableArrayList("Simple (μ*w*t)", "Heart Rate (HR)"));
        methodChoice.setValue("Simple (μ*w*t)");

        weightField.setText(String.valueOf(weightKg));
        ageField.setText(String.valueOf(ageYears));

        // daily goal defaults
        dailyGoalTable.setItems(goalRows);
        dailyGoalField.setText(String.valueOf(dailyGoalKcal));
        dailyGoalStatusLabel.setText("Daily goal: " + dailyGoalKcal + " kcal");

        statusLabel.setText("Ready.");
        totalsLabel.setText("Totals: -");
        profileStatusLabel.setText(savedProfileText());
    }

    // ================= Profile =================

    @FXML
    private void onSaveProfile() {
        try {
            double w = Double.parseDouble(weightField.getText().trim().replace(",", "."));
            if (w <= 0) throw new IllegalArgumentException("Weight must be > 0");
            weightKg = w;

            String ageTxt = ageField.getText().trim();
            int a = Integer.parseInt(ageTxt);
            if (a <= 0) throw new IllegalArgumentException("Age must be > 0");
            ageYears = a;

            sex = "Male".equals(sexChoice.getValue()) ? Sex.MALE : Sex.FEMALE;
            method = "Heart Rate (HR)".equals(methodChoice.getValue()) ? CalorieMethod.HR_BASED : CalorieMethod.SIMPLE;

            profileStatusLabel.setText(savedProfileText());
            recomputeRowsFromLoaded();     // refresh calories + totals
            recomputeDailyGoalTable();     // refresh per-day calories

        } catch (Exception ex) {
            ex.printStackTrace();
            profileStatusLabel.setText("ERROR: " + ex.getMessage());
        }
    }

    private String savedProfileText() {
        return "Saved profile: weight=" + weightKg + " kg, age=" + ageYears +
                ", sex=" + (sex == Sex.MALE ? "Male" : "Female") +
                ", method=" + (method == CalorieMethod.HR_BASED ? "HR" : "Simple");
    }

    // ================= Load Activities =================

    @FXML
    private void onLoadFiles() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select .tcx files");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TCX Files", "*.tcx"));

            List<File> files = fc.showOpenMultipleDialog(statusLabel.getScene().getWindow());
            if (files == null || files.isEmpty()) {
                statusLabel.setText("No files selected.");
                return;
            }
            loadFiles(files);

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("ERROR loading files: " + ex.getMessage());
        }
    }

    @FXML private void onLoadSample1() { loadSample("/sample1.tcx"); }
    @FXML private void onLoadSample2() { loadSample("/sample2.tcx"); }
    @FXML private void onLoadSample3() { loadSample("/sample3.tcx"); }

    private void loadSample(String resourcePath) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                statusLabel.setText("ERROR: Δεν βρέθηκε " + resourcePath + " στα resources");
                return;
            }
            File tcxFile = new File(url.toURI());
            loadFiles(List.of(tcxFile));
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("ERROR loading " + resourcePath + ": " + ex.getMessage());
        }
    }

    private void loadFiles(List<File> files) {
        TcxActivityReader reader = new TcxActivityReader();

        loadedActivities.clear();
        rows.clear();

        int activitiesCount = 0;
        double totalKm = 0.0;
        long totalSeconds = 0;
        int totalCalories = 0;

        for (File f : files) {
            List<Activity> acts = reader.readAll(f);
            if (acts == null) continue;

            for (Activity a : acts) {
                loadedActivities.add(a);

                long durSec = durationSeconds(a);
                double distKm = distanceKm(a);
                int avgHr = avgHeartRate(a);

                int calories = caloriesFor(a.getSport(), durSec, avgHr);

                rows.add(new ActivityRow(
                        String.valueOf(a.getSport()),
                        START_FMT.format(a.getStartTime()),
                        formatSeconds(durSec),
                        round2(distKm),
                        round2(avgSpeedKmh(distKm, durSec)),
                        avgHr,
                        calories
                ));

                activitiesCount++;
                totalKm += distKm;
                totalSeconds += durSec;
                totalCalories += calories;
            }
        }

        statusLabel.setText("Loaded " + files.size() + " file(s), activities: " + activitiesCount);
        totalsLabel.setText("Totals: activities=" + activitiesCount +
                " | distance=" + round2(totalKm) + " km" +
                " | time=" + formatSeconds(totalSeconds) +
                " | calories=" + totalCalories);

        recomputeDailyGoalTable();
    }

    private void recomputeRowsFromLoaded() {
        if (loadedActivities.isEmpty()) return;

        rows.clear();

        int activitiesCount = 0;
        double totalKm = 0.0;
        long totalSeconds = 0;
        int totalCalories = 0;

        for (Activity a : loadedActivities) {
            long durSec = durationSeconds(a);
            double distKm = distanceKm(a);
            int avgHr = avgHeartRate(a);

            int calories = caloriesFor(a.getSport(), durSec, avgHr);

            rows.add(new ActivityRow(
                    String.valueOf(a.getSport()),
                    START_FMT.format(a.getStartTime()),
                    formatSeconds(durSec),
                    round2(distKm),
                    round2(avgSpeedKmh(distKm, durSec)),
                    avgHr,
                    calories
            ));

            activitiesCount++;
            totalKm += distKm;
            totalSeconds += durSec;
            totalCalories += calories;
        }

        totalsLabel.setText("Totals: activities=" + activitiesCount +
                " | distance=" + round2(totalKm) + " km" +
                " | time=" + formatSeconds(totalSeconds) +
                " | calories=" + totalCalories);
    }

    // ================= Daily Goal =================

    @FXML
    private void onApplyDailyGoal() {
        try {
            int g = Integer.parseInt(dailyGoalField.getText().trim());
            if (g <= 0) throw new IllegalArgumentException("Goal must be > 0");
            dailyGoalKcal = g;

            dailyGoalStatusLabel.setText("Daily goal: " + dailyGoalKcal + " kcal");
            recomputeDailyGoalTable();

        } catch (Exception ex) {
            ex.printStackTrace();
            dailyGoalStatusLabel.setText("ERROR: " + ex.getMessage());
        }
    }

    private void recomputeDailyGoalTable() {
        goalRows.clear();

        if (loadedActivities.isEmpty()) {
            goalRows.add(new DailyGoalRow("-", 0, dailyGoalKcal, "-", dailyGoalKcal));
            return;
        }

        // group calories per LocalDate
        Map<LocalDate, Integer> caloriesByDate = new TreeMap<>();

        for (Activity a : loadedActivities) {
            LocalDate date = a.getStartTime().atZone(ZoneId.systemDefault()).toLocalDate();
            long durSec = durationSeconds(a);
            int avgHr = avgHeartRate(a);
            int cals = caloriesFor(a.getSport(), durSec, avgHr);

            caloriesByDate.merge(date, cals, Integer::sum);
        }

        for (var e : caloriesByDate.entrySet()) {
            LocalDate d = e.getKey();
            int cals = e.getValue();

            boolean achieved = cals >= dailyGoalKcal;
            int remaining = Math.max(0, dailyGoalKcal - cals);

            goalRows.add(new DailyGoalRow(
                    DATE_FMT.format(d),
                    cals,
                    dailyGoalKcal,
                    achieved ? "YES" : "NO",
                    remaining
            ));
        }
    }

    // ================= Stats helpers =================

    private double distanceKm(Activity a) {
        double maxMeters = 0.0;
        boolean has = false;

        for (var lap : a.getLaps()) {
            for (var track : lap.getTracks()) {
                for (var p : track.getPoints()) {
                    Double d = p.getDistanceMeters();
                    if (d != null) {
                        has = true;
                        if (d > maxMeters) maxMeters = d;
                    }
                }
            }
        }
        return has ? (maxMeters / 1000.0) : 0.0;
    }

    private long durationSeconds(Activity a) {
        Instant min = null;
        Instant max = null;

        for (var lap : a.getLaps()) {
            for (var track : lap.getTracks()) {
                for (var p : track.getPoints()) {
                    Instant t = p.getTime();
                    if (t == null) continue;

                    if (min == null || t.isBefore(min)) min = t;
                    if (max == null || t.isAfter(max)) max = t;
                }
            }
        }
        if (min == null || max == null || max.isBefore(min)) return 0;
        return Duration.between(min, max).getSeconds();
    }

    private int avgHeartRate(Activity a) {
        long sum = 0;
        int count = 0;

        for (var lap : a.getLaps()) {
            for (var track : lap.getTracks()) {
                for (var p : track.getPoints()) {
                    Integer hr = p.getHeartRate();
                    if (hr != null) {
                        sum += hr;
                        count++;
                    }
                }
            }
        }
        if (count == 0) return 0;
        return (int) Math.round(sum / (double) count);
    }

    private double avgSpeedKmh(double distKm, long durSec) {
        if (durSec <= 0) return 0.0;
        return distKm / (durSec / 3600.0);
    }

    // ================= Calories =================

    private int caloriesFor(SportType sport, long durationSeconds, int avgHr) {
        if (durationSeconds <= 0) return 0;

        if (method == CalorieMethod.SIMPLE) {
            double mu = simpleMuFor(sport);
            double hours = durationSeconds / 3600.0;
            double c = mu * weightKg * hours;
            return (int) Math.round(Math.max(0, c));
        }

        // HR-based (needs avgHr)
        if (avgHr <= 0) return 0;

        double minutes = durationSeconds / 60.0;
        double c;

        if (sex == Sex.MALE) {
            c = (-55.0969 + (0.6309 * avgHr) + (0.1966 * weightKg) + (0.2017 * ageYears)) * minutes / 4.184;
        } else {
            c = (-20.4022 + (0.4472 * avgHr) + (0.1263 * weightKg) + (0.074 * ageYears)) * minutes / 4.184;
        }

        return (int) Math.round(Math.max(0, c));
    }

    private double simpleMuFor(SportType sport) {
        if (sport == null) return 6.0;
        switch (sport) {
            case WALKING:  return 3.5;
            case RUNNING:  return 10.0;
            case CYCLING:  return 8.0;
            case SWIMMING: return 9.0;
            default:       return 6.0;
        }
    }

    // ================= Formatting =================

    private String formatSeconds(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;

        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%d:%02d", m, s);
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
