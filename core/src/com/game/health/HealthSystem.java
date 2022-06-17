package com.game.health;

import com.game.Component;
import com.game.entities.Entity;
import com.game.System;
import com.game.utils.Updatable;

import java.util.Set;

/**
 * {@link System} implementation for entity health.
 */
public class HealthSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(HealthComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
        Updatable healthUpdater = healthComponent.getHealthUpdater();
        if (healthUpdater != null) {
            healthUpdater.update(delta);
        }
    }

}
