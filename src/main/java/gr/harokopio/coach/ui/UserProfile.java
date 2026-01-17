package gr.harokopio.coach.ui;

public class UserProfile {
    private Double weightKg;

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public boolean hasWeight() {
        return weightKg != null && weightKg > 0;
    }
}
