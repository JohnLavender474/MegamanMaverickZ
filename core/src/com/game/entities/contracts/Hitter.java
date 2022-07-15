package com.game.entities.contracts;

import com.game.world.Fixture;

/**
 * Interface for any entity representing a projectile.
 */
public interface Hitter {

    /**
     * Called when the projectile hits a fixture.
     *
     * @param fixture the fixture hit by the projectile
     */
    void hit(Fixture fixture);

}
