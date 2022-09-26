package com.game.controllers;

import com.game.entities.Entity;
import com.game.System;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * {@link System} implementation for handling {@link ControllerComponent} instances.
 */
public class ControllerSystem extends System {

    private final Predicate<ControllerButton> isPressedPredicate;

    private Map<ControllerButton, Boolean> thisFrame;
    private Map<ControllerButton, Boolean> previousFrame;

    public ControllerSystem(Predicate<ControllerButton> isPressedPredicate) {
        super(ControllerComponent.class);
        this.isPressedPredicate = isPressedPredicate;
        this.thisFrame = new EnumMap<>(ControllerButton.class) {{
            for (ControllerButton controllerButton : ControllerButton.values()) {
                put(controllerButton, false);
            }
        }};
    }

    @Override
    protected void preProcess(float delta) {
        previousFrame = new EnumMap<>(thisFrame);
        for (ControllerButton controllerButton : ControllerButton.values()) {
            thisFrame.replace(controllerButton, isPressedPredicate.test(controllerButton));
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ControllerComponent controllerComponent = entity.getComponent(ControllerComponent.class);
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ControllerAdapter controllerAdapter = controllerComponent.getControllerAdapters().get(controllerButton);
            if (controllerAdapter == null) {
                continue;
            }
            boolean prev = previousFrame.get(controllerButton);
            boolean now = thisFrame.get(controllerButton);
            if (!prev && now) {
                controllerAdapter.onJustPressed();
            } else if (prev && now) {
                controllerAdapter.onPressContinued(delta);
            } else if (prev && !now) {
                controllerAdapter.onJustReleased();
            } else {
                controllerAdapter.onReleaseContinued();
            }
        }
    }

}
