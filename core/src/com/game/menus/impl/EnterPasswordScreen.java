package com.game.menus.impl;

import com.game.core.FontHandle;
import com.game.core.GameContext2d;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnterPasswordScreen extends MenuScreen {

    private final List<List<FontHandle>> passcodeFonts = new ArrayList<>();

    public EnterPasswordScreen(GameContext2d gameContext) {
        super(gameContext, "", "");

    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return null;
    }

}
