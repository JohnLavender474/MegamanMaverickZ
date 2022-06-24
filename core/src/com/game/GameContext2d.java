package com.game;

import com.game.core.*;

/**
 * Represents the essentials for a 2D game, including game state management, entity and systems management, controller
 * listening, asset loading, sprite batch, screen management, and blackboard.
 */
public interface GameContext2d extends IGameStateManager, IEntitiesAndSystemsManager, IScreenManager, IRenderingManager,
                                       IController, IAssetLoader, IBlackboard, IMessageDispatcher {}
