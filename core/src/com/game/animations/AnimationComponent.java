package com.game.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class AnimationComponent implements Component {

    private final Supplier<String> animationKeySupplier;
    private final Function<String, TimedAnimation> animationFunction;

    @Getter
    private String currentAnimationKey;

    /**
     * Create new animation component with only a single timed animation.
     *
     * @param timedAnimation the timed animation
     */
    public AnimationComponent(TimedAnimation timedAnimation) {
        this.animationKeySupplier = () -> "";
        this.animationFunction = key -> timedAnimation;
    }

    /**
     * Animates the supplied {@link Sprite}.
     *
     * @param sprite the sprite
     * @param delta  the delta time
     */
    public void animate(Sprite sprite, float delta) {
        String priorAnimationKey = currentAnimationKey;
        currentAnimationKey = animationKeySupplier.get();
        TimedAnimation timedAnimation = animationFunction.apply(currentAnimationKey);
        if (timedAnimation == null) {
            return;
        }
        timedAnimation.update(delta);
        sprite.setRegion(timedAnimation.getCurrentT());
        if (priorAnimationKey != null && !currentAnimationKey.equals(priorAnimationKey)) {
            TimedAnimation priorAnimation = animationFunction.apply(priorAnimationKey);
            priorAnimation.reset();
        }
    }

}
