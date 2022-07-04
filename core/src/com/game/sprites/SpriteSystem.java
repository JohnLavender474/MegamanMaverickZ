package com.game.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.core.IEntity;
import com.game.System;
import com.game.utils.Position;
import com.game.utils.UtilMethods;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * {@link System} implementation for rendering and optionally animating {@link Sprite} instances.
 */
@RequiredArgsConstructor
public class SpriteSystem extends System {

    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;
    private boolean isDrawing;

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SpriteComponent.class);
    }

    @Override
    protected void preProcess(float delta) {
        spriteBatch.setProjectionMatrix(camera.combined);
        isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        Sprite sprite = spriteComponent.getSprite();
        SpriteAdapter spriteAdapter = spriteComponent.getSpriteAdapter();
        if (spriteAdapter != null) {
            Rectangle boundingBox = spriteAdapter.getBoundingBox();
            Position position = spriteAdapter.getPosition();
            if (boundingBox != null && position != null) {
                Vector2 point = UtilMethods.getPoint(boundingBox, position);
                UtilMethods.setToPoint(sprite.getBoundingRectangle(), point, position, sprite::setPosition);
            }
            sprite.setAlpha(spriteAdapter.isHidden() ? 0f : spriteAdapter.getAlpha());
            sprite.setFlip(spriteAdapter.isFlipX(), spriteAdapter.isFlipY());
            sprite.translate(spriteAdapter.getOffsetX(), spriteAdapter.getOffsetY());
        }
        Texture texture = sprite.getTexture();
        if (texture != null) {
            texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            sprite.draw(spriteBatch);
        }
    }

    @Override
    protected void postProcess(float delta) {
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

}
