package com.mygdx.game.controllers;

/**
 * Defines actions for button on just pressed, on press continued, and on just released. Should be mapped to a
 * specific {@link ControllerButton} value.
 */
public interface ControllerButtonActuator {

    /**
     * On just pressed.
     *
     * @param delta the delta time
     */
    default void onJustPressed(float delta) {}

    /**
     * On press continued.
     *
     * @param delta the delta time
     */
    default void onPressContinued(float delta) {}

    /**
     * On just released.
     *
     * @param delta the delta time
     */
    default void onJustReleased(float delta) {}

}
