package com.game.menus.impl;

import com.game.GameContext2d;
import com.game.entities.megaman.MegamanInfo;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;

import java.util.Map;

import static com.game.entities.megaman.MegamanVals.MEGAMAN_INFO;
import static com.game.entities.megaman.MegamanWeapon.MEGA_BUSTER;

public class PauseMenuScreen extends MenuScreen {

    private final MegamanInfo megamanInfo;

    /**
     * Instantiates a new Menu Screen. The first button is setBounds and music begins playing on showing.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public PauseMenuScreen(GameContext2d gameContext) {
        super(gameContext, MEGA_BUSTER.name());
        this.megamanInfo = gameContext.getBlackboardObject(MEGAMAN_INFO, MegamanInfo.class);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return Map.of();
    }

}
