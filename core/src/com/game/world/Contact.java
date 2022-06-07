package com.game.world;

import com.game.utils.Pair;
import com.game.utils.ProcessState;
import lombok.Getter;
import lombok.ToString;

/**
 * Defines contact between two {@link Fixture} instances. Two contact instances are considered if they both
 * contain the same fixture instances, regardless of insertion order.
 */
@Getter
@ToString
public class Contact {

    private final Pair<Fixture> pair;

    /**
     * Instantiates a new Contact.
     *
     * @param f1 the fixture 1
     * @param f2 the fixture 2
     */
    public Contact(Fixture f1, Fixture f2) {
        pair = new Pair<>(f1, f2);
    }

    /**
     * Runs the contact process if any. Each {@link Fixture} is checked if it is listening to contacts with other
     * fixtures with the provided {@link ProcessState} value and the value of {@link Fixture#getFixtureType()} of
     * the other fixture. If the case, then the fixture's contact listener {@link Runnable} is run. Otherwise,
     * nothing happens.
     *
     * @param processState the process state
     */
    public void run(ProcessState processState) {
        if (pair.first().isListeningForContact(processState, pair.second().getFixtureType())) {
            pair.first().runContactListener(processState, pair.second().getFixtureType());
        }
        if (pair.second().isListeningForContact(processState, pair.first().getFixtureType())) {
            pair.second().runContactListener(processState, pair.first().getFixtureType());
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Contact contact &&
                pair.equals(contact.getPair());
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

}
