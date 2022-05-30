package com.mygdx.game.world;

/**
 * Defines the logic of handling {@link Contact} instances. Delta time (time between the last frame and the current
 * frame) needs to be supplied.
 */
public interface ContactListener {

    /**
     * End of contact.
     *
     * @param contact the contact
     * @param delta   the delta time
     */
    void endContact(Contact contact, float delta);

    /**
     * Beginning contact.
     *
     * @param contact the contact
     * @param delta   the delta time
     */
    void beginContact(Contact contact, float delta);

    /**
     * Continuing contact.
     *
     * @param contact the contact
     * @param delta   the delta time
     */
    void continueContact(Contact contact, float delta);

}
