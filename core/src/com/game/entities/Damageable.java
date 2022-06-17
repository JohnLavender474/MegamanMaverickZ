package com.game.entities;

/**
 * Interface for {@link Entity} instances that may be damaged by {@link Damager} instances.
 */
public interface Damageable {

    /**
     * Return true if can be damaged by the damager Entity.
     *
     * @param <DE>          the type parameter
     * @param damagerEntity the damager entity
     * @return if can be damaged by the damager Entity
     */
    <DE extends Entity & Damager> boolean canBeDamagedBy(DE damagerEntity);

    /**
     * Take damage from Damager.
     *
     * @param damager the Damager
     */
    void takeDamageFrom(Damager damager);

}
