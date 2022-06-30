package com.game.core;

import com.game.GameState;

public interface IGameStateManager {

    /**
     * Get current state.
     *
     * @return the current state
     */
    GameState getGameState();

    /**
     * Set current state.
     *
     * @param gameState the game state
     */
    void setGameState(GameState gameState);

}
