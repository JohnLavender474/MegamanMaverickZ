package com.game.health;

import com.game.Component;
import com.game.utils.Resettable;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link Component} implementation for health.
 */
@Getter
@Setter
public class HealthComponent implements Component, Resettable {

    private final Runnable runOnDeath;
    private int currentHealth;
    private int priorHealth;

    public HealthComponent(Runnable runOnDeath) {
        if (runOnDeath == null) {
            throw new IllegalStateException("Run on death runnable cannot be null");
        }
        reset();
        this.runOnDeath = runOnDeath;
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
