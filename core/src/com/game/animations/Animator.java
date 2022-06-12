package com.game.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Animates the {@link Sprite} supplied to {@link #animate(Sprite, float)}.
 */
@RequiredArgsConstructor
public class Animator {

    private String currentAnimationKey;
    private final Supplier<String> animationKeySupplier;
    private final Map<String, TimedAnimation> animations;

    /**
     * Animates the supplied {@link Sprite}.
     *
     * @param sprite the sprite
     * @param delta the delta time
     */
    public void animate(Sprite sprite, float delta) {
        String priorAnimationKey = currentAnimationKey;
        currentAnimationKey = animationKeySupplier.get();
        TimedAnimation timedAnimation = animations.get(currentAnimationKey);
        if (timedAnimation == null) {
            return;
        }
        timedAnimation.update(delta);
        sprite.setRegion(timedAnimation.getCurrentT());
        if (priorAnimationKey != null && !currentAnimationKey.equals(priorAnimationKey)) {
            TimedAnimation priorAnimation = animations.get(priorAnimationKey);
            priorAnimation.reset();
        }
    }

}
