package com.game;

import java.util.Collection;
import java.util.Map;

public interface Entity {

    Map<Class<? extends Component>, Component> getComponents();

    boolean isDead();

    void setDead(boolean dead);

    default void onDeath() {}

    default <C> C getComponent(Class<C> componentClass) {
        return componentClass.cast(getComponents().get(componentClass));
    }

    default boolean hasAllComponents(Collection<Class<? extends Component>> clazzes) {
        return getComponents().keySet().containsAll(clazzes);
    }

    default void addComponent(Component component) {
        getComponents().put(component.getClass(), component);
    }

}
