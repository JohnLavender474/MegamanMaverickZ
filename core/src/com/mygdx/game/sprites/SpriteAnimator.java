package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.utils.SoundAndAnimationState;
import com.mygdx.game.utils.TimedAnimation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Animates the supplied {@link Sprite}.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class SpriteAnimator {

    private final Supplier<String> animationKeySupplier;
    private final Map<String, TimedAnimation> animations = new HashMap<>();
    private SoundAndAnimationState soundAndAnimationState = SoundAndAnimationState.PLAY;
    @Setter(AccessLevel.NONE)
    private String priorAnimationKey;

    /**
     * Animates the supplied {@link Sprite}.
     *
     * @param sprite the sprite
     * @param delta the delta time
     */
    public void animate(Sprite sprite, float delta) {
        String currentAnimationKey = animationKeySupplier.get();
        TimedAnimation timedAnimation = animations.get(currentAnimationKey);
        if (timedAnimation == null) {
            return;
        }
        switch (soundAndAnimationState) {
            case PLAY -> timedAnimation.update(delta);
            case STOP -> timedAnimation.reset();
        }
        sprite.setRegion(timedAnimation.getCurrentT());
        if (!currentAnimationKey.equals(priorAnimationKey)) {
            TimedAnimation priorAnimation = animations.get(priorAnimationKey);
            if (priorAnimation != null) {
                priorAnimation.reset();
            }
        }
        priorAnimationKey = currentAnimationKey;
    }

}
