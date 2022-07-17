package com.game.entities.contracts;

/**
 * Interface that requires the implementor to either be "facing left" or "facing right" but never both at the same time.
 */
public interface Faceable {

    /**
     * Get facing.
     *
     * @return the facing
     */
    Facing getFacing();

    /**
     * Set facing.
     *
     * @param facing the facing
     */
    void setFacing(Facing facing);

    /**
     * Is facing in the provided direction.
     *
     * @param facing the facing
     * @return if facing in the provided direction
     */
    default boolean isFacing(Facing facing) {
        return getFacing() == facing;
    }

    /**
     * Swap facing.
     */
    default void swapFacing() {
        if (getFacing() == Facing.F_RIGHT) {
            setFacing(Facing.F_LEFT);
        } else {
            setFacing(Facing.F_RIGHT);
        }
    }

}
