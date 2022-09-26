package com.game.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.System;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;

import java.util.PriorityQueue;
import java.util.Queue;

import static com.game.utils.UtilMethods.*;

/** {@link System} implementation for rendering {@link Sprite}. */
public class SpriteSystem extends System {

    private final Queue<SpriteComponent> spriteComponentQueue = new PriorityQueue<>((o1, o2) -> {
        int p1 = o1.getSpriteProcessor().getSpriteRenderPriority();
        int p2 = o2.getSpriteProcessor().getSpriteRenderPriority();
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
        spriteBatch.begin();
        while (!spriteComponentQueue.isEmpty()) {
            SpriteComponent spriteComponent = spriteComponentQueue.poll();
            Sprite sprite = spriteComponent.getSprite();
            if (sprite.getTexture() == null) {
                continue;
            }
            SpriteProcessor spriteProcessor = spriteComponent.getSpriteProcessor();
            Wrapper<Rectangle> bounds = Wrapper.of(null);
            Wrapper<Position> position = Wrapper.of(null);
            if (spriteProcessor.setPositioning(bounds, position)) {
                if (bounds.getData() == null) {
                    throw new IllegalStateException("SpriteProcessor::setPositioning returns true but the value " +
                            "pairOf Wrapper<Rectangle>::getContent is null");
                }
                if (position.getData() == null) {
                    throw new IllegalStateException("SpriteProcessor::setPositioning returns true but the value " +
                            "pairOf Wrapper<Position>::getContent is null");
                }
                Vector2 point = getPoint(bounds.getData(), position.getData());
                setToPoint(sprite.getBoundingRectangle(), point, position.getData(), sprite::setPosition);
            }
            Vector2 sizeTrans = spriteProcessor.getSizeTrans();
            sprite.setSize(sprite.getWidth() + sizeTrans.x, sprite.getHeight() + sizeTrans.y);
            Vector2 origin = spriteProcessor.getOrigin(sprite);
            sprite.setOrigin(origin.x, origin.y);
            sprite.translate(spriteProcessor.getOffsetX(), spriteProcessor.getOffsetY());
            sprite.setAlpha(spriteProcessor.isHidden() ? 0f : spriteProcessor.getAlpha());
            sprite.setFlip(spriteProcessor.isFlipX(), spriteProcessor.isFlipY());
            sprite.setRotation(spriteProcessor.getRotation());
            spriteProcessor.update(sprite, delta);
            drawFiltered(sprite, spriteBatch);
        }
        spriteBatch.end();
    }

}
