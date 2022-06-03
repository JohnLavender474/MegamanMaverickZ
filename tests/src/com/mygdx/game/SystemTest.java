package com.mygdx.game;

import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.System;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.Assert.*;

public class SystemTest {

    private static class TestSystem extends System {

        public float p = 0;

        @Override
        public Set<Class<? extends Component>> getComponentMask() {
            return Set.of(Component1.class, Component2.class);
        }

        @Override
        protected void processEntity(Entity entity, float delta) {
            Component1 component1 = entity.getComponent(Component1.class);
            component1.b = !component1.b;
            if (component1.b) {
                Component2 component2 = entity.getComponent(Component2.class);
                component2.i++;
            }
        }

        @Override
        protected void preProcess(float delta) {
            p += delta;
        }

        @Override
        protected void postProcess(float delta) {
            p += delta;
        }

    }

    private static class Component1 implements Component {
        public boolean b = false;
    }
    private static class Component2 implements Component {
        public int i = 0;
    }
    private static class Component3 implements Component {}

    private Entity entity;
    private TestSystem testSystem;

    @Before
    public void setUp() {
        entity = new Entity();
        testSystem = new TestSystem();
    }

    @Test
    public void entityQualifiesMembership() {
        // given
        entity.addComponent(new Component1());
        entity.addComponent(new Component2());
        entity.addComponent(new Component3());
        // when
        boolean bool = testSystem.qualifiesMembership(entity);
        // then
        assertTrue(bool);
    }

    @Test
    public void entityDoesNotQualityMembership() {
        // given
        entity.addComponent(new Component2());
        entity.addComponent(new Component3());
        // when
        boolean bool = testSystem.qualifiesMembership(entity);
        // then
        assertFalse(bool);
    }

    @Test
    public void processEntity() {
        // given
        entity.addComponent(new Component1());
        entity.addComponent(new Component2());
        entity.addComponent(new Component3());
        testSystem.addEntity(entity);
        // when
        testSystem.update(1f);
        float p = testSystem.p;
        int i = entity.getComponent(Component2.class).i;
        boolean b = entity.getComponent(Component1.class).b;
        // then
        assertEquals(2f, p, 0f);
        assertEquals(1, i);
        assertTrue(b);
    }

    @Test
    public void entityQueuedToBeAdded() {
        // given
        entity.addComponent(new Component1());
        entity.addComponent(new Component2());
        entity.addComponent(new Component3());
        TestSystem spyTestSystem = Mockito.spy(testSystem);
        Mockito.doReturn(true).when(spyTestSystem).isUpdating();
        // when
        spyTestSystem.addEntity(entity);
        // then
        assertFalse(spyTestSystem.entityIsMember(entity));
        assertTrue(spyTestSystem.entityIsQueuedForMembership(entity));
    }

    @Test
    public void entityQueuedToBeDeleted() {
        // given
        entity.addComponent(new Component1());
        entity.addComponent(new Component2());
        entity.addComponent(new Component3());
        TestSystem spyTestSystem = Mockito.spy(testSystem);
        spyTestSystem.addEntity(entity);
        Mockito.doReturn(true).when(spyTestSystem).isUpdating();
        // when
        spyTestSystem.removeEntity(entity);
        // then
        assertTrue(spyTestSystem.entityIsMember(entity));
        assertTrue(spyTestSystem.entityIsQueuedForRemoval(entity));
    }

}
