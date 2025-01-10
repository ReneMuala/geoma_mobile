package ord.descartes.geoma.engine;

import android.annotation.SuppressLint;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeoutCounter implements Counter {
    final private Duration duration;
    @SuppressLint("NewApi")
    final private LocalDateTime startTime = LocalDateTime.now();

    public TimeoutCounter(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void count() {}

    @SuppressLint("NewApi")
    @Override
    public boolean isDone() {
        return Duration.between(startTime, LocalDateTime.now()).compareTo(duration) > 0;
    }
}