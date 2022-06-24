package com.game.core;

import com.game.GameState;

public interface IGameStateManager {

    /**
     * Set current state.
     *
     * @param gameState the game state
     */
    void setGameState(GameState gameState);

    /**
     * Get current state.
     *
     * @return the current state
     */
    GameState getGameState();

}
