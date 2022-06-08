package com.game.world;

/**
 * Interface for handling contacts.
 */
public interface ContactListener {

    /**
     * Listen to contact with {@link FixtureType}.
     *
     * @param fixtureType the fixture type
     * @param delta       the delta time
     */
    void listen(FixtureType fixtureType, float delta);

}
