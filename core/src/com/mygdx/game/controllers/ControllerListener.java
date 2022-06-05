package com.mygdx.game.controllers;

/**
 * Allows object to listen to controller events.
 */
public interface ControllerListener {

    /**
     * When {@link ControllerManager#listenToController(ControllerListener, float)} is called with this instance
     * provided as the first arg, then this method is called for each {@link ControllerButton} value with its
     * corresponding {@link ControllerButtonStatus} and the delta time.
     *
     * @param button the button
     * @param status the status
     * @param delta  the delta time
     */
    void listenToController(ControllerButton button, ControllerButtonStatus status, float delta);

}
