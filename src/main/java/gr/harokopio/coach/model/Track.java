package gr.harokopio.coach.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Track {

    private final List<TrackPoint> points;

    public Track(List<TrackPoint> points) {
        if (points == null) {
            throw new IllegalArgumentException("points cannot be null");
        }
        this.points = new ArrayList<>(points);
    }

    public List<TrackPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }
}
