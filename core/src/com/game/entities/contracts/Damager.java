package com.game.entities.contracts;

import com.game.entities.Entity;

import java.util.Set;

/**
 * Interface for {@link Entity} instances that are able to inflict damage.
 */
public interface Damager {

    /**
     * Damageable mask set that defines which objects can be damaged by this instance.
     *
     * @param <T> Bounds the type to {@link Entity} and {@link Damageable}
     * @return the damageable mask set
     */
    <T extends Damageable> Set<Class<? extends T>> getDamageableMaskSet();

    /**
     * On damage inflicted to.
     *
     * @param <T>             the type parameter
     * @param damageableClass the damageable class
     */
    <T extends Damageable> void onDamageInflictedTo(Class<T> damageableClass);

    /**
     * Is able to damage.
     *
     * @param damageable the damageable
     * @return the if is able to damage the object
     */
    default boolean isAbleToDamage(Damageable damageable) {
        return !damageable.isInvincible() && getDamageableMaskSet().contains(damageable.getClass());
    }

}
