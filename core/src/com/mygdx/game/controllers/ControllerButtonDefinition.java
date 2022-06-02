package com.mygdx.game.controllers;

import lombok.Getter;
import lombok.Setter;

/**
 * Definition of a controller button at a given instance.
 */
@Getter
@Setter
public class ControllerButtonDefinition {
    private ControllerButtonStatus controllerButtonStatus = ControllerButtonStatus.IS_RELEASED;
    private Integer customControllerButtonCode;
    private Integer customKeyboardButtonCode;
}
