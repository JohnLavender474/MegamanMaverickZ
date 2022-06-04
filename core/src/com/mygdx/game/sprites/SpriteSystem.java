package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ConstVals.RenderingGround;
import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.System;
import com.mygdx.game.utils.UtilMethods;

import java.util.*;

/**
 * {@link System} implementation for rendering and optionally animating {@link Sprite} instances.
 */
public class SpriteSystem extends System {

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Map<RenderingGround, Queue<Sprite>> sprites = new EnumMap<>(RenderingGround.class);

    public SpriteSystem(OrthographicCamera camera, SpriteBatch spriteBatch) {
        this.camera = camera;
        this.spriteBatch = spriteBatch;
        for (RenderingGround renderingGround : RenderingGround.values()) {
            sprites.put(renderingGround, new ArrayDeque<>());
        }
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
            sprites.get(spriteHandle.getRenderingGround()).add(sprite);
        }
    }

    @Override
    protected void postProcess(float delta) {
        spriteBatch.setProjectionMatrix(camera.combined);
        boolean isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
        for (RenderingGround renderingGround : RenderingGround.values()) {
            Queue<Sprite> spriteQueue = sprites.get(renderingGround);
            while (!spriteQueue.isEmpty()) {
                Sprite sprite = spriteQueue.poll();
                sprite.draw(spriteBatch);
            }
        }
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

}
