package com.game.screens.levels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.game.utils.Drawable;
import lombok.Setter;

/**
 * Container class for ui gameplay health bar.
 */
@Setter
public class HealthBarUi implements Drawable {

    private final Camera camera;
    private final Sprite containerSprite = new Sprite();
    private final Sprite healthBitSprite = new Sprite();

    private int currentHealthBits;

    /**
     * Instantiates a new Health bar.
     *
     * @param containerRegion the texture region of the container
     * @param containerBounds the bounds of the health bar container
     * @param healthBitRegion the region of the individual health bit
     * @param maxHealthBits   the max health bits the container can contain
     */
    public HealthBarUi(Camera camera, TextureRegion containerRegion, Rectangle containerBounds,
                       TextureRegion healthBitRegion, int maxHealthBits) {
        this.camera = camera;
        containerSprite.setRegion(containerRegion);
        containerSprite.setBounds(containerBounds.x, containerBounds.y,
                                  containerBounds.width, containerBounds.height);
        healthBitSprite.setRegion(healthBitRegion);
        healthBitSprite.setX(containerBounds.x);
        healthBitSprite.setSize(containerBounds.width, containerBounds.height / (float) maxHealthBits);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.setProjectionMatrix(camera.combined);
        boolean isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
        containerSprite.draw(spriteBatch);
        for (int i = 0; i < currentHealthBits; i++) {
            healthBitSprite.setY(containerSprite.getY() + (i * healthBitSprite.getHeight()));
            healthBitSprite.draw(spriteBatch);
        }
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

}
