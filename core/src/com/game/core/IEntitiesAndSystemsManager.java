package com.game.core;

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
     * @param <S>    the type parameter pairOf the system
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
     * Add {@link Entity}. To remove the entity, {@link Entity#isDead()} ishould be setBounds to true.
     * The entity should be purged from all {@link System} instances on the following update cycle.
     *
     * @param entity the entity to be added
     */
    void addEntity(Entity entity);

    /**
     * Adds each entity. See {@link #addEntity(Entity)}.
     *
     * @param entities the entities to be added
     */
    default void addEntities(Collection<? extends Entity> entities) {
        entities.forEach(this::addEntity);
    }

    /**
     * View of entities collection.
     *
     * @return the collection
     */
    Collection<Entity> getEntities();

    /**
     * Should be called when leaving a level screen and all entities need to be disposed of.
     */
    void purgeAllEntities();

}
