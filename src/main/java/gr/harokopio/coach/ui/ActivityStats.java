package gr.harokopio.coach.ui;

import gr.harokopio.coach.model.Activity;
import gr.harokopio.coach.model.Lap;
import gr.harokopio.coach.model.Track;
import gr.harokopio.coach.model.TrackPoint;

import java.time.Duration;
import java.time.Instant;

public class ActivityStats {

    public static double totalDistanceMeters(Activity activity) {
        double maxDist = 0.0;
        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                for (TrackPoint tp : track.getPoints()) {
                    Double d = tp.getDistanceMeters();
                    if (d != null && d > maxDist) {
                        maxDist = d;
                    }
                }
            }
        }
        return maxDist;
    }

    public static long totalDurationSeconds(Activity activity) {
        Instant minTime = null;
        Instant maxTime = null;

        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                for (TrackPoint tp : track.getPoints()) {
                    Instant t = tp.getTime();
                    if (t == null) continue;

                    if (minTime == null || t.isBefore(minTime)) minTime = t;
                    if (maxTime == null || t.isAfter(maxTime)) maxTime = t;
                }
            }
        }

        if (minTime == null || maxTime == null) return 0;
        return Duration.between(minTime, maxTime).getSeconds();
    }

    public static double averageHeartRate(Activity activity) {
        long sum = 0;
        long count = 0;

        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                for (TrackPoint tp : track.getPoints()) {
                    Integer hr = tp.getHeartRate();
                    if (hr != null) {
                        sum += hr;
                        count++;
                    }
                }
            }
        }

        if (count == 0) return 0;
        return (double) sum / count;
    }

    public static String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;

        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%d:%02d", m, s);
    }
}


