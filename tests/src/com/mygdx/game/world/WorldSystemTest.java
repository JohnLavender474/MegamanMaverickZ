package com.mygdx.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entity;
import com.mygdx.game.GdxTestRunner;
import com.mygdx.game.utils.KeyValuePair;
import com.mygdx.game.utils.Pair;
import com.mygdx.game.utils.exceptions.InvalidActionException;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class WorldSystemTest {

    private static final float fixedTimeStep = 1f / 150f;

    private enum ContactStatus {
        END,
        BEGIN,
        CONTINUE
    }

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
            contactPair = new KeyValuePair<>(ContactStatus.END, contact);
        }
        @Override
        public void beginContact(Contact contact, float delta) {
            contactPair = new KeyValuePair<>(ContactStatus.BEGIN, contact);
        }
        @Override
        public void continueContact(Contact contact, float delta) {
            contactPair = new KeyValuePair<>(ContactStatus.CONTINUE, contact);
        }
    }

    private class TestWorldSystem extends WorldSystem {

        public TestWorldSystem(Vector2 gravity, CollisionHandler collisionHandler,
                               ContactListener contactListener, float fixedTimeStep) {
            super(gravity, collisionHandler,
                  contactListener, fixedTimeStep);
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

    private KeyValuePair<ContactStatus, Contact> contactPair;
    private TestCollisionDef testCollisionDef;
    private WorldSystem worldSystem;

    @Before
    public void setUp() {
        CollisionHandler collisionHandler = new TestCollisionHandler();
        ContactListener contactListener = new TestContactListener();
        worldSystem = new WorldSystem(new Vector2(0f, -10f), collisionHandler,
                                      contactListener, fixedTimeStep);
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



}