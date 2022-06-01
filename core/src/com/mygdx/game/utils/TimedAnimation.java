package com.mygdx.game.utils;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines an animation where each {@link TextureRegion} has a set duration. The constructors of this class are
 * compatible only with {@link TextureRegion} instances consisting of only one row.
 */
@Getter
@Setter
public class TimedAnimation implements Updatable, Resettable {

    private final List<TextureRegion> frames = new ArrayList<>();
    private final List<Float> frameTimes = new ArrayList<>();
    private float animationDuration;
    private TextureRegion currentT;
    private boolean isFinished;
    private float timeElapsed;
    private boolean loop;

    /**
     * Instantiates a new Timed animation.
     *
     * @param frameTimeKeyValuePairs the frame time key value pairs
     */
    public TimedAnimation(List<KeyValuePair<Float, TextureRegion>> frameTimeKeyValuePairs) {
        frameTimeKeyValuePairs.forEach(frameTimeKeyValuePair -> {
            TextureRegion t = frameTimeKeyValuePair.value();
            Float f = frameTimeKeyValuePair.key();
            animationDuration += f;
            frameTimes.add(f);
            frames.add(t);
        });
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param textureRegion the texture region
     * @param numFrames     the num frames
     * @param duration      the duration
     */
    public TimedAnimation(TextureRegion textureRegion, int numFrames, float duration) {
        int width = textureRegion.getRegionWidth() / numFrames;
        int height = textureRegion.getRegionHeight();
        for (int i = 0; i < numFrames; i++) {
            animationDuration += duration;
            frameTimes.add(duration);
            frames.add(new TextureRegion(textureRegion, width * i, 0, width, height));
        }
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param timedAnimation the timed animation
     */
    public TimedAnimation(TimedAnimation timedAnimation) {
        frames.addAll(timedAnimation.getFrames());
        frameTimes.addAll(timedAnimation.getFrameTimes());
        for (float f : frameTimes) {
            animationDuration += f;
        }
        setLoop(timedAnimation.isLoop());
    }

    /**
     * Reverses the animation.
     */
    public void reverse() {
        Collections.reverse(frames);
        Collections.reverse(frameTimes);
    }

    @Override
    public void update(float delta) {
        if (timeElapsed >= animationDuration && !loop) {
            isFinished = true;
        }
        if (!isFinished) {
            timeElapsed += delta;
            float currentLoopDuration = timeElapsed % animationDuration;
            int index = 0;
            while (currentLoopDuration > frameTimes.get(index) && index < frames.size()) {
                currentLoopDuration -= frameTimes.get(index);
                index++;
            }
            currentT = frames.get(index);
        }
    }

    @Override
    public void reset() {
        timeElapsed = 0.0f;
    }

    public static TimedAnimation of(TextureRegion textureRegion, List<Float> durations) {
        int width = textureRegion.getRegionWidth() / durations.size();
        int height = textureRegion.getRegionHeight();
        List<KeyValuePair<Float, TextureRegion>> keyValuePairs = new ArrayList<>();
        for (int i = 0; i < durations.size(); i++) {
            keyValuePairs.add(new KeyValuePair<>(
                    durations.get(i), new TextureRegion(textureRegion, width * i, 0,
                                                        width, height)));
        }
        return new TimedAnimation(keyValuePairs);
    }

}