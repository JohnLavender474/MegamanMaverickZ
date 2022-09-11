package com.game.animations;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.utils.interfaces.Resettable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.KeyValuePair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines an animation where each {@link TextureRegion} has a setBounds duration. The constructors of this class are
 * compatible only with {@link TextureRegion} instances consisting of one row. {@link #isLoop()} is true by default.
 */
@Getter
@Setter
public class TimedAnimation implements Updatable, Resettable {

    private final List<TextureRegion> frames = new ArrayList<>();
    private final List<Float> frameTimes = new ArrayList<>();

    private float animationDuration;
    private TextureRegion currentT;
    private boolean loop = true;
    private boolean isFinished;
    private float timeElapsed;

    /**
     * Copies all fields of the supplied timed animation to this. Can be reversed if desired.
     *
     * @param timedAnimation the timed animation to copy
     * @param reverse        if this should be the reverse of the supplied animation
     */
    public TimedAnimation(TimedAnimation timedAnimation, boolean reverse) {
        frames.addAll(timedAnimation.getFrames());
        frameTimes.addAll(timedAnimation.getFrameTimes());
        animationDuration = timedAnimation.getAnimationDuration();
        timeElapsed = timedAnimation.getTimeElapsed();
        isFinished = timedAnimation.isFinished();
        currentT = timedAnimation.getCurrentT();
        loop = timedAnimation.isLoop();
        if (reverse) {
            reverse();
        }
    }

    /**
     * See {@link #TimedAnimation(TimedAnimation, boolean)}.
     *
     * @param timedAnimation the timed animation to copy
     */
    public TimedAnimation(TimedAnimation timedAnimation) {
        this(timedAnimation, false);
    }

    /**
     * Instantiates a new Timed animation. Default number of frames is 1, default duration is 1 second.
     *
     * @param textureRegion the texture region
     */
    public TimedAnimation(TextureRegion textureRegion) {
        this(textureRegion, 1, 1f);
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param textureRegion the texture region
     * @param durations     the durations
     */
    public TimedAnimation(TextureRegion textureRegion, float[] durations) {
        this(textureRegion, durations, true);
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param textureRegion the texture region
     * @param durations     the durations
     * @param loop          if loop
     */
    public TimedAnimation(TextureRegion textureRegion, float[] durations, boolean loop) {
        this.loop = loop;
        instantiate(textureRegion, durations);
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param textureRegion the texture region
     * @param numFrames     the num frames
     * @param duration      the duration
     */
    public TimedAnimation(TextureRegion textureRegion, int numFrames, float duration) {
        this(textureRegion, numFrames, duration, true);
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param textureRegion the texture region
     * @param numFrames     the num frames
     * @param duration      the duration
     * @param loop          if loop
     */
    public TimedAnimation(TextureRegion textureRegion, int numFrames, float duration, boolean loop) {
        float[] durations = new float[numFrames];
        Arrays.fill(durations, duration);
        instantiate(textureRegion, durations);
        this.loop = loop;
    }

    /**
     * Instantiates a new Timed animation.
     *
     * @param frameTimeKeyValuePairs the frame time key value pairs
     */
    public TimedAnimation(List<KeyValuePair<Float, TextureRegion>> frameTimeKeyValuePairs) {
        instantiate(frameTimeKeyValuePairs);
    }

    /**
     * Static method for instantiating new timed animation.
     *
     * @param textureRegion the texture region
     * @param durations     the durations
     * @return the timed animation
     */
    public static TimedAnimation of(TextureRegion textureRegion, List<Float> durations) {
        int width = textureRegion.getRegionWidth() / durations.size();
        int height = textureRegion.getRegionHeight();
        List<KeyValuePair<Float, TextureRegion>> keyValuePairs = new ArrayList<>();
        for (int i = 0; i < durations.size(); i++) {
            keyValuePairs.add(new KeyValuePair<>(durations.get(i), new TextureRegion(textureRegion, width * i, 0,
                    width, height)));
        }
        return new TimedAnimation(keyValuePairs);
    }

    private void instantiate(TextureRegion textureRegion, float[] durations) {
        int width = textureRegion.getRegionWidth() / durations.length;
        int height = textureRegion.getRegionHeight();
        List<KeyValuePair<Float, TextureRegion>> frameTimeKeyValuePairs = new ArrayList<>();
        for (int i = 0; i < durations.length; i++) {
            frameTimeKeyValuePairs.add(new KeyValuePair<>(durations[i], new TextureRegion(textureRegion, width * i, 0
                    , width, height)));
        }
        instantiate(frameTimeKeyValuePairs);
    }

    private void instantiate(List<KeyValuePair<Float, TextureRegion>> frameTimeKeyValuePairs) {
        frameTimeKeyValuePairs.forEach(frameTimeKeyValuePair -> {
            TextureRegion t = frameTimeKeyValuePair.value();
            Float f = frameTimeKeyValuePair.key();
            animationDuration += f;
            frameTimes.add(f);
            frames.add(t);
        });
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
            timeElapsed = animationDuration;
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

}