package com.game.contracts;

import com.game.Entity;

/**
 * Interface for {@link Entity} instances that are able to inflict damage.
 */
public interface Damager {

    /**
     * On damage inflicted to {@link Damageable} instance. This method receives only the class of the instance
     * because this method is not meant to modify the other object but merely for the "reaction" logic of this object.
     *
     * @param damageableClass the damageable class
     */
    default void onDamageInflictedTo(Class<? extends Damageable> damageableClass) {}

}
