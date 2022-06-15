package com.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.GameScreen;
import com.game.ConstVals.RenderingGround;
import com.game.controllers.ControllerButton;
import com.game.controllers.IController;

import java.util.Collection;
import java.util.Map;

/**
 * Represents the essentials for a 2D game, including game state management, entity and systems management, controller
 * listening, asset loading, sprite batch, screen management, and blackboard.
 */
public interface GameContext2d extends IController {

    /**
     * Gets uiViewport.
     *
     * @param renderingGround the rendering ground
     * @return the uiViewport
     */
    Viewport getViewport(RenderingGround renderingGround);

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

    /**
     * Add {@link Entity}. To remove the entity, {@link Entity#isMarkedForRemoval()} should be set to true.
     * The entity should be purged from all {@link System} instances on the following update cycle.
     *
     * @param entity the entity
     */
    void addEntity(Entity entity);

    /**
     * View of entities collection.
     *
     * @return the collection
     */
    Collection<Entity> viewOfEntities();

    /**
     * Should be called when leaving a level screen and all entities need to be disposed of.
     */
    void purgeAllEntities();

    /**
     * Add system.
     *
     * @param system the system
     */
    void addSystem(System system);

    /**
     * Get {@link System}.
     *
     * @param <S>    the type parameter of the system
     * @param sClass the system class
     * @return the system
     */
    <S extends System> S getSystem(Class<S> sClass);

    /**
     * Update systems.
     *
     * @param delta the delta
     */
    void updateSystems(float delta);

    /**
     * Get sprite batch.
     *
     * @return the sprite batch
     */
    SpriteBatch getSpriteBatch();

    /**
     * Get shape renderer.
     *
     * @return the shape renderer
     */
    ShapeRenderer getShapeRenderer();

    /**
     * Put blackboard object.
     *
     * @param key    the key
     * @param object the object
     */
    void putBlackboardObject(String key, Object object);

    /**
     * Get blackboard object.
     *
     * @param <T>    the type parameter of the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the blackboard object
     */
    <T> T getBlackboardObject(String key, Class<T> tClass);

    /**
     * Get asset such as music or sound effect object.
     *
     * @param <T>    the type parameter of the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the asset
     */
    <T> T loadAsset(String key, Class<T> tClass);

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
