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
        entity.getComponent(UpdatableComponent.class).getUpdatables().forEach((updatable, doUpdate) -> {
            if (doUpdate.get()) {
                updatable.update(delta);
            }
        });
    }

}
