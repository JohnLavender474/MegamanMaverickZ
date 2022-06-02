package com.mygdx.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entity;
import com.mygdx.game.MegamanMaverick;
import com.mygdx.game.utils.KeyValuePair;
import com.mygdx.game.utils.Pair;
import com.mygdx.game.utils.ProcessState;
import com.mygdx.game.utils.UtilMethods;
import com.mygdx.game.utils.exceptions.InvalidActionException;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class WorldSystemTest {

    private static final float fixedTimeStep = 1f / 120f;

    private record TestCollisionDef(Pair<BodyComponent> bodyComponentPair, Rectangle overlap, float delta) {}

    private class TestCollisionHandler implements CollisionHandler {
        @Override
        public void handleCollision(BodyComponent bc1, BodyComponent bc2, Rectangle overlap, float delta) {
            testCollisionDef = new TestCollisionDef(new Pair<>(bc1, bc2), overlap, delta);
        }
    }

    private class TestContactListener implements ContactListener {
        @Override
        public void endContact(Contact contact, float delta) {
            testContactPair = new KeyValuePair<>(ProcessState.END, contact);
        }
        @Override
        public void beginContact(Contact contact, float delta) {
            testContactPair = new KeyValuePair<>(ProcessState.BEGIN, contact);
        }
        @Override
        public void continueContact(Contact contact, float delta) {
            testContactPair = new KeyValuePair<>(ProcessState.CONTINUE, contact);
        }
    }

    private class TestWorldSystem extends WorldSystem {

        public TestWorldSystem() {
            super(new MegamanMaverick(), new TestCollisionHandler(),
                  new TestContactListener(), fixedTimeStep);
        }

        @Override
        public void processEntity(Entity entity, float delta) {
            super.processEntity(entity, delta);
        }

        @Override
        public void preProcess(float delta) {
            super.preProcess(delta);
        }

        @Override
        public void postProcess(float delta) {
            super.postProcess(delta);
        }

    }

    private KeyValuePair<ProcessState, Contact> testContactPair;
    private TestCollisionDef testCollisionDef;
    private WorldSystem worldSystem;

    @Before
    public void setUp() {
        testContactPair = null;
        testCollisionDef = null;
        worldSystem = new TestWorldSystem();
    }

    @Test
    public void testAcceptsEntity() {
        // given
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent();
        entity.addComponent(bodyComponent);
        // then
        assertTrue(worldSystem.qualifiesMembership(entity));
    }

    @Test
    public void throwsOnAddAttempt() {
        // given
        Entity entity = new Entity();
        // then
        assertThrows(InvalidActionException.class, () -> worldSystem.addEntity(entity));
    }

    @Test
    public void testImpulseAndVelocity() {
        // given
        Entity entity = new Entity();
        entity.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.setImpulse(new Vector2(0f, 1f));
        bodyComponent.setVelocity(new Vector2(1f, 0f));
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        worldSystem.update(fixedTimeStep);
        // then
        assertEquals(fixedTimeStep, entity.getBoundingBox().y, 0.01f);
        assertEquals(2 * fixedTimeStep, entity.getBoundingBox().x, 0.01f);
    }

    @Test
    public void testImpulseAndVelocityAndGravity() {
        // given
        Entity entity = new Entity();
        entity.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.setGravity(new Vector2(0f, -10f));
        bodyComponent.setImpulse(new Vector2(0f, 25f));
        bodyComponent.setVelocity(new Vector2(5f, 0f));
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        worldSystem.update(fixedTimeStep);
        // then
        // velocity of 5f scaled by fixedTimeStep, twice
        float x = 2f * 5f * fixedTimeStep;
        // impulse of 25f scaled by fixedTimeStep, once, plus y gravity twice also scaled by fixedTimeStep
        float y = (25f  * fixedTimeStep) + (2f * -10f * fixedTimeStep);
        assertEquals(x, entity.getBoundingBox().x, 0.01f);
        assertEquals(y, entity.getBoundingBox().y, 0.01f);
    }
    
    @Test
    public void collisionHandling() {
        // given
        // define entity 1
        Entity entity1 = new Entity();
        entity1.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent1 = new BodyComponent();
        bodyComponent1.getCollisionBox().setSize(1f, 1f);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        entity2.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent2 = new BodyComponent();
        bodyComponent2.getCollisionBox().setSize(1f, 1f);
        entity2.addComponent(bodyComponent2);
        // add entities to world system
        worldSystem.addEntity(entity1);
        worldSystem.addEntity(entity2);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        assertNotNull(testCollisionDef);
        assertTrue(testCollisionDef.bodyComponentPair.contains(bodyComponent1));
        assertTrue(testCollisionDef.bodyComponentPair.contains(bodyComponent2));
        assertEquals(1f, testCollisionDef.overlap.width, 0.01f);
        assertEquals(1f, testCollisionDef.overlap.height, 0.01f);
        assertEquals(fixedTimeStep, testCollisionDef.delta, 0.01f);
    }

    @Test
    public void frictionScalar() {
        // given
        Entity entity = new Entity();
        entity.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.getCollisionBox().setPosition(0f, 0f);
        bodyComponent.setFrictionScalar(0.35f, 0.85f);
        bodyComponent.setGravity(new Vector2(0f, -10f));
        bodyComponent.setImpulse(new Vector2(5f, 0f));
        bodyComponent.setVelocity(new Vector2(5f, 0f));
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        float x = fixedTimeStep * 10f * 0.35f;
        float y = fixedTimeStep * -10f * 0.85f;
        assertEquals(x, bodyComponent.getCollisionBox().x, 0.01f);
        assertEquals(x, entity.getBoundingBox().x, 0.01f);
        assertEquals(y, bodyComponent.getCollisionBox().y, 0.01f);
        assertEquals(y, entity.getBoundingBox().y, 0.01f);
    }

    @Test
    public void contactHandling() {
        // given
        // define entity 1
        System.out.println(UtilMethods.longDottedLine());
        Entity entity1 = new Entity();
        entity1.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent1 = new BodyComponent();
        bodyComponent1.getCollisionBox().setSize(1f, 1f);
        Fixture fixture1 = new Fixture(FixtureType.HEAD);
        fixture1.getFixtureBox().setSize(1f, 1f);
        bodyComponent1.getFixtures().add(fixture1);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        entity2.getBoundingBox().setPosition(0f, 0f);
        BodyComponent bodyComponent2 = new BodyComponent();
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
        // move body component 1 away from 2 but still keep them overlapping
        bodyComponent1.getCollisionBox().x += .5f;
        testContactPair = null;
        worldSystem.update(fixedTimeStep);
        // then
        assertNotNull(testContactPair);
        assertEquals(ProcessState.CONTINUE, testContactPair.key());
        Contact contact2 = testContactPair.value();
        assertTrue(contact2.acceptMask(FixtureType.HEAD, FixtureType.FEET));
        Pair<Fixture> mask2 = contact2.getMask();
        assertEquals(fixture1, mask2.first());
        assertEquals(fixture2, mask2.second());
        // when
        // move bodies completely off of each other
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