package com.game.health;

import com.game.Component;
import com.game.utils.Percentage;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * {@link Component} implementation for health. Health consumer consumes {@link #getHealth()}.
 */
public class HealthComponent implements Component {

    private final Percentage percentage = Percentage.of(100);
    @Setter @Getter private Consumer<Integer> healthConsumer;

    /**
     * Gets health.
     *
     * @return the health
     */
    public Integer getHealth() {
        return percentage.get();
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
     * Returns if health is zero.
     *
     * @return the boolean
     */
    public boolean isDead() {
        return percentage.isZero();
    }

    /**
     * Returns if health is max.
     *
     * @return the boolean
     */
    public boolean isMaxHealth() {
        return percentage.isFull();
    }

}
