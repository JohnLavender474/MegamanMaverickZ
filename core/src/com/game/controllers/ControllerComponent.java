package com.game.controllers;

import com.game.Component;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

/**
 * {@link Component} implementation allowing entities to listen to the player's controller.
 */
@Getter
public class ControllerComponent implements Component {
    private final Map<ControllerButton, ControllerAdapter> controllerAdapters =
            new EnumMap<>(ControllerButton.class);
}
