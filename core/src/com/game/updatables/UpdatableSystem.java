package com.game.updatables;

import com.game.core.Entity;
import com.game.core.System;
import com.game.utils.interfaces.Updatable;

import java.util.Iterator;
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
        UpdatableComponent updatableComponent = entity.getComponent(UpdatableComponent.class);
        Iterator<QualifiedUpdatable> uIter = updatableComponent.getUpdatables().iterator();
        while (uIter.hasNext()) {
            QualifiedUpdatable updatable = uIter.next();
            if (updatable.doUpdate()) {
                updatable.update(delta);
            }
            if (updatable.doRemove()) {
                uIter.remove();
            }
        }
    }

}
