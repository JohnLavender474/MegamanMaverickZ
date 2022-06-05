package com.mygdx.game.world;

import com.mygdx.game.entities.Actor;

import static com.mygdx.game.entities.ActorState.*;
import static com.mygdx.game.world.FixtureType.*;
import static com.mygdx.game.world.FixtureType.WALL_SLIDE_LEFT;

/**
 * Implementation for {@link ContactListener}. WARNING: Numerous times {@link Fixture#getUserData()} undergoes unchecked
 * casting which WILL THROW {@link ClassCastException} if invalid. No call is made to "instance of" to verify class
 * casts on purpose because ideally the implemented design shouldn't need to worry about the exception.
 */
public class ContactListenerImpl implements ContactListener {

    @Override
    public void beginContact(Contact contact, float delta) {
        if (contact.acceptMask(FEET, BLOCK)) {
            ((Actor) contact.getMask().first()).setIs(GROUNDED);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_LEFT)) {
            ((Actor) contact.getMask().first()).setIs(WALL_SLIDING_LEFT);
        }
    }

    @Override
    public void endContact(Contact contact, float delta) {
        if (contact.acceptMask(FEET, BLOCK)) {
            ((Actor) contact.getMask().first()).setIsNot(GROUNDED);
        }
    }

}
