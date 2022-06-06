package com.game.controllers;

import java.util.EnumMap;
import java.util.Map;

import static com.game.controllers.ControllerUtils.*;

/**
 * Manages player input on a controller or keyboard.
 */
public class ControllerManager {

    private final Map<ControllerButton, ControllerButtonStatus> controllerButtons =
            new EnumMap<>(ControllerButton.class);

    /**
     * Instantiates a new Controller Manager.
     */
    public ControllerManager() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            controllerButtons.put(controllerButton, ControllerButtonStatus.IS_RELEASED);
        }
    }

    public ControllerButtonStatus getButtonStatus(ControllerButton button) {
        return controllerButtons.get(button);
    }

    public void updateControllerStatuses() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ControllerButtonStatus status = controllerButtons.get(controllerButton);
            boolean isControllerButtonPressed = isControllerConnected() ?
                    isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                    isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
            if (isControllerButtonPressed) {
                if (status == ControllerButtonStatus.IS_RELEASED ||
                        status == ControllerButtonStatus.IS_JUST_RELEASED) {
                    controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_JUST_PRESSED);
                } else {
                    controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_PRESSED);
                }
            } else if (status == ControllerButtonStatus.IS_JUST_RELEASED ||
                    status == ControllerButtonStatus.IS_RELEASED) {
                controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_RELEASED);
            } else {
                controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_JUST_RELEASED);
            }
        }
    }

    public void listenToController(ControllerListener listener, float delta) {
        controllerButtons.forEach(
                (key, value) -> listener.listenToController(key, value, delta));
    }

}
