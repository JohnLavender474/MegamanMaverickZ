package com.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.controllers.ControllerButton;

import java.util.Collection;

/**
 * Represents the essentials for a 2D game, including game state management, entity and systems management, controller
 * listening, asset loading, sprite batch, screen management, and blackboard.
 */
public interface GameContext2d {

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
     * Get {@link System}.
     *
     * @param <S>    the type parameter of the system
     * @param sClass the system class
     * @return the system
     */
    <S extends System> S getSystem(Class<S> sClass);

    /**
     * Get sprite batch.
     *
     * @return the sprite batch
     */
    SpriteBatch getSpriteBatch();

    /**
     * If controller button is just pressed.
     *
     * @param controllerButton the controller button
     * @return if controller button is just pressed
     */
    boolean isJustPressed(ControllerButton controllerButton);

    /**
     * If controller button is pressed. Include if just pressed.
     *
     * @param controllerButton the controller button
     * @return if the controller button is pressed or just pressed
     */
    boolean isPressed(ControllerButton controllerButton);

    /**
     * If controller button is just released.
     *
     * @param controllerButton the controller button
     * @return if the controller button is just released
     */
    boolean isJustReleased(ControllerButton controllerButton);

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
     * @param key the key
     */
    void setScreen(String key);

}
