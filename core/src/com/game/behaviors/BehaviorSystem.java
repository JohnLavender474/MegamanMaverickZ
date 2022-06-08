package com.game.behaviors;

import com.game.Component;
import com.game.Entity;
import com.game.System;

import java.util.Set;

/**
 * {@link System} implementation for updating entity behaviors.
 */
public class BehaviorSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(BehaviorComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        BehaviorComponent behaviorComponent = entity.getComponent(BehaviorComponent.class);
        behaviorComponent.getBehaviors().forEach(behavior -> behavior.update(delta));
    }

}
