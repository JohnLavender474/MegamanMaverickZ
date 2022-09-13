package com.game.world;

/**
 * Defines the logic pairOf handling {@link Contact} instances.
 */
public interface WorldContactListener {

    /**
     * Begin contact.
     *
     * @param contact the contact
     * @param delta   the delta time
     */
    void beginContact(Contact contact, float delta);

    /**
     * Continue contact.
     *
     * @param contact the contact
     * @param delta   the delta
     */
    void continueContact(Contact contact, float delta);

    /**
     * End contact.
     *
     * @param contact the contact
     * @param delta   the delta time
     */
    void endContact(Contact contact, float delta);

}