package com.game.utils.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface Positional {

    void setPosition(float x, float y);

    default void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

}
