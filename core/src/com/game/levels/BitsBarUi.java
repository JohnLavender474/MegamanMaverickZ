package com.game.levels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.constants.TextureAsset;
import lombok.Setter;

import java.util.function.Supplier;

import static com.game.constants.RenderingGround.*;

/**
 * Container class for ui gameplay bits bar for health or weapon energy.
 */
@Setter
public class BitsBarUi {

    private final GameContext2d gameContext;
    private final Supplier<Integer> bitsSupplier;
    private final Supplier<TextureRegion> bitSupplier;
    private final Rectangle bounds;
    private final Vector2 bitSize;
    private final Sprite black = new Sprite();

    public BitsBarUi(GameContext2d gameContext, Supplier<Integer> bitsSupplier, Supplier<TextureRegion> bitSupplier,
                     Vector2 bitSize, Rectangle bounds) {
        this.gameContext = gameContext;
        this.bitsSupplier = bitsSupplier;
        this.bitSupplier = bitSupplier;
        this.bounds = bounds;
        this.bitSize = bitSize;
        TextureRegion blackRegion = gameContext.getAsset(TextureAsset.DECORATIONS.getSrc(), TextureAtlas.class)
                .findRegion("Black");
        black.setRegion(blackRegion);
        black.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void draw() {
        Camera camera = gameContext.getViewport(UI).getCamera();
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);
        black.draw(spriteBatch);
        for (int i = 0; i < bitsSupplier.get(); i++) {
            spriteBatch.draw(bitSupplier.get(), bounds.x, bounds.y + (i * bitSize.y), bitSize.x, bitSize.y);
        }
    }

}
