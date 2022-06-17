package com.game.entities;

import com.game.Component;
import com.game.utils.exceptions.ClashException;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.game.utils.UtilMethods.objName;

/**
 * Entity instances act mostly as a conglomeration of {@link Component} instances.
 */
public class Entity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    @Getter @Setter private boolean markedForRemoval;

    /**
     * Die.
     */
    public void die() {
        setMarkedForRemoval(true);
    }

    /**
     * Fetches the {@link Component} instances mapped to the provided {@link Class<Component>} if it exists.
     *
     * @param <C>            the Component type parameter
     * @param componentClass the component class
     * @return the component to be fetched
     */
    public <C> C getComponent(Class<C> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    /**
     * Returns true the key set of {@link #components} contains all the provided {@link Class<Component>} instances
     * in the Collection.
     *
     * @param clazzes the Component classes
     * @return true if the key set of this Entity's component set contains all the Component classes in the Collection
     */
    public boolean hasAllComponents(Collection<Class<? extends Component>> clazzes) {
        return components.keySet().containsAll(clazzes);
    }

    /**
     * Add a {@link Component} to this Entity.
     *
     * @param component the Component to be added
     * @throws ClashException thrown if there is already a Component mapped to {@link Component#getClass()} of the provided Component.
     */
    public void addComponent(Component component)
            throws ClashException {
        if (components.containsKey(component.getClass())) {
            throw new ClashException("new component " + objName(component), "existing component " + objName(component) + " in " + this);
        }
        components.put(component.getClass(), component);
    }

}
