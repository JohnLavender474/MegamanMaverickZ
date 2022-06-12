package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.megaman.behaviors.MegamanRun;
import lombok.RequiredArgsConstructor;

import static com.game.ConstVals.ViewVals.*;

/**
 * Simply tests that each grid rectangle is 32 x 32 pixels and moves in any direction at speed
 * {@link com.game.megaman.behaviors.MegamanRun#RUN_SPEED} world units per second.
 */
@RequiredArgsConstructor
public class TestPPMAndMovementSpeedsScreen extends ScreenAdapter {

    private final Viewport viewport = new FitViewport(
            VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private Sprite badlogicSprite;
    private Rectangle[][] grid;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        badlogicSprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        badlogicSprite.setBounds(0f, 0f, PPM, PPM);
        grid = new Rectangle[50][50];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Rectangle(i * PPM, j * PPM, PPM, PPM);
            }
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            badlogicSprite.translateX(-PPM * MegamanRun.RUN_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            badlogicSprite.translateX(PPM * MegamanRun.RUN_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            badlogicSprite.translateY(PPM * MegamanRun.RUN_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            badlogicSprite.translateY(-PPM * MegamanRun.RUN_SPEED * delta);
        }
        viewport.getCamera().position.x = badlogicSprite.getX();
        viewport.getCamera().position.y = badlogicSprite.getY();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeType.Line);
        for (Rectangle[] row : grid) {
            for (Rectangle rect : row) {
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }
        }
        shapeRenderer.end();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        badlogicSprite.draw(spriteBatch);
        spriteBatch.end();
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

}
