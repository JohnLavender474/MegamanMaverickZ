package com.game.health;

import com.game.Component;
import com.game.utils.Percentage;
import com.game.utils.Resettable;
import com.game.utils.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * {@link Component} implementation for health. Health consumer consumes {@link #getHealth()}.
 */
public class HealthComponent implements Component, Resettable {

    private final Percentage percentage = Percentage.of(100);
    @Setter @Getter private Updatable healthUpdater;

    /**
     * Get health.
     *
     * @return the health
     */
    public Integer getHealth() {
        return percentage.get();
    }

    /**
     * Set health.
     *
     * @param health the health
     */
    public void setHealth(int health) {
        percentage.setPercentage(health);
    }

    /**
     * Translate health.
     *
     * @param delta the delta
     */
    public void translateHealth(int delta) {
        percentage.translate(delta);
    }

    /**
     * Return if health is zero.
     *
     * @return if health is zero
     */
    public boolean isDead() {
        return percentage.isZero();
    }

    /**
     * Return if health is max.
     *
     * @return if health is max
     */
    public boolean isMaxHealth() {
        return percentage.isFull();
    }

    @Override
    public void reset() {
        setHealth(100);
    }

}
