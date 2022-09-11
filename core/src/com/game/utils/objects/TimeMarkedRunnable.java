package com.game.utils.objects;

/**
 * Intended to be used with {@link Timer}. Performs {@link Runnable#run()} on each {@link Runnable} in the
 * supplied Collection when the supplied time has just been surpassed by a timer.
 */
public record TimeMarkedRunnable(Float time, Runnable runnable) implements Comparable<TimeMarkedRunnable> {

    @Override
    public int compareTo(TimeMarkedRunnable o) {
        return time.compareTo(o.time());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TimeMarkedRunnable timeMarkedRunnable && time.equals(timeMarkedRunnable.time());
    }

    @Override
    public int hashCode() {
        return time.hashCode();
    }

}
