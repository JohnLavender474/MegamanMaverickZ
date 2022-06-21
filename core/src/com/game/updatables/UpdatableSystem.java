package com.game.updatables;

import com.game.core.Component;
import com.game.core.System;
import com.game.entities.Entity;

import java.util.Set;

/**
 * {@link System} implementation for handling {@link UpdatableComponent} instances.
 */
public class UpdatableSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(UpdatableComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        UpdatableComponent updatableComponent = entity.getComponent(UpdatableComponent.class);
        updatableComponent.getUpdatable().update(delta);
    }

}
