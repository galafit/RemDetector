package rem.saccade;

/**
 * Created by galafit on 18/11/18.
 */
public class Saccade {
    long startTime; // ms
    long duration; // ms
    int value;

    public Saccade(long startTime, long duration, int value) {
        this.startTime = startTime;
        this.duration = duration;
        this.value = value;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return startTime + duration;
    }

    public long getDuration() {
        return duration;
    }

    public int getValue() {
        return value;
    }
}
