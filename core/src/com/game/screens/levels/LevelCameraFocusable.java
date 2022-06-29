package com.game.screens.levels;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Defines an object that can be followed by the camera managed by {@link LevelCameraManager}.
 */
public interface LevelCameraFocusable {

    /**
     * Get bounding box of object.
     *
     * @return the bounding box
     */
    Rectangle getCurrentFocusBox();

    /**
     * Get prior bounding box.
     *
     * @return the prior bounding box
     */
    Rectangle getPriorFocusBox();

    /**
     * Get focus point of object.
     *
     * @return the focus
     */
    default Vector2 getCurrentFocus() {
        Vector2 center = new Vector2();
        getCurrentFocusBox().getCenter(center);
        return center;
    }

    /**
     * Get prior focus.
     *
     * @return the prior focus
     */
    default Vector2 getPriorFocus() {
        Vector2 center = new Vector2();
        getPriorFocusBox().getCenter(center);
        return center;
    }

}
