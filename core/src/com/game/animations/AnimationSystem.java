package com.game.animations;

import com.game.core.Entity;
import com.game.core.System;
import com.game.sprites.SpriteComponent;

import java.util.Set;

public class AnimationSystem extends System {

    public AnimationSystem() {
        super(SpriteComponent.class, AnimationComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        animationComponent.animate(spriteComponent.getSprite(), delta);
    }

}
