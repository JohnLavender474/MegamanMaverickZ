package com.game.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.System;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.game.utils.UtilMethods.*;

/**
 * {@link System} implementation for rendering and optionally animating {@link Sprite} instances.
 */
public class SpriteSystem extends System {

    private final Queue<SpriteComponent> spriteComponentQueue = new PriorityQueue<>((o1, o2) -> {
        int p1 = o1.getSpriteAdapter().getSpriteRenderPriority();
        int p2 = o2.getSpriteAdapter().getSpriteRenderPriority();
        return p1 - p2;
    });
    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;

    public SpriteSystem(OrthographicCamera camera, SpriteBatch spriteBatch) {
        super(SpriteComponent.class);
        this.camera = camera;
        this.spriteBatch = spriteBatch;
    }

    @Override
    protected void preProcess(float delta) {
        spriteComponentQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        spriteComponentQueue.add(spriteComponent);
    }

    @Override
    protected void postProcess(float delta) {
        spriteBatch.setProjectionMatrix(camera.combined);
        boolean isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
        while (!spriteComponentQueue.isEmpty()) {
            SpriteComponent spriteComponent = spriteComponentQueue.poll();
            Sprite sprite = spriteComponent.getSprite();
            SpriteAdapter spriteAdapter = spriteComponent.getSpriteAdapter();
            Wrapper<Rectangle> bounds = Wrapper.of(null);
            Wrapper<Position> position = Wrapper.of(null);
            if (spriteAdapter.setPositioning(bounds, position)) {
                if (bounds.getData() == null) {
                    throw new IllegalStateException("SpriteAdapter::setPositioning returns true but the value " +
                            "of Wrapper<Rectangle>::getData is null");
                }
                if (position.getData() == null) {
                    throw new IllegalStateException("SpriteAdapter::setPositioning returns true but the value " +
                            "of Wrapper<Position>::getData is null");
                }
                Vector2 point = getPoint(bounds.getData(), position.getData());
                setToPoint(sprite.getBoundingRectangle(), point, position.getData(), sprite::setPosition);
            }
            Vector2 sizeTrans = spriteAdapter.getSizeTrans();
            sprite.setSize(sprite.getWidth() + sizeTrans.x, sprite.getHeight() + sizeTrans.y);
            Vector2 origin = spriteAdapter.getOrigin(sprite);
            sprite.setOrigin(origin.x, origin.y);
            sprite.translate(spriteAdapter.getOffsetX(), spriteAdapter.getOffsetY());
            sprite.setAlpha(spriteAdapter.isHidden() ? 0f : spriteAdapter.getAlpha());
            sprite.setFlip(spriteAdapter.isFlipX(), spriteAdapter.isFlipY());
            sprite.setRotation(spriteAdapter.getRotation());
            spriteAdapter.update(sprite, delta);
            drawFiltered(sprite, spriteBatch);
        }
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

}
