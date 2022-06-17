package com.game.entities;

/**
 * Interface for {@link Entity} instances that may be damaged by {@link Damager} instances.
 */
public interface Damageable {

    /**
     * Return true if can be damaged by the damager.
     *
     * @param damager the damager
     * @return if can be damaged by the damager Entity
     */
    boolean canBeDamagedBy(Damager damager);

    /**
     * Take damage from Damager.
     *
     * @param damager the Damager
     */
    void takeDamageFrom(Damager damager);

}
