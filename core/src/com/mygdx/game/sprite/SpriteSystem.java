package com.mygdx.game.sprite;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.*;
import com.mygdx.game.System;
import com.mygdx.game.utils.UtilMethods;

import java.util.*;

/**
 * {@link System} implementation for rendering {@link Sprite} instances.
 */
public class SpriteSystem extends System {

    private final SpriteBatch spriteBatch;
    private final Map<RenderingGround, Queue<Sprite>> sprites = new EnumMap<>(RenderingGround.class);
    private final Map<RenderingGround, OrthographicCamera> cameras = new EnumMap<>(RenderingGround.class);

    /**
     * Instantiates a new Sprite system.
     *
     * @param spriteBatch      the sprite batch
     * @param playgroundCamera the playground camera
     * @param backgroundCamera the background camera
     * @param uiCamera         the ui camera
     */
    public SpriteSystem(SpriteBatch spriteBatch, OrthographicCamera playgroundCamera,
                        OrthographicCamera backgroundCamera, OrthographicCamera uiCamera) {
        super(SystemType.SPRITE);
        this.spriteBatch = spriteBatch;
        for (RenderingGround renderingGround : RenderingGround.values()) {
            sprites.put(renderingGround, new ArrayDeque<>());
        }
        cameras.put(RenderingGround.PLAYGROUND, playgroundCamera);
        cameras.put(RenderingGround.BACKGROUND, backgroundCamera);
        cameras.put(RenderingGround.UI, uiCamera);
    }

    @Override
    public Set<GameState> getSwitchOffStates() {
        return Set.of();
    }

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SpriteComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        Sprite sprite = spriteComponent.getSprite();
        Vector2 center = UtilMethods.centerPoint(entity.getBoundingBox());
        sprite.setCenter(center.x, center.y);
        SpriteSettings spriteSettings = spriteComponent.getSpriteSettings();
        sprite.setFlip(spriteSettings.flipX(), spriteSettings.flipY());
        sprite.setAlpha(spriteSettings.hidden() ? 0f : spriteSettings.alpha());
        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
        sprite.translate(spriteSettings.translateOffsetX(), spriteSettings.translateOffsetY());
        sprites.get(spriteComponent.getRenderingGround()).add(sprite);
    }

    @Override
    protected void postProcess(float delta) {
        for (RenderingGround renderingGround : RenderingGround.values()) {
            OrthographicCamera camera = cameras.get(renderingGround);
            spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatch.begin();
            Queue<Sprite> spriteQueue = sprites.get(renderingGround);
            while (!spriteQueue.isEmpty()) {
                Sprite sprite = spriteQueue.poll();
                sprite.draw(spriteBatch);
            }
            spriteBatch.end();
        }
    }

}
