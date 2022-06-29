package com.game.contracts;

import com.game.Entity;

import java.util.Set;

/**
 * Interface for {@link Entity} instances that are able to be damaged.
 */
public interface Damageable {

    /**
     * Damager mask set.
     *
     * @return the set
     */
    Set<Class<? extends Damager>> getDamagerMaskSet();

    /**
     * Take damage from.
     *
     * @param damagerClass the damager class
     */
    void takeDamageFrom(Class<? extends Damager> damagerClass);

    /**
     * Is invincible.
     *
     * @return is invincible
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
