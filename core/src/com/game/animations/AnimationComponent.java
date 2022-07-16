package com.game.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class AnimationComponent implements Component {

    private final Supplier<String> animationKeySupplier;
    private final Map<String, TimedAnimation> animations;

    @Getter
    private String currentAnimationKey;

    /**
     * Create new animation component with only a single timed animation.
     *
     * @param timedAnimation
     */
    public AnimationComponent(TimedAnimation timedAnimation) {
        this.animations = Map.of("", timedAnimation);
        this.animationKeySupplier = () -> "";
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
