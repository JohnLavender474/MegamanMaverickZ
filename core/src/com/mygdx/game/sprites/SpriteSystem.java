package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import com.mygdx.game.MegamanMaverick;
import com.mygdx.game.System;
import com.mygdx.game.utils.UtilMethods;
import com.mygdx.game.utils.exceptions.InvalidActionException;

import java.util.Set;

/**
 * {@link System} implementation for rendering and optionally animating {@link Sprite} instances.
 */
public class SpriteSystem extends System {

    public SpriteSystem(MegamanMaverick megamanMaverick)
            throws InvalidActionException {
        super(megamanMaverick);
    }

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SpriteComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        for (SpriteHandle spriteHandle : spriteComponent.getSpriteHandles().values()) {
            Sprite sprite = spriteHandle.getSprite();
            Vector2 center = UtilMethods.centerPoint(entity.getBoundingBox());
            sprite.setCenter(center.x, center.y);
            sprite.setFlip(spriteHandle.isFlipX(), spriteHandle.isFlipY());
            sprite.setAlpha(spriteHandle.isHidden() ? 0f : spriteHandle.getAlpha());
            sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
            sprite.translate(spriteHandle.getOffset().x, spriteHandle.getOffset().y);
            SpriteAnimator spriteAnimator = spriteHandle.getSpriteAnimator();
            if (spriteAnimator != null) {
                spriteAnimator.animate(sprite, delta);
            }
            megamanMaverick.getRenderables().get(spriteHandle.getRenderingGround()).add(spriteHandle);
        }
    }

}
