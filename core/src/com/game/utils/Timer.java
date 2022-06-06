package com.game.utils;

import com.game.utils.exceptions.InvalidArgumentException;
import com.game.utils.exceptions.InvalidFieldException;

import java.util.*;

/**
 * Timer that ticks up from 0 to {@link #duration}. Can be injected with {@link TimeMarkedRunnable} instances.
 */
public class Timer implements Updatable, Resettable {

    private float time;
    private float duration;
    private boolean justFinished;
    private final Set<TimeMarkedRunnable> timeMarkedRunnables = new TreeSet<>();
    private final Queue<TimeMarkedRunnable> timeMarkedRunnableQueue = new PriorityQueue<>();

    /**
     * Instantiates a new Time ticker.
     */
    public Timer() {
        this(1f);
    }

    /**
     * Instantiates a new Time ticker.
     *
     * @param duration            the duration
     * @param timeMarkedRunnables the time marked runnables
     * @throws InvalidArgumentException the invalid argument exception
     */
    public Timer(float duration, TimeMarkedRunnable... timeMarkedRunnables)
            throws InvalidArgumentException {
        this(duration, Arrays.asList(timeMarkedRunnables));
    }

    /**
     * Instantiates a new Time ticker.
     *
     * @param duration            the duration
     * @param timeMarkedRunnables the time marked runnables
     * @throws InvalidArgumentException the invalid argument exception
     */
    public Timer(float duration, Collection<TimeMarkedRunnable> timeMarkedRunnables)
            throws InvalidArgumentException {
        if (duration <= 0f) {
            throw new InvalidArgumentException(String.valueOf(duration), "duration (which must be greater than 0)");
        }
        timeMarkedRunnables.forEach(timeMarkedRunnable -> {
            if (timeMarkedRunnable.time() < 0f || timeMarkedRunnable.time() > duration) {
                throw new InvalidFieldException(String.valueOf(timeMarkedRunnable.time()),
                                                "time marked runnable: time", "time marked runnable");
            }
        });
        this.timeMarkedRunnables.addAll(timeMarkedRunnables);
        this.duration = duration;
        reset();
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     * @throws InvalidArgumentException thrown if duration is less than zero
     */
    public void setDuration(float duration)
        throws InvalidArgumentException {
        if (duration < 0f) {
            throw new InvalidArgumentException(String.valueOf(duration), "duration");
        }
        this.duration = duration;
    }

    /**
     * Gets ratio between duration (max time) and current time.
     *
     * @return the ratio
     */
    public float getRatio() {
        return duration > 0f ? Math.min(time / duration, 1f) : 0f;
    }

    /**
     * Returns if {@link #time} is equal to zero.
     *
     * @return true if time = 0
     */
    public boolean isAtBeginning() {
        return time == 0f;
    }

    /**
     * Returns if {@link #time} is greater than or equal to {@link #duration}.
     *
     * @return true if time >= duration
     */
    public boolean isFinished() {
        return time >= duration;
    }

    /**
     * Returns if {@link #time} is greater than or equal to {@link #duration} and was not previously.
     *
     * @return true is time just became greater than or equal to duration
     */
    public boolean isJustFinished() {
        return justFinished;
    }

    @Override
    public void update(float delta) {
        boolean finishedBefore = isFinished();
        time = Math.min(duration, time + delta);
        while (!timeMarkedRunnableQueue.isEmpty() && timeMarkedRunnableQueue.peek().time() <= time) {
            TimeMarkedRunnable timeMarkedRunnable = timeMarkedRunnableQueue.poll();
            if (timeMarkedRunnable == null || timeMarkedRunnable.runnable() == null) {
                continue;
            }
            timeMarkedRunnable.runnable().run();
        }
        justFinished = !finishedBefore && isFinished();
    }

    @Override
    public void reset() {
        time = 0f;
        timeMarkedRunnableQueue.clear();
        timeMarkedRunnableQueue.addAll(timeMarkedRunnables);
    }

}