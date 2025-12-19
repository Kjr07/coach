package gr.harokopio.coach.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lap {

    private final Instant startTime;
    private final List<Track> tracks;

    public Lap(Instant startTime, List<Track> tracks) {
        if (startTime == null) {
            throw new IllegalArgumentException("startTime cannot be null");
        }
        if (tracks == null) {
            throw new IllegalArgumentException("tracks cannot be null");
        }
        this.startTime = startTime;
        this.tracks = new ArrayList<>(tracks);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public List<Track> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }
}
