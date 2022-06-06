package com.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Util methods for controller and keyboard.
 */
@SuppressWarnings("ConstantConditions")
public class ControllerUtils {

    /**
     * Returns if a controller other than the keyboard is connected.
     *
     * @return if a controller is connected
     */
    public static boolean isControllerConnected() {
        return !Controllers.getControllers().isEmpty();
    }

    /**
     * Gets the first connected controller if any.
     *
     * @return the controller
     */
    public static Controller getController() {
        return isControllerConnected() ? Controllers.getControllers().first() : null;
    }

    /**
     * Is controller button pressed.
     *
     * @param controllerButtonKeycode the controller button keycode
     * @return if the controller button is pressed
     */
    public static boolean isControllerButtonPressed(Integer controllerButtonKeycode) {
        return isControllerConnected() && getController().getButton(controllerButtonKeycode);
    }

    /**
     * Is keyboard button pressed.
     *
     * @param keyboardButtonKeycode the keyboard button keycode
     * @return if the keyboard button is pressed
     */
    public static boolean isKeyboardButtonPressed(Integer keyboardButtonKeycode) {
        return keyboardButtonKeycode != null && Gdx.input.isKeyPressed(keyboardButtonKeycode);
    }

}
