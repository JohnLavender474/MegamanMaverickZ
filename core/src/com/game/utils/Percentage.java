package com.game.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Percentage in integer representation.
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Percentage implements Comparable<Percentage> {

    private Integer percentage;

    /**
     * Of percentage.
     *
     * @param percent the percent
     * @return the percentage
     */
    public static Percentage of(int percent) {
        Percentage percentage = new Percentage();
        percentage.setPercentage(percent);
        return percentage;
    }

    /**
     * Get percentage as whole number between 0 and 100.
     *
     * @return the integer percentage
     */
    public Integer getAsWholeNumber() {
        return percentage;
    }

    /**
     * Get percentage as decimal between 0 and 1.
     *
     * @return the as decimal
     */
    public Float getAsDecimal() {
        return percentage / 100f;
    }

    /**
     * Returns true if 100%, else false
     *
     * @return true if 100%, else false
     */
    public boolean isFull() {
        return percentage == 100;
    }

    /**
     * Returns true if 0%, else false
     *
     * @return true if 0%, else false
     */
    public boolean isZero() {
        return percentage == 0;
    }

    /**
     * Sets the percentage. Maximum is 100 and minimum is 0.
     *
     * @param percentage the percentage
     */
    public void setPercentage(int percentage) {
        if (percentage > 100) {
            this.percentage = 100;
        } else {
            this.percentage = Math.max(percentage, 0);
        }
    }

    /**
     * Translate the percentage. Caps at 100 and lowest is 0.
     *
     * @param delta the delta translation
     */
    public void translate(int delta) {
        setPercentage(percentage - delta);
    }

    @Override
    public int compareTo(Percentage o) {
        return percentage.compareTo(o.getAsWholeNumber());
    }

}
