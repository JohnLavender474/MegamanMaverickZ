package com.mygdx.game.sprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Entity;

/**
 * The interface for certain settings of {@link Sprite}.
 */
public interface SpriteSettings {

    /**
     * Returns if the {@link Sprite} should be hidden (i.e., not rendered).
     *
     * @return if the Sprite should be hidden
     */
    default boolean hidden() {
        return false;
    }

    /**
     * Returns if the {@link Sprite} should be flipped on its x-axis.
     *
     * @return if the Sprite should be x-flipped
     */
    default boolean flipX() {
        return false;
    }

    /**
     * Returns if the {@link Sprite} should be flipped on its y-axis.
     *
     * @return if the Sprite should be y-flipped
     */
    default boolean flipY() {
        return false;
    }

    /**
     * Returns amount of x offset for {@link Sprite#getX()} in relation to center of {@link Entity#getBoundingBox()}.
     *
     * @return the x offset
     */
    default float translateOffsetX() {
        return 0f;
    }

    /**
     * Returns amount of y offset for {@link Sprite#getY()} in relation to center of {@link Entity#getBoundingBox()}.
     *
     * @return the y offset
     */
    default float translateOffsetY() {
        return 0f;
    }

    /**
     * Returns alpha value for {@link Sprite#setAlpha(float)}.
     *
     * @return the alpha value
     */
    default float alpha() {
        return 1.0f;
    }

}
