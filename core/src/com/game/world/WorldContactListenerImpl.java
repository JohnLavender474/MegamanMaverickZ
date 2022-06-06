package com.game.world;

import com.game.entities.Actor;

import static com.game.entities.ActorState.*;
import static com.game.world.FixtureType.*;
import static com.game.world.FixtureType.WALL_SLIDE_SENSOR;

/**
 * Implementation for {@link WorldContactListener}. WARNING: Numerous times {@link Fixture#getUserData()} undergoes
 * unchecked casting which WILL THROW {@link ClassCastException} if invalid. No call is made to "instance of" to verify
 * class casts on purpose because ideally the implemented design shouldn't need to worry about the exception.
 */
public class WorldContactListenerImpl implements WorldContactListener {

    @Override
    public void beginContact(Contact contact, float delta) {
        if (contact.acceptMask(FEET, BLOCK)) {
            ((Actor) contact.getMask().first()).setIs(GROUNDED);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            ((Actor) contact.getMask().first()).setIs(WALL_SLIDING_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            ((Actor) contact.getMask().first()).setIs(WALL_SLIDING_RIGHT);
        }
    }

    @Override
    public void endContact(Contact contact, float delta) {
        if (contact.acceptMask(FEET, BLOCK)) {
            ((Actor) contact.getMask().first()).setIsNot(GROUNDED);
        }
    }

}
