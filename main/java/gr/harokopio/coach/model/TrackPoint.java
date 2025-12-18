package gr.harokopio.coach.model;

import java.time.Instant;

public class TrackPoint {

    private Instant time;
    private Double latitude;
    private Double longitude;
    private Double altitudeMeters;
    private Double distanceMeters;
    private Integer heartRate;

    public TrackPoint(Instant time,
                      Double latitude,
                      Double longitude,
                      Double altitudeMeters,
                      Double distanceMeters,
                      Integer heartRate) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitudeMeters = altitudeMeters;
        this.distanceMeters = distanceMeters;
        this.heartRate = heartRate;
    }

    public Instant getTime() {
        return time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAltitudeMeters() {
        return altitudeMeters;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public Integer getHeartRate() {
        return heartRate;
    }
}
