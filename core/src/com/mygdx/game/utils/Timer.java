package com.mygdx.game.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Timer implements Updatable, Resettable {

    private float time = 0f;
    private float duration = 0f;
    private boolean justFinished = false;
    private final Map<String, Marker> markers = new HashMap<>();

    public Timer(float duration) {
        setDuration(duration);
    }

    public boolean isMarkerSurpassed(String key) {
        Marker marker = markers.get(key);
        return marker != null && marker.isSurpassed;
    }

    public boolean isMarkerJustSurpassed(String key) {
        Marker marker = markers.get(key);
        return marker != null && marker.isSurpassed && !marker.wasSurpassed;
    }

    public float getRatio() {
        return duration > 0f ? Math.min(time / duration, 1f) : 0f;
    }

    public float getReverseRatio() {
        return duration > 0f ? Math.max(0f, 1f - (time / duration)) : 0f;
    }

    public void jumpToBeginning() {
        setTime(0f);
    }

    public void jumpToEnd() {
        setTime(duration);
    }

    public boolean isAtBeginning() {
        return time == 0f;
    }

    public boolean isFinished() {
        return time >= duration;
    }

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
        setTime(0f);
    }

    public static class Marker {

        public float time;
        public boolean isSurpassed = false;
        public boolean wasSurpassed = false;

        public Marker(Float time) {
            this.time = time;
        }

    }

}