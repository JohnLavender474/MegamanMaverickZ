package com.mygdx.game.screens.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public interface Focusable {
    Vector2 focus();
    Rectangle boundingBox();
}
