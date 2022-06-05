package com.mygdx.game.screens.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public interface GameCameraFocusable {

    Rectangle getBoundingBox();

    default Vector2 getFocus() {
        Vector2 center = new Vector2();
        getBoundingBox().getCenter(center);
        return center;
    }

}
