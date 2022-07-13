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

    private Runnable runOnDeath;
    private int currentHealth;
    private int priorHealth;
    private int maxHealth;

    public HealthComponent(int maxHealth) {
        this(maxHealth, () -> {});
    }

    public HealthComponent(int maxHealth, Runnable runOnDeath) {
        if (runOnDeath == null) {
            throw new IllegalStateException();
        }
        this.runOnDeath = runOnDeath;
        this.maxHealth = maxHealth;
        reset();
    }

    public void sub(int delta) {
        setHealth(currentHealth - delta);
    }

    public void add(int delta) {
        setHealth(currentHealth + delta);
    }

    public void setHealth(int health) {
        currentHealth = Math.max(0, Math.min(maxHealth, health));
    }

    public boolean isHealthJustDepleted() {
        return currentHealth <= 0 && priorHealth > 0;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    @Override
    public void reset() {
        currentHealth = priorHealth = maxHealth;
    }

}
