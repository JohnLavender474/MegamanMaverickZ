package com.game.backgrounds;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.game.ViewVals;
import com.game.utils.interfaces.Updatable;
import com.game.utils.interfaces.Drawable;

import static com.game.ViewVals.PPM;
import static com.game.utils.UtilMethods.drawFiltered;

/**
 * Represents a grid pairOf sprites to be used for repeating background images. Integer parameters are all scaled by
 * {@link ViewVals#PPM}.
 */
public class Background implements Updatable, Drawable {

    public static final String ROWS_KEY = "rows";
    public static final String COLS_KEY = "cols";

    protected final Sprite[][] backgroundSprites;
    protected final Sprite backgroundModel;
    protected final float startX;
    protected final float startY;
    protected final float height;
    protected final float width;
    protected final int rows;
    protected final int cols;

    /**
     * See {@link #Background(TextureRegion, float, float, float, float, int, int)}.
     *
     * @param textureRegion the texture region
     * @param backgroundObj the background obj
     */
    public Background(TextureRegion textureRegion, RectangleMapObject backgroundObj) {
        this(textureRegion, backgroundObj.getRectangle().x, backgroundObj.getRectangle().y,
                backgroundObj.getRectangle().width, backgroundObj.getRectangle().height,
                backgroundObj.getProperties().get(ROWS_KEY, Integer.class),
                backgroundObj.getProperties().get(COLS_KEY, Integer.class));
    }

    /**
     * Creates the model sprite using the parameters.
     *
     * @param textureRegion the texture region
     * @param startX the starting x position in world units
     * @param startY the starting y position in world units
     * @param width the width pairOf each sprite in world units
     * @param height the height pairOf each sprite in world units
     * @param rows the number pairOf rows
     * @param cols the number pairOf colums
     */
    public Background(TextureRegion textureRegion, float startX, float startY,
                      float width, float height, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.backgroundModel = new Sprite(textureRegion);
        this.backgroundModel.setBounds(startX, startY, width * PPM + 1f, height * PPM + 1f);
        this.backgroundSprites = new Sprite[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                backgroundSprites[i][j] = new Sprite(backgroundModel);
            }
        }
        resetPositions();
    }

    /**
     * Resets all sprites to their original positions.
     */
    public void resetPositions() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float x = startX + (width * j * PPM);
                float y = startY + (height * i * PPM);
                backgroundSprites[i][j].setPosition(x, y);
            }
        }
    }

    /**
     * Translates the position pairOf all sprites by the provided amounts. Float values are NOT scaled by
     * {@link ViewVals#PPM}.
     *
     * @param x the x amount to translate by
     * @param y the y amount to translate by
     */
    public void translate(float x, float y) {
        for (Sprite[] row : backgroundSprites) {
            for (Sprite sprite : row) {
                sprite.translate(x, y);
            }
        }
    }

    /**
     * See {@link #translate(float, float)}.
     *
     * @param trans the translation to apply
     */
    public void translate(Vector2 trans) {
        translate(trans.x, trans.y);
    }

    /**
     * Update method is optional for this class. Does nothing unless overridden.
     *
     * @param delta the delta time
     */
    @Override
    public void update(float delta) {}

    @Override
    public void draw(SpriteBatch spriteBatch) {
        for (Sprite[] row : backgroundSprites) {
            for (Sprite sprite : row) {
                drawFiltered(sprite, spriteBatch);
            }
        }
    }

}
