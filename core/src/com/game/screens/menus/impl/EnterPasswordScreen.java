package com.game.screens.menus.impl;

import com.game.core.MegaTextHandle;
import com.game.core.GameContext2d;
import com.game.screens.menus.MenuButton;
import com.game.screens.menus.MenuScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnterPasswordScreen extends MenuScreen {

    private final List<List<MegaTextHandle>> passcodeFonts = new ArrayList<>();

    public EnterPasswordScreen(GameContext2d gameContext) {
        super(gameContext, "", "");

    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return null;
    }

}
