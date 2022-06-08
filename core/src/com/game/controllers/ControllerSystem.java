package com.game.controllers;

import com.game.Component;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.System;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * {@link System} implementation for handling {@link ControllerComponent} instances.
 */
@RequiredArgsConstructor
public class ControllerSystem extends System {

    private final GameContext2d gameContext;

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(ControllerComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ControllerComponent controllerComponent = entity.getComponent(ControllerComponent.class);
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ControllerAdapter controllerAdapter = controllerComponent.getControllerAdapters().get(controllerButton);
            if (controllerAdapter == null) {
                continue;
            }
            if (gameContext.isJustPressed(controllerButton)) {
                controllerAdapter.onJustPressed(delta);
            } else if (gameContext.isPressed(controllerButton)) {
                controllerAdapter.onPressContinued(delta);
            } else if (gameContext.isJustReleased(controllerButton)) {
                controllerAdapter.onJustReleased(delta);
            }
        }
    }

}
