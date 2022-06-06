package com.game.screens.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Defines an object that can be followed by the camera managed by {@link LevelCameraManager}.
 */
public interface LevelCameraFocusable {

    /**
     * Gets bounding box of object.
     *
     * @return the bounding box
     */
    Rectangle getBoundingBox();

    /**
     * Gets focus point of object.
     *
     * @return the focus
     */
    default Vector2 getFocus() {
        Vector2 center = new Vector2();
        getBoundingBox().getCenter(center);
        return center;
    }

}
