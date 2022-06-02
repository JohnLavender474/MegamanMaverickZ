package com.mygdx.game.controllers;

import com.mygdx.game.utils.Updatable;

import java.util.EnumMap;
import java.util.Map;

import static com.mygdx.game.controllers.ControllerUtils.*;

/**
 * Manages player input on a controller or keyboard.
 */
public class ControllerManager implements Updatable {

    private final Map<ControllerButton, ControllerButtonDefinition> controllerButtons =
            new EnumMap<>(ControllerButton.class);

    /**
     * Instantiates a new Controller manager.
     */
    public ControllerManager() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            controllerButtons.put(controllerButton, new ControllerButtonDefinition());
        }
    }

    /**
     * Gets controller button status.
     *
     * @param controllerButton the controller button
     * @return the controller button status
     */
    public ControllerButtonStatus getControllerButtonStatus(ControllerButton controllerButton) {
        ControllerButtonDefinition controllerButtonDefinition = controllerButtons.get(controllerButton);
        return controllerButtonDefinition != null ? controllerButtonDefinition.getControllerButtonStatus() :
                ControllerButtonStatus.IS_RELEASED;
    }

    /**
     * Sets custom keyboard button code.
     *
     * @param controllerButton   the controller button
     * @param keyboardButtonCode the keyboard button code
     */
    public void setCustomKeyboardButtonCode(ControllerButton controllerButton, Integer keyboardButtonCode) {
        ControllerButtonDefinition controllerButtonDefinition = controllerButtons.get(controllerButton);
        if (controllerButtonDefinition != null) {
            controllerButtonDefinition.setCustomKeyboardButtonCode(keyboardButtonCode);
        }
    }

    /**
     * Sets custom controller button code.
     *
     * @param controllerButton     the controller button
     * @param controllerButtonCode the controller button code
     */
    public void setCustomControllerButtonCode(ControllerButton controllerButton, Integer controllerButtonCode) {
        ControllerButtonDefinition controllerButtonDefinition = controllerButtons.get(controllerButton);
        if (controllerButtonDefinition != null) {
            controllerButtonDefinition.setCustomControllerButtonCode(controllerButtonCode);
        }
    }

    @Override
    public void update(float delta) {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ControllerButtonDefinition controllerButtonDefinition = controllerButtons.get(controllerButton);
            ControllerButtonStatus controllerButtonStatus = controllerButtonDefinition.getControllerButtonStatus();
            boolean isControllerButtonPressed;
            if (isControllerConnected()) {
                Integer controllerButtonCode = controllerButtonDefinition.getCustomControllerButtonCode() != null ?
                        controllerButtonDefinition.getCustomControllerButtonCode() :
                        controllerButton.getDefaultControllerBindingCode();
                isControllerButtonPressed = isControllerButtonPressed(controllerButtonCode);
            } else {
                Integer keyboardButtonCode = controllerButtonDefinition.getCustomKeyboardButtonCode() != null ?
                        controllerButtonDefinition.getCustomKeyboardButtonCode() :
                        controllerButton.getDefaultKeyboardBindingCode();
                isControllerButtonPressed = isKeyboardButtonPressed(keyboardButtonCode);
            }
            if (isControllerButtonPressed) {
                if (controllerButtonStatus == ControllerButtonStatus.IS_RELEASED ||
                        controllerButtonStatus == ControllerButtonStatus.IS_JUST_RELEASED) {
                    controllerButtonDefinition.setControllerButtonStatus(ControllerButtonStatus.IS_JUST_PRESSED);
                } else {
                    controllerButtonDefinition.setControllerButtonStatus(ControllerButtonStatus.IS_PRESSED);
                }
            } else {
                if (controllerButtonStatus == ControllerButtonStatus.IS_JUST_RELEASED ||
                        controllerButtonStatus == ControllerButtonStatus.IS_RELEASED) {
                    controllerButtonDefinition.setControllerButtonStatus(ControllerButtonStatus.IS_RELEASED);
                } else {
                    controllerButtonDefinition.setControllerButtonStatus(ControllerButtonStatus.IS_JUST_RELEASED);
                }
            }
        }
    }

}
