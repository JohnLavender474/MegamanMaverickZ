package com.game.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.System;
import com.game.core.IEntity;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;

import java.util.Set;

import static com.game.utils.UtilMethods.*;

/**
 * {@link System} implementation for rendering and optionally animating {@link Sprite} instances.
 */
public class SpriteSystem extends System {

    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;

    private boolean isDrawing;

    public SpriteSystem(OrthographicCamera camera, SpriteBatch spriteBatch) {
        super(Set.of(SpriteComponent.class));
        this.camera = camera;
        this.spriteBatch = spriteBatch;
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
            Wrapper<Rectangle> bounds = Wrapper.of(null);
            Wrapper<Position> position = Wrapper.of(null);
            if (spriteAdapter.setPositioning(bounds, position)) {
                if (bounds.getData() == null) {
                    throw new IllegalStateException("SpriteAdapter::setPositioning returns true but the value of " +
                            "Wrapper<Rectangle>::getData is null");
                }
                if (position.getData() == null) {
                    throw new IllegalStateException("SpriteAdapter::setPositioning returns true but the value of " +
                            "Wrapper<Position>::getData is null");
                }
                Vector2 point = getPoint(bounds.getData(), position.getData());
                setToPoint(sprite.getBoundingRectangle(), point, position.getData(), sprite::setPosition);
            }
            sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
            sprite.translate(spriteAdapter.getOffsetX(), spriteAdapter.getOffsetY());
            sprite.setAlpha(spriteAdapter.isHidden() ? 0f : spriteAdapter.getAlpha());
            sprite.setFlip(spriteAdapter.isFlipX(), spriteAdapter.isFlipY());
            sprite.setRotation(spriteAdapter.getRotation());
            spriteAdapter.update(delta);
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
