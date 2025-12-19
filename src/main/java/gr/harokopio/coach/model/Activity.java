package gr.harokopio.coach.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Activity {

    private final SportType sport;
    private final Instant startTime;
    private final List<Lap> laps;

    public Activity(SportType sport, Instant startTime, List<Lap> laps) {
        if (sport == null) {
            throw new IllegalArgumentException("sport cannot be null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("startTime cannot be null");
        }
        if (laps == null) {
            throw new IllegalArgumentException("laps cannot be null");
        }
        this.sport = sport;
        this.startTime = startTime;
        this.laps = new ArrayList<>(laps);
    }

    public SportType getSport() {
        return sport;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public List<Lap> getLaps() {
        return Collections.unmodifiableList(laps);
    }

    public boolean isEmpty() {
        return laps.isEmpty();
    }
}
