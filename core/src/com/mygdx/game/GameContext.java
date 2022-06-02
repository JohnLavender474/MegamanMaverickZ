package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.controllers.ControllerManager;
import com.mygdx.game.sprites.RenderingGround;
import com.mygdx.game.levels.LevelTiledMapManager;
import com.mygdx.game.sprites.SpriteHandle;

import java.util.Map;
import java.util.Queue;

/**
 * Fetches the objects that need to be globally accessible for the game context.
 */
public interface GameContext {

    /**
     * Sets the screen
     *
     * @param screen the screen
     */
    void setScreen(Screen screen);

    /**
     * Gets screens.
     *
     * @return the screens
     */
    Map<String, Screen> getScreens();

    /**
     * Gets viewports.
     *
     * @return the viewports
     */
    Map<RenderingGround, Viewport> getViewports();

    /**
     * Gets renderables.
     *
     * @return the renderables
     */
    Map<RenderingGround, Queue<SpriteHandle>> getRenderables();

    /**
     * Gets controller manager.
     *
     * @return the controller manager
     */
    ControllerManager getControllerManager();

    /**
     * Gets tiled map manager.
     *
     * @return the tiled map manager
     */
    LevelTiledMapManager getLevelTiledMapManager();

    /**
     * Gets systems manager.
     *
     * @return the systems manager
     */
    SystemsManager getSystemsManager();

    /**
     * Gets asset manager.
     *
     * @return the asset manager
     */
    AssetManager getAssetManager();

    /**
     * Gets sprite batch.
     *
     * @return the sprite batch
     */
    SpriteBatch getSpriteBatch();

}
