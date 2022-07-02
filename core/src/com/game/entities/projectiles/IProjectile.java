package com.game.entities.projectiles;

import com.game.world.Fixture;

/**
 * Interface for any entity representing a projectile.
 */
public interface IProjectile {

    /**
     * Called when the projectile hits a fixture.
     *
     * @param fixture the fixture hit by the projectile
     */
    void hit(Fixture fixture);

}
