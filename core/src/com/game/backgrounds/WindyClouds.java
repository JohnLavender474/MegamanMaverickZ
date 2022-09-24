package com.game.backgrounds;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.core.IAssetLoader;

import static com.game.constants.TextureAsset.*;
import static com.game.constants.ViewVals.PPM;

/**
 * Windy clouds background.
 */
public class WindyClouds extends Background {

    public static final String WINDY_CLOUDS = "WindyClouds";

    private static final float WIDTH = 10;
    private static final float HEIGHT = 20;
    private static final float DURATION = 45f;

    private float dist;

    public WindyClouds(IAssetLoader assetLoader, RectangleMapObject backgroundObj) {
        this(assetLoader, backgroundObj.getRectangle().x, backgroundObj.getRectangle().y,
                backgroundObj.getProperties().get(ROWS_KEY, Integer.class),
                backgroundObj.getProperties().get(COLS_KEY, Integer.class));
    }

    public WindyClouds(IAssetLoader assetLoader, float startX, float startY, int rows, int cols) {
        super(assetLoader.getAsset(BACKGROUNDS_1.getSrc(), TextureAtlas.class).findRegion("BKG04"),
                startX, startY, WIDTH, HEIGHT, rows, cols);
    }

    @Override
    public void update(float delta) {
        float trans = WIDTH * PPM * delta / DURATION;
        translate(-trans, 0f);
        dist += trans;
        if (dist >= WIDTH * PPM) {
            resetPositions();
            dist = 0f;
        }
    }

}
