package com.game.core;

import com.badlogic.gdx.Screen;
import com.game.ConstVals.GameScreen;

public interface IScreenManager {

    /**
     * Set screen.
     *
     * @param gameScreen the game screen
     */
    void setScreen(GameScreen gameScreen);

    /**
     * Put screen.
     *
     * @param gameScreen the game screen
     * @param screen     the screen
     */
    void putScreen(GameScreen gameScreen, Screen screen);

}
