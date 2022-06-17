package com.game.entities;

/**
 * Interface for {@link Entity} instances that inflict damage.
 */
public interface Damager {

    /**
     * Returns true if the {@link Damageable} can be damaged.
     *
     * @param damageable the damageable
     * @return if the Entity can be damaged
     */
    boolean canDamage(Damageable damageable);

    /**
     * Called when {@link Damageable#takeDamageFrom(Damager)} has also been called. This method should not
     * be used to inflict damage but rather to specify an action that is performed by the Damager when
     * damage has been inflicted.
     *
     * @param damageable the damageable
     */
    void onDamageInflicted(Damageable damageable);

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
