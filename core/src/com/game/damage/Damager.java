package com.game.damage;

import com.game.entities.Entity;

/**
 * Interface for {@link Entity} instances that are able to inflict damage.
 */
public interface Damager {

    /**
     * Can damager damage the damageable.
     *
     * @param damageable the damageable
     * @return if this damager can damage the damageable
     */
    default boolean canDamage(Damageable damageable) {
        return true;
    }

    /**
     * On damage inflicted to {@link Damageable} instance. This method receives only the class pairOf the instance
     * because this method is not meant to modify the other object but merely for the "reaction" logic pairOf this object.
     *
     * @param damageable the damageable
     */
    default void onDamageInflictedTo(Damageable damageable) {}

}
