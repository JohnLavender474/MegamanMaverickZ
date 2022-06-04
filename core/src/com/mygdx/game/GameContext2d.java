package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.ConstVals.GameState;
import com.mygdx.game.controllers.ControllerManager;
import com.mygdx.game.core.SystemsManager;

/**
 * Represents the bare essentials for a 2D game.
 */
public interface GameContext2d {

    /**
     * Gets sprite batch.
     *
     * @return the sprite batch
     */
    SpriteBatch getSpriteBatch();

    /**
     * Gets systems manager.
     *
     * @return the systems manager
     */
    SystemsManager getSystemsManager();

    /**
     * Gets controller manager.
     *
     * @return the controller manager
     */
    ControllerManager getControllerManager();

    /**
     * Gets asset such as music or sound effect object.
     *
     * @param <T>    the type parameter of the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the asset
     */
    <T> T getAsset(String key, Class<T> tClass);

    /**
     * Sets screen.
     *
     * @param key the key
     */
    void setScreen(String key);

    /**
     * Gets game state.
     *
     * @return the game state
     */
    GameState getGameState();

    /**
     * Sets game state.
     *
     * @param gameState the game state
     */
    void setGameState(GameState gameState);

}
