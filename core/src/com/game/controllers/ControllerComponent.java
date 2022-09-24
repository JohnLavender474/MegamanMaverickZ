package com.game.controllers;

import com.game.Component;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

/**
 * {@link Component} implementation allowing entities to listen to the player's controller.
 */
@Getter
public class ControllerComponent extends Component {

    private final Map<ControllerButton, ControllerAdapter> controllerAdapters = new EnumMap<>(ControllerButton.class);

    /**
     * Add controller adapter.
     *
     * @param controllerButton  the controller button
     * @param controllerAdapter the controller adapter
     */
    public void addControllerAdapter(ControllerButton controllerButton, ControllerAdapter controllerAdapter) {
        controllerAdapters.put(controllerButton, controllerAdapter);
    }

}
