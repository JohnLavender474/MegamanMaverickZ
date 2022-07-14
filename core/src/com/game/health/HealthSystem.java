package com.game.health;

import com.game.System;
import com.game.core.IEntity;

import java.util.Set;

/**
 * {@link System} implementation for entity health.
 */
public class HealthSystem extends System {

    public HealthSystem() {
        super(Set.of(HealthComponent.class));
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
        if (healthComponent.isHealthJustDepleted()) {
            entity.setDead(true);
            healthComponent.getRunOnDeath().run();
        }
        healthComponent.setPriorHealth(healthComponent.getCurrentHealth());
    }

}
