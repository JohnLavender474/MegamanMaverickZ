package com.game.world;

/**
 * Implementation of {@link WorldContactListener}.
 */
public class WorldContactListenerImpl implements WorldContactListener {

    @Override
    public void beginContact(Contact contact, float delta) {
        if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
            contact.getMask().first().getBodyComponent().setIs(BodySense.FEET_ON_GROUND);
        } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
            contact.getMask().first().getBodyComponent().setIs(BodySense.HEAD_TOUCHING_BLOCK);
        }
    }

    @Override
    public void continueContact(Contact contact, float delta) {}

    @Override
    public void endContact(Contact contact, float delta) {
        if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
            contact.getMask().first().getBodyComponent().setIsNot(BodySense.FEET_ON_GROUND);
        } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
            contact.getMask().first().getBodyComponent().setIsNot(BodySense.HEAD_TOUCHING_BLOCK);
        }
    }

}
