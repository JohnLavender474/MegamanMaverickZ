package com.game.tests.core;

import com.game.System;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import lombok.Getter;

import java.util.*;

public class TestEntitiesAndSystemsManager implements IEntitiesAndSystemsManager {

    @Getter
    private final Set<IEntity> entities = new HashSet<>();
    private final Queue<IEntity> queuedEntities = new ArrayDeque<>();
    private final Map<Class<? extends System>, System> systems = new LinkedHashMap<>();

    private boolean updating;

    @Override
    public void addEntity(IEntity entity) {
        if (updating) {
            queuedEntities.add(entity);
        } else {
            entities.add(entity);
        }
    }

    @Override
    public void purgeAllEntities() {
        entities.clear();
        systems.values().forEach(System::purgeAllEntities);
    }

    @Override
    public void addSystem(System system) {
        systems.put(system.getClass(), system);
    }

    @Override
    public <S extends System> S getSystem(Class<S> sClass) {
        return sClass.cast(systems.get(sClass));
    }

    @Override
    public Collection<System> getSystems() {
        return systems.values();
    }

    @Override
    public void updateSystems(float delta) {
        updating = true;
        while (!queuedEntities.isEmpty()) {
            entities.add(queuedEntities.poll());
        }
        Iterator<IEntity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            IEntity entity = entityIterator.next();
            if (entity.isDead()) {
                systems.values().forEach(system -> {
                    if (system.entityIsMember(entity)) {
                        system.removeEntity(entity);
                    }
                });
                entityIterator.remove();
            } else {
                systems.values().forEach(system -> {
                    if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                        system.addEntity(entity);
                    } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                        system.removeEntity(entity);
                    }
                });
            }
        }
        systems.values().forEach(system -> system.update(delta));
        updating = false;
    }

}
