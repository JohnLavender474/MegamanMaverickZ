package com.game.updatables;

import com.game.System;
import com.game.core.IEntity;

import java.util.Set;

/**
 * {@link System} implementation for handling {@link UpdatableComponent} instances.
 */
public class UpdatableSystem extends System {

    public UpdatableSystem() {
        super(Set.of(UpdatableComponent.class));
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        UpdatableComponent updatableComponent = entity.getComponent(UpdatableComponent.class);
        updatableComponent.getUpdatable().update(delta);
    }

}
