package com.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.controllers.ControllerManager;
import com.game.entities.Entity;

import java.util.Collection;

/**
 * Represents the bare essentials for a 2D game.
 */
public interface GameContext2d {

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
     * Gets {@link System}.
     *
     * @param <S>    the type parameter of the system
     * @param sClass the system class
     * @return the system
     */
    <S extends System> S getSystem(Class<S> sClass);

    /**
     * Gets sprite batch.
     *
     * @return the sprite batch
     */
    SpriteBatch getSpriteBatch();

    /**
     * Gets controller manager.
     *
     * @return the controller manager
     */
    ControllerManager getControllerManager();

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
     * Gets asset such as music or sound effect object.
     *
     * @param <T>    the type parameter of the object
     * @param key    the key
     * @param tClass the class to cast the object to
     * @return the asset
     */
    <T> T loadAsset(String key, Class<T> tClass);

    /**
     * Sets screen.
     *
     * @param key the key
     */
    void setScreen(String key);

}
