package com.mygdx.game.behaviors;

import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.System;

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
        behaviorComponent.getBehaviors().values().forEach(behavior -> behavior.update(delta));
    }

}
