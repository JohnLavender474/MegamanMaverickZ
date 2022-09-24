package com.game.backgrounds;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.game.GameContext2d;

import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.*;

public class Stars extends Background {

    private static final float DURATION = 10f;
    private static final float WIDTH = VIEW_WIDTH / 3f;
    private static final float HEIGHT = VIEW_HEIGHT / 4f;

    private float dist;

    public Stars(GameContext2d gameContext, float startX, float startY, int index) {
        super(gameContext.getAsset(STAGE_SELECT.getSrc(), TextureAtlas.class).findRegion("Stars" + index),
                startX, startY, WIDTH, HEIGHT, 1, 6);
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
