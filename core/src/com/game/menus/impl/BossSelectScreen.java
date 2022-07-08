package com.game.menus.impl;

import com.game.GameContext2d;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;

import java.util.Map;

public class BossSelectScreen extends MenuScreen {

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext2d the {@link GameContext2d}
     */
    public BossSelectScreen(GameContext2d gameContext2d) {
        super(gameContext2d);
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return null;
    }

    @Override
    public void render(float delta) {

    }

}
