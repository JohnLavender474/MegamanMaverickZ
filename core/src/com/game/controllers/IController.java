package com.game.controllers;

public interface IController {

    /**
     * If controller button is just pressed.
     *
     * @param controllerButton the controller button
     * @return if controller button is just pressed
     */
    boolean isJustPressed(ControllerButton controllerButton);

    /**
     * If controller button is pressed. Include if just pressed.
     *
     * @param controllerButton the controller button
     * @return if the controller button is pressed or just pressed
     */
    boolean isPressed(ControllerButton controllerButton);

    /**
     * If controller button is just released.
     *
     * @param controllerButton the controller button
     * @return if the controller button is just released
     */
    boolean isJustReleased(ControllerButton controllerButton);

    void updateController();

}
