package com.game.animations;

import com.game.Component;
import com.game.System;
import com.game.core.IEntity;
import com.game.sprites.SpriteComponent;

import java.util.Set;

public class AnimationSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SpriteComponent.class, AnimationComponent.class);
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        animationComponent.animator().animate(spriteComponent.getSprite(), delta);
    }

}
