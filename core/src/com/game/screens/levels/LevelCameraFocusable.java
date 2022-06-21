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
    Rectangle getCurrentBoundingBox();

    /**
     * Get prior bounding box.
     *
     * @return the prior bounding box
     */
    Rectangle getPriorBoundingBox();

    /**
     * Get focus point of object.
     *
     * @return the focus
     */
    default Vector2 getFocus() {
        Vector2 center = new Vector2();
        getCurrentBoundingBox().getCenter(center);
        return center;
    }

    /**
     * Get prior focus.
     *
     * @return the prior focus
     */
    default Vector2 getPriorFocus() {
        Vector2 center = new Vector2();
        getPriorBoundingBox().getCenter(center);
        return center;
    }

}
