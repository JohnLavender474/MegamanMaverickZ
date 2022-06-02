package com.mygdx.game.animations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.*;
import com.mygdx.game.System;
import com.mygdx.game.Entity;
import com.mygdx.game.sprites.SpriteComponent;
import com.mygdx.game.utils.TimedAnimation;

import java.util.Set;

/**
 * {@link System} implementation that handles animating the {@link Sprite} of {@link SpriteComponent}.
 */
public class AnimationSystem extends System {

    /**
     * Instantiates a new Animation System.
     *
     * @param gameContext the game context
     */
    public AnimationSystem(GameContext gameContext) {
        super(gameContext);
    }

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(AnimationComponent.class, SpriteComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        if (animationComponent.getSoundAndAnimationState() == null) {
            return;
        }
        String currentKey = animationComponent.getCurrentKeySupplier().get();
        TimedAnimation timedAnimation = animationComponent.getAnimations().get(currentKey);
        if (timedAnimation == null) {
            return;
        }
        switch (animationComponent.getSoundAndAnimationState()) {
            case PLAY -> timedAnimation.update(delta);
            case STOP -> timedAnimation.reset();
        }
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        spriteComponent.getSpriteHandle().getSprite().setRegion(timedAnimation.getCurrentT());
        if (!currentKey.equals(animationComponent.getPriorKey())) {
            TimedAnimation priorAnimation = animationComponent.getAnimations().get(animationComponent.getPriorKey());
            if (priorAnimation != null) {
                priorAnimation.reset();
            }
        }
        animationComponent.setPriorKey(currentKey);
    }

}
