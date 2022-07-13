package com.game.tests.core;

import com.game.controllers.ControllerButton;
import com.game.controllers.ButtonStatus;
import com.game.core.IController;

import java.util.HashMap;
import java.util.Map;

import static com.game.controllers.ButtonStatus.*;
import static com.game.controllers.ControllerUtils.*;

public class TestController implements IController {

    private final Map<ControllerButton, ButtonStatus> controllerButtons = new HashMap<>() {{
        for (ControllerButton controllerButton : ControllerButton.values()) {
            put(controllerButton, IS_RELEASED);
        }
    }};

    @Override
    public boolean isJustPressed(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_PRESSED;
    }

    @Override
    public boolean isPressed(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_PRESSED ||
                controllerButtons.get(controllerButton) == IS_PRESSED;
    }

    @Override
    public boolean isJustReleased(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_RELEASED;
    }

    @Override
    public void updateController() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ButtonStatus status = controllerButtons.get(controllerButton);
            boolean isControllerButtonPressed = isControllerConnected() ?
                    isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                    isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
            if (isControllerButtonPressed) {
                if (status == IS_RELEASED || status == IS_JUST_RELEASED) {
                    controllerButtons.replace(controllerButton, IS_JUST_PRESSED);
                } else {
                    controllerButtons.replace(controllerButton, IS_PRESSED);
                }
            } else if (status == IS_RELEASED || status == IS_JUST_RELEASED) {
                controllerButtons.replace(controllerButton, IS_RELEASED);
            } else {
                controllerButtons.replace(controllerButton, IS_JUST_RELEASED);
            }
        }
    }

}