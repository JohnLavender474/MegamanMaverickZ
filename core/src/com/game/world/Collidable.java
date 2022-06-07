package com.game.world;

import com.game.utils.Direction;

import java.util.Map;

/**
 * The interface Collidable.
 */
public interface Collidable {

    /**
     * Gets collision flags.
     *
     * @return the collision flags
     */
    Map<Direction, Boolean> getCollisionFlags();

    /**
     * Reset collision flags.
     */
    default void resetCollisionFlags() {
        for (Direction direction : Direction.values()) {
            getCollisionFlags().put(direction, false);
        }
    }

    /**
     * Set is colliding in the specified direction.
     *
     * @param direction the direction
     */
    default void setIsColliding(Direction direction) {
        getCollisionFlags().put(direction, true);
    }

    /**
     * Is colliding in the provided direction.
     *
     * @param direction the direction
     * @return is colliding in the provided direction
     */
    default boolean isColliding(Direction direction) {
        return getCollisionFlags().get(direction);
    }

    /**
     * Set colliding left.
     */
    default void setCollidingLeft() {
        setIsColliding(Direction.LEFT);
    }

    /**
     * Set colliding right.
     */
    default void setCollidingRight() {
        setIsColliding(Direction.RIGHT);
    }

    /**
     * Set colliding up.
     */
    default void setCollidingUp() {
        setIsColliding(Direction.UP);
    }

    /**
     * Set colliding down.
     */
    default void setCollidingDown() {
        setIsColliding(Direction.DOWN);
    }

}
