package com.mygdx.game.utils;

import com.mygdx.game.utils.exceptions.InvalidArgumentException;
import com.mygdx.game.utils.exceptions.InvalidFieldException;

import java.util.*;

/**
 * Timer that ticks up from 0 to {@link #duration}. Can be injected with {@link TimeMarkedRunnable} instances.
 */
public class TimeTicker implements Updatable, Resettable {

    private float time = 0f;
    private boolean justFinished;
    private final float duration;
    private final Set<TimeMarkedRunnable> timeMarkedRunnables = new TreeSet<>();
    private final Queue<TimeMarkedRunnable> timeMarkedRunnableQueue = new PriorityQueue<>();

    public TimeTicker() {
        this(1f);
    }

    public TimeTicker(float duration, TimeMarkedRunnable... timeMarkedRunnables)
            throws InvalidArgumentException {
        this(duration, Arrays.asList(timeMarkedRunnables));
    }

    public TimeTicker(float duration, Collection<TimeMarkedRunnable> timeMarkedRunnables)
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