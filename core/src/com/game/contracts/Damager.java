package com.game.contracts;

/**
 * Interface for {@link Entity} instances that are able to inflict damage.
 */
public interface Damager {

    /**
     * On damage inflicted to.
     *
     * @param damageableClass the damageable class
     */
    void onDamageInflictedTo(Class<? extends Damageable> damageableClass);

}
