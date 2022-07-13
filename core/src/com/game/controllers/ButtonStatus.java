package com.game.controllers;

/**
 * The status of a {@link ControllerButton}.
 * <p>
 * {@link #IS_JUST_PRESSED}: The button was not pressed in the last update cycle and is pressed
 * in this update cycle.
 * {@link #IS_PRESSED}: The button is pressed in this update cycle.
 * {@link #IS_JUST_RELEASED}: The button was pressed in the last update cycle but is not
 * pressed in this update cycle.
 * {@link #IS_RELEASED}: The button is not pressed in this update cycle.
 */
public enum ButtonStatus {
    IS_JUST_PRESSED, IS_PRESSED, IS_JUST_RELEASED, IS_RELEASED
}
