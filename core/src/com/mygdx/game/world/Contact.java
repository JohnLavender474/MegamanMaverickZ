package com.mygdx.game.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Defines the case in which {@link Intersector#intersectRectangles(Rectangle, Rectangle, Rectangle)}, provided with
 * {@link Fixture#getFixtureBox()} of both the {@link Fixture} instances, returns true.
 * <p>
 * {@link #acceptMask(FixtureType, FixtureType)} returns if {@link Fixture#getFixtureType()} of the two fixtures
 * matches the supplied {@link FixtureType} values. If the method returns true, then {@link #mask} is set with the two
 * fixtures in the same order as the supplied FixtureType arguments. Otherwise, the mask pair remains null.
 */
@Getter
@ToString
@RequiredArgsConstructor
public class Contact {

    private final Fixture fixture1;
    private final Fixture fixture2;
    private Pair<Fixture> mask;

    /**
     * Checks if {@link Fixture#getFixtureType()} of {@link #fixture1} and {@link #fixture2} matches the supplied
     * {@link FixtureType} arguments. If so, then return true and set {@link #mask}, otherwise return false and
     * keep the mask pair null.
     *
     * @param fixtureType1 the fixture type 1
     * @param fixtureType2 the fixture type 2
     * @return if the mask is accepted
     */
    public boolean acceptMask(FixtureType fixtureType1, FixtureType fixtureType2) {
        if (acceptFixture1Mask(fixtureType1) && acceptFixture2Mask(fixtureType2)) {
            mask = new Pair<>(fixture1, fixture2);
        } else if (acceptFixture1Mask(fixtureType2) && acceptFixture2Mask(fixtureType1)) {
            mask = new Pair<>(fixture2, fixture1);
        }
        return mask != null;
    }

    private boolean acceptFixture1Mask(FixtureType fixtureType) {
        return fixture1.getFixtureType().equals(fixtureType);
    }

    private boolean acceptFixture2Mask(FixtureType fixtureType) {
        return fixture2.getFixtureType().equals(fixtureType);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Contact contact &&
                ((fixture1.equals(contact.getFixture1()) && fixture2.equals(contact.getFixture2())) ||
                        (fixture1.equals(contact.getFixture2()) && fixture2.equals(contact.getFixture1())));
    }

    @Override
    public int hashCode() {
        return fixture1.hashCode() + fixture2.hashCode();
    }

}
