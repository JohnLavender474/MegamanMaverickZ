package com.mygdx.game.world;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContactTest {

    @Test
    public void acceptMask1() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.HEAD);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Contact contact = new Contact(fixture1, fixture2);
        // then
        assertTrue(contact.acceptMask(FixtureType.HEAD, FixtureType.FEET));
        assertEquals(fixture1, contact.getMask().first());
        assertNotEquals(fixture2, contact.getMask().first());
        assertEquals(fixture2, contact.getMask().second());
        assertNotEquals(fixture1, contact.getMask().second());
    }

    @Test
    public void acceptMask2() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.FEET);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Contact contact = new Contact(fixture1, fixture2);
        // then
        assertTrue(contact.acceptMask(FixtureType.FEET, FixtureType.FEET));
        assertEquals(fixture1, contact.getMask().first());
        assertNotEquals(fixture2, contact.getMask().first());
        assertEquals(fixture2, contact.getMask().second());
        assertNotEquals(fixture1, contact.getMask().second());
    }

    @Test
    public void rejectMask1() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.HEAD);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Contact contact = new Contact(fixture1, fixture2);
        // then
        assertFalse(contact.acceptMask(FixtureType.FEET, FixtureType.FEET));
    }

    @Test
    public void rejectMask2() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.FEET);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Contact contact = new Contact(fixture1, fixture2);
        // then
        assertFalse(contact.acceptMask(FixtureType.FEET, FixtureType.HEAD));
    }

    @Test
    public void equality1() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.HEAD);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Contact contact1 = new Contact(fixture1, fixture2);
        Contact contact2 = new Contact(fixture2, fixture1);
        // then
        assertEquals(contact1, contact2);
    }

    @Test
    public void equality2() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.FEET);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Contact contact1 = new Contact(fixture1, fixture2);
        Contact contact2 = new Contact(fixture2, fixture1);
        // then
        assertEquals(contact1, contact2);
    }

    @Test
    public void inequality1() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.FEET);
        Fixture fixture2 = new Fixture(FixtureType.HEAD);
        Fixture fixture3 = new Fixture(FixtureType.PROJECTILE);
        Contact contact1 = new Contact(fixture1, fixture2);
        Contact contact2 = new Contact(fixture1, fixture3);
        Contact contact3 = new Contact(fixture2, fixture3);
        // then
        assertNotEquals(contact1, contact2);
        assertNotEquals(contact2, contact3);
        assertNotEquals(contact1, contact3);
    }

    @Test
    public void inequality2() {
        // given
        Fixture fixture1 = new Fixture(FixtureType.FEET);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        Fixture fixture3 = new Fixture(FixtureType.FEET);
        Contact contact1 = new Contact(fixture1, fixture2);
        Contact contact2 = new Contact(fixture1, fixture3);
        Contact contact3 = new Contact(fixture2, fixture3);
        // then
        assertNotEquals(contact1, contact2);
        assertNotEquals(contact2, contact3);
        assertNotEquals(contact1, contact3);
    }

}