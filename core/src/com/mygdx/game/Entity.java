package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Component;
import com.mygdx.game.utils.exceptions.ClashException;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.mygdx.game.utils.UtilMethods.objName;

/**
 * Entity instances act simply as a conglomeration of {@link Component} instances with a {@link Rectangle} bounding box.
 */
@ToString
public class Entity {

    @Getter
    private final Rectangle boundingBox = new Rectangle();

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

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
     * Returns true if this Entity has a {@link Component} mapped to the provided {@link Class<Component>}.
     *
     * @param clazz the Component class
     * @return true if this Entity has a Component mapped to the provided Component class
     */
    public boolean hasComponent(Class<? extends Component> clazz) {
        return components.containsKey(clazz);
    }

    /**
     * Add a {@link Component} to this Entity.
     *
     * @param component the Component to be added
     * @throws ClashException thrown if there is already a Component mapped to {@link Component#getClass()} of the
     *                        provided Component.
     */
    public void addComponent(Component component)
            throws ClashException {
        if (components.containsKey(component.getClass())) {
            throw new ClashException("new component " + objName(component),
                                     "existing component " + objName(component) + " in " + this);
        }
        components.put(component.getClass(), component);
    }

    /**
     * Removes the {@link Component} from this Entity's Component set.
     *
     * @param clazz the class of the Component to be removed
     */
    public void removeComponent(Class<? extends Component> clazz) {
        components.remove(clazz);
    }

}
