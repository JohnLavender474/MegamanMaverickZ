package com.game.damage;

import com.game.core.Entity;

import java.util.Set;

/**
 * Interface for {@link Entity} instances that are able to be damaged.
 */
public interface Damageable {

    /**
     * DamagerDef mask setBounds.
     *
     * @return the setBounds
     */
    Set<Class<? extends Damager>> getDamagerMaskSet();

    /**
     * Take damage from the damager.
     *
     * @param damager the damager
     */
    void takeDamageFrom(Damager damager);

    /**
     * Is invincible.
     *
     * @return is invincible
     */
    default boolean isInvincible() {
        return false;
    }

    /**
     * Can be damaged by the damager. Returns true if {@link #isInvincible()} is false and {@link #getDamagerMaskSet()}
     * contains the supplied {@link Damager}.
     *
     * @param damager the damager
     * @return can be damaged by the damager
     */
    default boolean canBeDamagedBy(Damager damager) {
        return !isInvincible() && getDamagerMaskSet().contains(damager.getClass());
    }

}
