package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.utils.KeyValuePair;
import com.game.utils.Pair;
import com.game.utils.ProcessState;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class WorldSystemTest {

    private static final float fixedTimeStep = 1f / 120f;

    private class TestWorldContactListener implements WorldContactListener {
        @Override
        public void endContact(Contact contact, float delta) {
            testContactPair = new KeyValuePair<>(ProcessState.END, contact);
        }
        @Override
        public void continueContact(Contact contact, float delta) {

        }
        @Override
        public void beginContact(Contact contact, float delta) {
            testContactPair = new KeyValuePair<>(ProcessState.BEGIN, contact);
        }
    }

    private KeyValuePair<ProcessState, Contact> testContactPair;
    private WorldSystem worldSystem;

    @Before
    public void setUp() {
        testContactPair = null;
        worldSystem = new WorldSystem(new TestWorldContactListener(), fixedTimeStep);
    }

    @Test
    public void testAcceptsEntity() {
        // given
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        entity.addComponent(bodyComponent);
        // then
        assertTrue(worldSystem.qualifiesMembership(entity));
    }

    @Test
    public void testImpulseAndVelocity() {
        // given
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.getImpulse().set(0f, 1f);
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        worldSystem.update(fixedTimeStep);
        // then
        assertEquals(fixedTimeStep, bodyComponent.getCollisionBox().y, 0.01f);
    }

    @Test
    public void testImpulseAndVelocityAndGravity() {
        // given
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.getGravity().set(0f, -10f);
        bodyComponent.getImpulse().set(0f, 25f);
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        worldSystem.update(fixedTimeStep);
        // then
        // impulse of 25f scaled by fixedTimeStep, once, plus y gravity twice also scaled by fixedTimeStep
        float y = (25f  * fixedTimeStep) + (2f * -10f * fixedTimeStep);
        assertEquals(y, bodyComponent.getCollisionBox().y, 0.01f);
    }

    @Test
    public void collisionHandling() {
        // given
        // define entity 1
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent(BodyType.STATIC);
        bodyComponent1.getCollisionBox().set(0f, 0f, 1f, 1f);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent2.getCollisionBox().set(0.9f, 0f, 1f, 1f);
        entity2.addComponent(bodyComponent2);
        // add entities to world system
        worldSystem.addEntity(entity1);
        worldSystem.addEntity(entity2);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        assertEquals(new Rectangle(0f, 0f, 1f, 1f),
                     bodyComponent1.getCollisionBox());
        assertEquals(new Rectangle(1f, 0f, 1f, 1f),
                     bodyComponent2.getCollisionBox());
    }

    @Test
    public void frictionScalar() {
        // given
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.getCollisionBox().setPosition(0f, 0f);
        bodyComponent.getFrictionScalar().set(0.35f, 0.85f);
        bodyComponent.getGravity().set(new Vector2(0f, -10f));
        bodyComponent.getImpulse().set(new Vector2(5f, 0f));
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        float x = fixedTimeStep * 5f * 0.35f;
        float y = fixedTimeStep * -10f * 0.85f;
        assertEquals(x, bodyComponent.getCollisionBox().x, 0.01f);
        assertEquals(y, bodyComponent.getCollisionBox().y, 0.01f);
    }

    @Test
    public void contactHandling() {
        // given
        // define entity 1
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent(BodyType.STATIC);
        bodyComponent1.getCollisionBox().setSize(1f, 1f);
        Fixture fixture1 = new Fixture(FixtureType.HEAD);
        fixture1.getFixtureBox().setSize(1f, 1f);
        bodyComponent1.getFixtures().add(fixture1);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent(BodyType.STATIC);
        bodyComponent2.getCollisionBox().setSize(1f, 1f);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        fixture2.getFixtureBox().setSize(1f, 1f);
        bodyComponent2.getFixtures().add(fixture2);
        entity2.addComponent(bodyComponent2);
        // add entities to world system
        worldSystem.addEntity(entity1);
        worldSystem.addEntity(entity2);
        // when
        testContactPair = null;
        worldSystem.update(fixedTimeStep);
        // then
        assertNotNull(testContactPair);
        assertEquals(ProcessState.BEGIN, testContactPair.key());
        Contact contact1 = testContactPair.value();
        assertTrue(contact1.acceptMask(FixtureType.FEET, FixtureType.HEAD));
        Pair<Fixture> mask1 = contact1.getMask();
        assertEquals(fixture2, mask1.first());
        assertEquals(fixture1, mask1.second());
        // when
        bodyComponent1.getCollisionBox().x += 10f;
        bodyComponent2.getCollisionBox().x -= 10f;
        testContactPair = null;
        worldSystem.update(fixedTimeStep);
        // then
        assertNotNull(testContactPair);
        assertEquals(ProcessState.END, testContactPair.key());
        Contact contact3 = testContactPair.value();
        assertTrue(contact3.acceptMask(FixtureType.FEET, FixtureType.HEAD));
        Pair<Fixture> mask3 = contact3.getMask();
        assertEquals(fixture2, mask3.first());
        assertEquals(fixture1, mask3.second());
        // when
        // null test contact pair
        testContactPair = null;
        worldSystem.update(fixedTimeStep);
        // then
        assertNull(testContactPair);
        // when
        bodyComponent1.getCollisionBox().setPosition(1f, .9f);
        bodyComponent2.getCollisionBox().setPosition(1f, 1f);
        testContactPair = null;
        worldSystem.update(fixedTimeStep);
        // then
        assertNotNull(testContactPair);
        assertEquals(ProcessState.BEGIN, testContactPair.key());
        Contact contact4 = testContactPair.value();
        assertTrue(contact4.acceptMask(FixtureType.FEET, FixtureType.HEAD));
        Pair<Fixture> mask4 = contact4.getMask();
        assertEquals(fixture2, mask4.first());
        assertEquals(fixture1, mask4.second());
    }

}