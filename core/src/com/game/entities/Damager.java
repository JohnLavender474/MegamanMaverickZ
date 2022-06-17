package com.game.entities;

/**
 * Interface for {@link Entity} instances that inflict damage.
 */
public interface Damager {

    /**
     * Returns true if the Entity can be damaged.
     *
     * @param damageableEntity the damageable Entity
     * @return if the Entity can be damaged
     */
    <DE extends Entity & Damageable> boolean canDamage(DE damageableEntity);

    /**
     * Get damage to inflict.
     *
     * @return the damage to inflict
     */
    int getDamage();

    /**
     * Set damage to inflict.
     *
     * @param damage the damage to inflict
     */
    void setDamage(int damage);

}
