package com.game.menus.impl;

import com.game.GameContext2d;
import com.game.controllers.ControllerActuator;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

public class ControllerSettingsScreen extends MenuScreen {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum ControllerSettingsButton {

        BACK("Back"),
        UP("Up"),
        DOWN("Down"),
        LEFT("Left"),
        RIGHT("Right"),
        X("X"),
        A("A"),
        START("Start");

        private final String str;

    }

    private final ControllerActuator actuator;

    public ControllerSettingsScreen(GameContext2d gameContext) {
        super(gameContext, "");
        this.actuator = gameContext.getControllerActuator();
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return null;
    }

}
