package gr.harokopio.coach.ui;

import gr.harokopio.coach.model.Activity;

public class SimpleCalories {

    private SimpleCalories() {}

    // C = μ * w * t  (t σε ώρες)
    public static int estimate(Activity activity, double weightKg, long durationSeconds) {
        if (weightKg <= 0 || durationSeconds <= 0) return 0;

        double hours = durationSeconds / 3600.0;
        double mu = multiplier(activity);
        double c = mu * weightKg * hours;

        return (int) Math.max(0, Math.round(c));
    }

    // Μονάδα: kcal / (kg * hour)
    private static double multiplier(Activity a) {
        String s = String.valueOf(a.getSport()).toUpperCase();
        switch (s) {
            case "RUNNING": return 9.8;
            case "CYCLING": return 7.5;
            case "SWIMMING": return 8.0;
            case "WALKING": return 3.5;
            default: return 5.0;
        }
    }
}
