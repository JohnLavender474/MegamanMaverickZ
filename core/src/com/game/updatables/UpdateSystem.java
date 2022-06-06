package com.game.updatables;

import com.game.Component;
import com.game.entities.Entity;
import com.game.System;

import java.util.Set;

public class UpdateSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(UpdatableComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        UpdatableComponent updatableComponent = entity.getComponent(UpdatableComponent.class);
        updatableComponent.getUpdatables().forEach(updatable -> updatable.update(delta));
    }

}
