package com.game.core;

import com.game.Component;
import com.game.utils.exceptions.ClashException;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class EntityTest {

    private static class Component1 implements Component {}
    private static class Component2 implements Component {}
    private static class Component3 implements Component {}

    private Entity entity;
    private Component component1;
    private Component component2;
    private Component component3;

    @Before
    public void setUp() {
        entity = new Entity();
        component1 = new Component1();
        component2 = new Component2();
        component3 = new Component3();
    }

    @Test
    public void successfullyAddComponents() {
        // given
        entity.addComponent(component1);
        entity.addComponent(component2);
        entity.addComponent(component3);
        // then
        assertTrue(entity.hasAllComponents(
                Set.of(Component1.class, Component2.class, Component3.class)));
    }

    @Test
    public void failToAddComponent() {
        // given
        entity.addComponent(component1);
        // then
        assertThrows(ClashException.class, () -> entity.addComponent(component1));
    }

    @Test
    public void componentEquality() {
        // given
        entity.addComponent(component1);
        entity.addComponent(component2);
        entity.addComponent(component3);
        // then
        assertEquals(component1, entity.getComponent(Component1.class));
        assertEquals(component2, entity.getComponent(Component2.class));
        assertEquals(component3, entity.getComponent(Component3.class));
    }

    @Test
    public void componentInequality() {
        // given
        entity.addComponent(component1);
        entity.addComponent(component2);
        entity.addComponent(component3);
        // then
        assertNotEquals(component1, entity.getComponent(Component2.class));
        assertNotEquals(component2, entity.getComponent(Component3.class));
        assertNotEquals(component3, entity.getComponent(Component1.class));
    }

}