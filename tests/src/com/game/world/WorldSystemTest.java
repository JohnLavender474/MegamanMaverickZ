package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.utils.Direction;
import com.game.utils.ProcessState;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class WorldSystemTest {

    private static final float fixedTimeStep = 1f / 120f;

    private WorldSystem worldSystem;
    private boolean flag1;
    private boolean flag2;

    @Before
    public void setUp() {
        flag1 = false;
        flag2 = false;
        worldSystem = new WorldSystem(fixedTimeStep);
    }

    @Test
    public void contactHandling1() {
        // given
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent();
        bodyComponent1.getCollisionBox().setSize(1f, 1f);
        Fixture fixture1 = new Fixture(FixtureType.HEAD);
        fixture1.getFixtureBox().setSize(1f, 1f);
        fixture1.putContactListener(ProcessState.BEGIN, FixtureType.FEET,
                                    () -> flag1 = true);
        bodyComponent1.getFixtures().add(fixture1);
        entity1.addComponent(bodyComponent1);
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent();
        bodyComponent2.getCollisionBox().setSize(1f, 1f);
        Fixture fixture2 = new Fixture(FixtureType.FEET);
        fixture2.getFixtureBox().setSize(1f, 1f);
        fixture2.putContactListener(ProcessState.BEGIN, FixtureType.HEAD,
                                    () -> flag2 = true);
        bodyComponent2.getFixtures().add(fixture2);
        entity2.addComponent(bodyComponent2);
        worldSystem.addEntity(entity1);
        worldSystem.addEntity(entity2);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        assertTrue(flag1);
        assertTrue(flag2);
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
    public void testImpulseAndVelocity() {
        // given
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.setImpulse(new Vector2(0f, 1f));
        bodyComponent.setVelocity(new Vector2(1f, 0f));
        entity.addComponent(bodyComponent);
        worldSystem.addEntity(entity);
        // when
        worldSystem.update(fixedTimeStep);
        worldSystem.update(fixedTimeStep);
        // then
        assertEquals(fixedTimeStep, bodyComponent.getCollisionBox().y, 0.01f);
        assertEquals(2 * fixedTimeStep, bodyComponent.getCollisionBox().x, 0.01f);
    }

    @Test
    public void testImpulseAndVelocityAndGravity() {
        // given
        Entity entity = new Entity();
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
        assertEquals(x, bodyComponent.getCollisionBox().x, 0.01f);
        assertEquals(y, bodyComponent.getCollisionBox().y, 0.01f);
    }

    @Test
    public void testCollisionDirection1() {
        // given
        // define entity 1
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent();
        bodyComponent1.setBodyType(BodyType.STATIC);
        bodyComponent1.getCollisionBox().set(0f, 0f, 1f, 1f);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent();
        bodyComponent2.setBodyType(BodyType.DYNAMIC);
        bodyComponent2.getCollisionBox().set(0.9f, 0f, 1f, 1f);
        entity2.addComponent(bodyComponent2);
        // add entities to world system
        worldSystem.addEntity(entity1);
        worldSystem.addEntity(entity2);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        assertTrue(bodyComponent1.isColliding(Direction.RIGHT));
        assertFalse(bodyComponent1.isColliding(Direction.LEFT));
        assertFalse(bodyComponent1.isColliding(Direction.UP));
        assertFalse(bodyComponent1.isColliding(Direction.DOWN));
        assertTrue(bodyComponent2.isColliding(Direction.LEFT));
        assertFalse(bodyComponent2.isColliding(Direction.RIGHT));
        assertFalse(bodyComponent2.isColliding(Direction.UP));
        assertFalse(bodyComponent2.isColliding(Direction.DOWN));
    }

    @Test
    public void testCollisionDirection2() {
        // given
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent();
        bodyComponent1.setBodyType(BodyType.STATIC);
        bodyComponent1.getCollisionBox().set(0f, 0f, 1f, 1f);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent();
        bodyComponent2.setBodyType(BodyType.DYNAMIC);
        bodyComponent2.getCollisionBox().set(0.5f, 0.9f, 1f, 1f);
        entity2.addComponent(bodyComponent2);
        // add entities to world system
        worldSystem.addEntity(entity1);
        worldSystem.addEntity(entity2);
        // when
        worldSystem.update(fixedTimeStep);
        // then
        assertTrue(bodyComponent1.isColliding(Direction.UP));
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP) {
                assertFalse(bodyComponent1.isColliding(direction));
            }
        }
        assertTrue(bodyComponent2.isColliding(Direction.DOWN));
        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN) {
                assertFalse(bodyComponent2.isColliding(direction));
            }
        }
    }

    @Test
    public void collisionHandling() {
        // given
        // define entity 1
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent();
        bodyComponent1.setBodyType(BodyType.STATIC);
        bodyComponent1.getCollisionBox().set(0f, 0f, 1f, 1f);
        entity1.addComponent(bodyComponent1);
        // define entity 2
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent();
        bodyComponent2.setBodyType(BodyType.DYNAMIC);
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
        assertEquals(y, bodyComponent.getCollisionBox().y, 0.01f);
    }

}