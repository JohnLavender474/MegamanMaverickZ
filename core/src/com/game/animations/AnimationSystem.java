package com.game.animations;

import com.game.entities.Entity;
import com.game.System;
import com.game.sprites.SpriteComponent;

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
