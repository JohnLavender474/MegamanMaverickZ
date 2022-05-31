package com.mygdx.game.utils;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Timer that ticks up from 0 to {@link #duration}. Can be injected with {@link Marker} instances.
 */
@NoArgsConstructor
public class TimeTicker implements Updatable, Resettable {

    private float time = 0f;
    private float duration = 0f;
    private boolean justFinished = false;
    private final Map<String, Marker> markers = new HashMap<>();

    /**
     * Instantiates a new TimeTicker.
     *
     * @param duration the duration
     */
    public TimeTicker(float duration) {
        setDuration(duration);
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Add marker.
     *
     * @param key  the key
     * @param time the time
     */
    public void addMarker(String key, float time) {
        markers.put(key, new Marker(time));
    }

    /**
     * Remove marker.
     *
     * @param key the key
     */
    public void removeMarker(String key) {
        markers.remove(key);
    }

    /**
     * Returns if the {@link Marker} mapped to the key has been surpassed.
     *
     * @param key the key
     * @return true if the Marker has been surpassed
     */
    public boolean isMarkerSurpassed(String key) {
        Marker marker = markers.get(key);
        return marker != null && marker.isSurpassed;
    }

    /**
     * Returns if the {@link Marker} mapped to the key has been surpassed and was not previously surpassed.
     *
     * @param key the key
     * @return true if the Market has just been surpassed
     */
    public boolean isMarkerJustSurpassed(String key) {
        Marker marker = markers.get(key);
        return marker != null && marker.isSurpassed && !marker.wasSurpassed;
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
     * Gets the result of 1 - {@link #getRatio()}.
     *
     * @return the reverse ratio
     */
    public float getReverseRatio() {
        return duration > 0f ? Math.max(0f, 1f - (time / duration)) : 0f;
    }

    /**
     * Jump to beginning (sets time to zero).
     */
    public void jumpToBeginning() {
        time = 0f;
    }

    /**
     * Jump to end (sets {@link #time} to {@link #duration}).
     */
    public void jumpToEnd() {
        time = duration;
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
        for (Marker marker : markers.values()) {
            marker.wasSurpassed = marker.isSurpassed;
            marker.isSurpassed = time >= marker.time;
        }
        justFinished = !finishedBefore && isFinished();
    }

    @Override
    public void reset() {
        time = 0f;
    }

    private static class Marker {

        private final float time;
        private boolean isSurpassed = false;
        private boolean wasSurpassed = false;

        private Marker(Float time) {
            this.time = time;
        }

    }

}