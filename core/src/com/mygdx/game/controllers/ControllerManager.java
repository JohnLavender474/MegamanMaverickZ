package com.mygdx.game.controllers;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mygdx.game.controllers.ControllerUtils.*;

/**
 * Manages player input on a controller or keyboard.
 */
public class ControllerManager {

    private final Map<ControllerButton, ControllerButtonDefinition> controllerButtons =
            new EnumMap<>(ControllerButton.class);
    private final Set<ControllerListener> controllerListeners = new HashSet<>();

    /**
     * Instantiates a new Controller manager.
     */
    public ControllerManager() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            controllerButtons.put(controllerButton, new ControllerButtonDefinition());
        }
    }

    /**
     * Add controller listener.
     *
     * @param controllerListener the controller listener
     */
    public void addControllerListener(ControllerListener controllerListener) {
        controllerListeners.add(controllerListener);
    }

    /**
     * Remove controller listener.
     *
     * @param controllerListener the controller listener
     */
    public void removeControllerListener(ControllerListener controllerListener) {
        controllerListeners.remove(controllerListener);
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

    /**
     * Update controller statuses.
     */
    public void updateControllerStatuses() {
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

    /**
     * Update controller listeners.
     */
    public void updateControllerListeners() {
        for (Map.Entry<ControllerButton, ControllerButtonDefinition> entry : controllerButtons.entrySet()) {
            controllerListeners.forEach(controllerListener -> controllerListener
                    .listenToController(entry.getKey(),
                                        entry.getValue().getControllerButtonStatus()));
        }
    }

}
