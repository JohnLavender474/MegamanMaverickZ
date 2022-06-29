package com.game.core;

import com.game.Entity;
import com.game.System;

import java.util.Collection;

public interface IEntitiesAndSystemsManager {

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
     * Add {@link Entity}. To remove the entity, {@link Entity#isDead()} ishould be set to true.
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

}
