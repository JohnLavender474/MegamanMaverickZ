package com.game.controllers;

/**
 * The interface Controller Adapter.
 */
public interface ControllerAdapter {

    /**
     * On just pressed.
     */
    default void onJustPressed() {
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
     */
    default void onJustReleased() {
    }

}
