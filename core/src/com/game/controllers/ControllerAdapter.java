package com.game.controllers;

/**
 * The interface Controller Adapter.
 */
public interface ControllerAdapter {

    /**
     * On just pressed.
     *
     * @param delta the delta time
     */
    default void onJustPressed(float delta) {
    }

    /**
     * On press continued.
     *
     * @param delta the delta time
     */
    default void onPressContinued(float delta) {
    }

    /**
     * On just released.
     *
     * @param delta the delta time
     */
    default void onJustReleased(float delta) {
    }

}
