package gr.harokopio.coach.ui;

public class ActivityRow {
    private final String sport;
    private final String start;
    private final String duration;
    private final double distanceKm;
    private final double avgSpeedKmh;
    private final int avgHr;


    private final int caloriesSimple;

    public ActivityRow(String sport, String start, String duration,
                       double distanceKm, double avgSpeedKmh, int avgHr, int caloriesSimple) {
        this.sport = sport;
        this.start = start;
        this.duration = duration;
        this.distanceKm = distanceKm;
        this.avgSpeedKmh = avgSpeedKmh;
        this.avgHr = avgHr;
        this.caloriesSimple = caloriesSimple;
    }

    public String getSport() { return sport; }
    public String getStart() { return start; }
    public String getDuration() { return duration; }
    public double getDistanceKm() { return distanceKm; }
    public double getAvgSpeedKmh() { return avgSpeedKmh; }
    public int getAvgHr() { return avgHr; }
    public int getCaloriesSimple() { return caloriesSimple; }
}
