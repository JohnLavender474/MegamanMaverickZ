package com.game.contracts;

import com.game.Entity;

import java.util.Set;

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
