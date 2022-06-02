package com.mygdx.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton containing all {@link System} instances. Systems are mapped to the value of {@link System#getClass()}.
 * Because the behavior of systems is independent of game state, systems should be initialized only once.
 */
public class SystemsManager {

    private final Map<Class<? extends System>, System> systems = new HashMap<>();

    /**
     * Add system.
     *
     * @param system the system
     */
    public void addSystem(System system) {
        systems.put(system.getClass(), system);
    }

    /**
     * Gets system.
     *
     * @param <S>   the type parameter
     * @param clazz the clazz
     * @return the system
     */
    public <S extends System> S getSystem(Class<S> clazz) {
        return clazz.cast(systems.get(clazz));
    }

    /**
     * The {@link Entity} is filtered through all {@link System} values. If the entity is not a member of a system
     * but qualifies for membership, then it is added to the system. And if the entity is a member of a system but
     * no longer qualifies for membership, then it is removed from the system.
     *
     * @param entity the entity to be filtered through the systems
     */
    public void filterEntityThroughSystems(Entity entity) {
        systems.values().forEach(system -> {
            if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                system.addEntity(entity);
            } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                system.removeEntity(entity);
            }
        });
    }

    /**
     * Removes the {@link Entity} from all {@link System} values that it is a member of.
     *
     * @param entity the entity to be removed from systems it is currently a member of
     */
    public void removeEntityFromSystems(Entity entity) {
        systems.values().forEach(system -> {
            if (system.entityIsMember(entity)) {
                system.removeEntity(entity);
            }
        });
    }

    /**
     * Returns if the {@link Entity} is a member of the {@link System} corresponding to the provided class key.
     *
     * @param clazz  the system clazz
     * @param entity the entity
     * @return if the entity is a member of the system
     */
    public boolean systemContainsEntity(Class<? extends System> clazz, Entity entity) {
        System system = systems.get(clazz);
        return system != null && system.entityIsMember(entity);
    }

}
