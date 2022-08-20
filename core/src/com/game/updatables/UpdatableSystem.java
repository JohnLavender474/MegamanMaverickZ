package com.game.updatables;

import com.game.core.Entity;
import com.game.core.System;

import java.util.Set;

/**
 * {@link System} implementation for handling {@link UpdatableComponent} instances.
 */
public class UpdatableSystem extends System {

    public UpdatableSystem() {
        super(UpdatableComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        entity.getComponent(UpdatableComponent.class).getUpdatables().forEach((updatable, doUpdate) -> {
            if (doUpdate.get()) {
                updatable.update(delta);
            }
        });
    }

}
