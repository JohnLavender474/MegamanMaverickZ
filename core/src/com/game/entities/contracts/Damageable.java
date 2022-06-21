package com.game.entities.contracts;

import com.game.entities.Entity;

import java.util.Set;

/**
 * Interface for {@link Entity} instances that are able to be damaged.
 */
public interface Damageable {

    /**
     * Damager mask set.
     *
     * @param <T> the type parameter
     * @return the set
     */
    <T extends Damager> Set<Class<? extends T>> getDamagerMaskSet();

    /**
     * Take damage from.
     *
     * @param <T>          the type parameter
     * @param damagerClass the damager class
     */
    <T extends Damager> void takeDamageFrom(Class<T> damagerClass);

    /**
     * Is invincible boolean.
     *
     * @return the boolean
     */
    boolean isInvincible();

    /**
     * Can be damaged by the damager.
     *
     * @param damager the damager
     * @return can be damaged by the damager
     */
    default boolean canBeDamagedBy(Damager damager) {
        return !isInvincible() && getDamagerMaskSet().contains(damager.getClass());
    }

}
