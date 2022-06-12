package com.game.behaviors;

import com.game.Component;
import com.game.Entity;
import com.game.System;
import com.game.utils.Updatable;

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
        Updatable preProcess = behaviorComponent.getPreProcess();
        if (preProcess != null) {
            preProcess.update(delta);
        }
        behaviorComponent.getBehaviors().forEach(behavior -> behavior.update(delta));
        Updatable postProcess = behaviorComponent.getPostProcess();
        if (postProcess != null) {
            postProcess.update(delta);
        }
    }

}
