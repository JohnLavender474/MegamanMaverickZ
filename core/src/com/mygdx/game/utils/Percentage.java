package com.mygdx.game.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents percentage.
 */
@Getter
@EqualsAndHashCode
public class Percentage implements Comparable<Percentage> {

    private Integer percentage;

    /**
     * Sets the percentage.
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
     * Translate the percentage.
     *
     * @param delta the delta
     */
    public void translate(int delta) {
        setPercentage(percentage - delta);
    }

    @Override
    public int compareTo(Percentage o) {
        return percentage.compareTo(o.getPercentage());
    }

}
