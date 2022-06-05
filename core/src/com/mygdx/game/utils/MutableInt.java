package com.mygdx.game.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Simple convenience wrapper class.
 */
@Getter
@Setter
@AllArgsConstructor
public class MutableInt {

    private Integer value;

    public static MutableInt of(int value) {
        return new MutableInt(value);
    }

    /**
     * Increment the value by one.
     */
    public void increment() {
        value++;
    }

    /**
     * Decrement the value by one.
     */
    public void decrement() {
        value--;
    }

    /**
     * Translate by the provided delta value.
     *
     * @param delta the delta
     */
    public void translate(int delta) {
        value += delta;
    }

}
