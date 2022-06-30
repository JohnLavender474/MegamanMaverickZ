package com.game.health;

import com.game.Component;
import com.game.updatables.Updatable;
import com.game.utils.Resettable;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link Component} implementation for health.
 *
 */
@Getter
@Setter
public class HealthComponent implements Component, Resettable {

    private Updatable healthUpdater;
    private int currentHealth;
    private int priorHealth;

    public HealthComponent() {
        reset();
    }

    public void translateHealth(int delta) {
        setHealth(currentHealth + delta);
    }

    public void setHealth(int health) {
        currentHealth = Math.max(0, Math.min(100, health));
    }

    public boolean isHealthJustDepleted() {
        return currentHealth <= 0 && priorHealth > 0;
    }

    @Override
    public void reset() {
        currentHealth = 100;
        priorHealth = 100;
    }

}
