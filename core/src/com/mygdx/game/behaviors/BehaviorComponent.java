package com.mygdx.game.behaviors;

import com.mygdx.game.core.Component;

import java.util.*;

/**
 * {@link Component} implementation for handling behaviors.
 */
public class BehaviorComponent implements Component {

    private final Map<String, Behavior> behaviors = new HashMap<>();

    /**
     * Put {@link Behavior} mapped to key. {@link Behavior#setUserData(Object)} will be called, adding a reference
     * to this {@link BehaviorComponent} as the argument.
     *
     * @param key      the key
     * @param behavior the behavior to put
     */
    public void putBehavior(String key, Behavior behavior) {
        behavior.setUserData(this);
        behaviors.put(key, behavior);
    }

    /**
     * Gets behavior.
     *
     * @param key the key
     * @return the behavior
     */
    public Behavior getBehavior(String key) {
        return behaviors.get(key);
    }

    /**
     * Gets unmodifiable view of behavior map.
     *
     * @return the behavior map
     */
    public Map<String, Behavior> getBehaviors() {
        return Collections.unmodifiableMap(behaviors);
    }

}
