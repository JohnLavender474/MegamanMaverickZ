package com.game.health;

import com.game.entities.Entity;
import com.game.System;

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
