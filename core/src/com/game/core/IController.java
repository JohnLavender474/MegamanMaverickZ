package com.game.core;

import com.game.controllers.ControllerButton;

import static java.util.Arrays.stream;

/**
 * The interface Controller.
 */
public interface IController {

    /**
     * If any controller button is pressed.
     *
     * @return if any controller button is pressed
     */
    default boolean isAnyControllerButtonPressed() {
        return stream(ControllerButton.values()).anyMatch(this::isPressed);
    }

    /**
     * If any controller button is just pressed.
     *
     * @return if any controller button is just pressed
     */
    default boolean isAnyControllerButtonJustPressed() {
        return stream(ControllerButton.values()).anyMatch(this::isJustPressed);
    }

    /**
     * If any controller button is just released.
     *
     * @return if any controller button is just released
     */
    default boolean isAnyControllerButtonJustReleased() {
        return stream(ControllerButton.values()).anyMatch(this::isJustReleased);
    }

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

    /**
     * Update controller.
     */
    void updateController();

    /**
     * Returns if the controller should be updated.
     *
     * @return if the controller should be updated
     */
    boolean doUpdateController();

    /**
     * Set if the controller should be updated.
     *
     * @param doUpdateController if the controller should be updated
     */
    void setDoUpdateController(boolean doUpdateController);

}
