package com.game.health;

import com.game.core.Entity;
import com.game.core.System;

import java.util.Set;

/**
 * {@link System} implementation for entity health.
 */
public class HealthSystem extends System {

    public HealthSystem() {
        super(HealthComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
        if (healthComponent.isHealthJustDepleted()) {
            entity.setDead(true);
            healthComponent.getRunOnDeath().run();
        }
        healthComponent.setPriorHealth(healthComponent.getCurrentHealth());
    }

}
