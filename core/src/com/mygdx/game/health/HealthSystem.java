package com.mygdx.game.health;

import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.System;

import java.util.Set;
import java.util.function.Consumer;

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
        Consumer<Integer> healthConsumer = healthComponent.getHealthConsumer();
        if (healthConsumer != null) {
            healthConsumer.accept(healthComponent.getHealth());
        }
    }

}
