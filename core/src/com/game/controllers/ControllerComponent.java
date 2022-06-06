package com.game.controllers;

import com.game.Component;
import com.game.entities.Entity;

import java.util.EnumMap;
import java.util.Map;

/**
 * The {@link Component} implementation allowing {@link Entity} instances to listen to controller.
 */
public class ControllerComponent implements Component, ControllerListener {

    private final Map<ControllerButton, ControllerButtonActuator> controllerButtonActuators =
            new EnumMap<>(ControllerButton.class);

    @Override
    public void listenToController(ControllerButton button, ControllerButtonStatus status, float delta) {
        ControllerButtonActuator actuator = controllerButtonActuators.get(button);
        if (actuator != null) {
            switch (status) {
                case IS_JUST_PRESSED -> actuator.onJustPressed(delta);
                case IS_PRESSED -> actuator.onPressContinued(delta);
                case IS_JUST_RELEASED -> actuator.onJustReleased(delta);
            }
        }
    }

    /**
     * Add actuator.
     *
     * @param button   the button
     * @param actuator the actuator
     */
    public void putActuator(ControllerButton button, ControllerButtonActuator actuator) {
        controllerButtonActuators.put(button, actuator);
    }

}
