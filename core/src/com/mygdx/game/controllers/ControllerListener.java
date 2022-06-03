package com.mygdx.game.controllers;

/**
 * Allows object to listen to controller events.
 */
public interface ControllerListener {

    /**
     * Listen to controller.
     *
     * @param controllerButton       the controller button
     * @param controllerButtonStatus the controller button status
     */
    void listenToController(ControllerButton controllerButton, ControllerButtonStatus controllerButtonStatus);

}
