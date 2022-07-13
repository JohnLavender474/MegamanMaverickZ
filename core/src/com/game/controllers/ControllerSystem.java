package com.game.controllers;

import com.game.System;
import com.game.core.IController;
import com.game.core.IEntity;

import java.util.Set;

/**
 * {@link System} implementation for handling {@link ControllerComponent} instances.
 */
public class ControllerSystem extends System {

    private final IController controller;

    public ControllerSystem(IController controller) {
        super(Set.of(ControllerComponent.class));
        this.controller = controller;
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        ControllerComponent controllerComponent = entity.getComponent(ControllerComponent.class);
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ControllerAdapter controllerAdapter = controllerComponent.getControllerAdapters().get(controllerButton);
            if (controllerAdapter == null) {
                continue;
            }
            if (controller.isJustPressed(controllerButton)) {
                controllerAdapter.onJustPressed();
            } else if (controller.isPressed(controllerButton)) {
                controllerAdapter.onPressContinued(delta);
            } else if (controller.isJustReleased(controllerButton)) {
                controllerAdapter.onJustReleased();
            } else {
                controllerAdapter.onReleaseContinued();
            }
        }
    }

}
