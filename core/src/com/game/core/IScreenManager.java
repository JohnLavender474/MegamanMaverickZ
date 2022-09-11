package com.game.core;

import com.badlogic.gdx.Screen;
import com.game.core.constants.GameScreen;

public interface IScreenManager {

    /**
     * Set screen. Previous screen should be disposed of.
     *
     * @param gameScreen the game screen
     */
    void setScreen(GameScreen gameScreen);

    /**
     * Gets the screen mapped to the game screen key
     *
     * @param gameScreen the game screen key
     * @return  the screen
     */
    Screen getScreen(GameScreen gameScreen);

    /**
     * Overlays a screen onto the current screen.
     *
     * @param gameScreen the game screen
     */
    void putOverlayScreen(GameScreen gameScreen);

    /**
     * Removes the overlay screen.
     */
    void popOverlayScreen();

}
