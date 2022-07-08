package com.game.levels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.ConstVals;
import com.game.GameContext2d;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

/**
 * Container class for ui gameplay bits bar for health or weapon energy.
 */
@Setter
@RequiredArgsConstructor
public class BitsBarUi {

    private final GameContext2d gameContext;
    private final Supplier<Integer> bitsSupplier;
    private final Supplier<TextureRegion> bitSupplier;
    private final Vector2 bitSize;
    private final Rectangle bounds;

    public void draw() {
        Camera camera = gameContext.getViewport(ConstVals.RenderingGround.UI).getCamera();
        // bounds
        ShapeRenderer shapeRenderer = gameContext.getShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        boolean shapeRendererIsDrawing = shapeRenderer.isDrawing();
        if (!shapeRendererIsDrawing) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        if (!shapeRendererIsDrawing) {
            shapeRenderer.end();
        }
        // bits
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);
        boolean spriteBatchIsDrawing = spriteBatch.isDrawing();
        if (!spriteBatchIsDrawing) {
            spriteBatch.begin();
        }
        for (int i = 0; i < bitsSupplier.get(); i++) {
            spriteBatch.draw(bitSupplier.get(), bounds.x, bounds.y + (i * bitSize.y), bitSize.x, bitSize.y);
        }
        if (!spriteBatchIsDrawing) {
            spriteBatch.end();
        }
    }

}
