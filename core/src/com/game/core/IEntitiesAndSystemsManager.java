package com.game.core;

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
     * Get systems.
     *
     * @return systems
     */
    Collection<System> getSystems();

    /**
     * Update systems.
     *
     * @param delta the delta
     */
    void updateSystems(float delta);

    /**
     * Add {@link IEntity}. To remove the entity, {@link IEntity#isDead()} ishould be set to true.
     * The entity should be purged from all {@link System} instances on the following update cycle.
     *
     * @param entity the entity to be added
     */
    void addEntity(IEntity entity);

    /**
     * Adds each entity. See {@link #addEntity(IEntity)}.
     *
     * @param entities the entities to be added
     */
    default void addEntities(Collection<? extends IEntity> entities) {
        entities.forEach(this::addEntity);
    }

    /**
     * View of entities collection.
     *
     * @return the collection
     */
    Collection<IEntity> getEntities();

    /**
     * Should be called when leaving a level screen and all entities need to be disposed of.
     */
    void purgeAllEntities();

}
