package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import com.mygdx.game.GameContext;
import com.mygdx.game.System;
import com.mygdx.game.utils.UtilMethods;
import com.mygdx.game.utils.exceptions.InvalidActionException;

import java.util.Set;

/**
 * {@link System} implementation for rendering {@link Sprite} instances.
 */
public class SpriteSystem extends System {

    public SpriteSystem(GameContext gameContext)
            throws InvalidActionException {
        super(gameContext);
    }

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SpriteComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        Sprite sprite = spriteComponent.getSpriteHandle().getSprite();
        Vector2 center = UtilMethods.centerPoint(entity.getBoundingBox());
        sprite.setCenter(center.x, center.y);
        SpriteSettings spriteSettings = spriteComponent.getSpriteSettings();
        sprite.setFlip(spriteSettings.flipX(), spriteSettings.flipY());
        sprite.setAlpha(spriteSettings.hidden() ? 0f : spriteSettings.alpha());
        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
        sprite.translate(spriteSettings.translateOffsetX(), spriteSettings.translateOffsetY());
        gameContext.getRenderables().get(spriteSettings.renderingGround()).add(spriteComponent.getSpriteHandle());
    }

}
