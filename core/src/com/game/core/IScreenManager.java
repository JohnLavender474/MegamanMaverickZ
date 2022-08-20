package com.game.core;

import com.game.core.ConstVals.GameScreen;

public interface IScreenManager {

    /**
     * Get current game screen key.
     *
     * @return current game screen key
     */
    GameScreen getCurrentScreenKey();

    /**
     * Set screen. Previous screen should be disposed of.
     *
     * @param gameScreen the game screen
     */
    void setScreen(GameScreen gameScreen);

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

    /**
     * Returns the key of the current overlay screen if one is present.
     *
     * @return the current overlay screen key
     */
    GameScreen getOverlayScreenKey();

}
