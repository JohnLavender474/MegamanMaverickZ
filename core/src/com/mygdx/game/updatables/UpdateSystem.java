package com.mygdx.game.updatables;

import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.System;

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
