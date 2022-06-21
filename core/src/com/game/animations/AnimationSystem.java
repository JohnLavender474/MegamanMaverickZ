package com.game.animations;

import com.game.core.Component;
import com.game.entities.Entity;
import com.game.core.System;
import com.game.sprites.SpriteComponent;

import java.util.Set;

public class AnimationSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SpriteComponent.class, AnimationComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        animationComponent.animator().animate(spriteComponent.getSprite(), delta);
    }

}
