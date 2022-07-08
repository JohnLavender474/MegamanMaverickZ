package com.game.entities.contracts;

import com.game.core.IEntity;

/**
 * Interface for {@link IEntity} instances that are able to inflict damage.
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
     * On damage inflicted to {@link Damageable} instance. This method receives only the class of the instance
     * because this method is not meant to modify the other object but merely for the "reaction" logic of this object.
     *
     * @param damageableClass the damageable class
     */
    default void onDamageInflictedTo(Class<? extends Damageable> damageableClass) {
    }

}
