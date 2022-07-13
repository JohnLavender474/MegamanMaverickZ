package com.game;

import com.game.core.IEntity;
import com.game.updatables.Updatable;
import com.game.utils.exceptions.InvalidActionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

import static com.game.utils.UtilMethods.objName;

/**
 * The base class of game systems. Instances of this class perform game logic on a set of {@link IEntity} instances.
 * Entities are eligible to be added to a System only if {@link IEntity#hasAllComponents(Collection)} contains all the
 * elements of {@link #componentMask}. Because the behavior of systems is independent of game state, systems should
 * only be initialized once.
 */
@RequiredArgsConstructor
public abstract class System implements Updatable {

    private final Set<IEntity> entities = new HashSet<>();
    private final Queue<IEntity> entitiesToAddQueue = new LinkedList<>();
    private final Queue<IEntity> entitiesToRemoveQueue = new LinkedList<>();
    private final Set<Class<? extends Component>> componentMask;
    @Setter
    @Getter
    private boolean isOn = true;

    /**
     * Process each {@link IEntity} during the update cycle.
     *
     * @param entity the entity
     * @param delta  the delta time
     */
    protected abstract void processEntity(IEntity entity, float delta);

    /**
     * Optional method. Called once before {@link #entities} is filtered through {@link #processEntity(IEntity, float)}.
     *
     * @param delta the delta time
     */
    protected void preProcess(float delta) {
    }

    /**
     * Optional method. Called once after {@link #entities} is filtered through {@link #processEntity(IEntity, float)}.
     *
     * @param delta the delta time
     */
    protected void postProcess(float delta) {
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@link #isOn()}, then update the system. Otherwise, do nothing and return.
     *
     * @param delta the delta time
     */
    @Override
    public void update(float delta) {
        if (!isOn) {
            return;
        }
        while (!entitiesToAddQueue.isEmpty()) {
            entities.add(entitiesToAddQueue.poll());
        }
        while (!entitiesToRemoveQueue.isEmpty()) {
            entities.remove(entitiesToRemoveQueue.poll());
        }
        entities.removeIf(entity -> !qualifiesMembership(entity) || entity.isDead());
        preProcess(delta);
        entities.forEach(entity -> processEntity(entity, delta));
        postProcess(delta);
    }

    /**
     * Returns if the {@link IEntity} can be accepted as a member of this System by comparing {@link #componentMask}
     * to {@link IEntity#hasAllComponents(Collection)}. If the com.game.Entity's set of component keys contains all
     * the component
     * classes contained in this System's component mask, then the com.game.Entity is accepted, otherwise the com
     * .game.Entity is rejected.
     *
     * @param entity the entity
     * @return true if the com.game.Entity can be added, else false
     */
    public boolean qualifiesMembership(IEntity entity) {
        return entity.hasAllComponents(componentMask);
    }

    /**
     * Attempts to add the {@link IEntity} as a member, returns true if the attempt is successful, else false.
     *
     * @param entity the entity
     * @throws InvalidActionException the invalid action exception
     */
    public void addEntity(IEntity entity) throws InvalidActionException {
        if (!qualifiesMembership(entity)) {
            throw new InvalidActionException("Cannot add " + objName(entity) + " as member of " + this);
        }
        entitiesToAddQueue.add(entity);
    }

    /**
     * Attempts to remove the {@link IEntity} from membership to this System. If this System is currently in an
     * update cycle, then the com.game.Entity is queued to be removed on the next update cycle, else it is removed
     * immediately.
     *
     * @param entity the entity
     */
    public void removeEntity(IEntity entity) {
        entitiesToRemoveQueue.add(entity);
    }

    /**
     * Returns if the {@link IEntity} is a member of {@link #entities}. Returns false if the entity is queued
     * for membership.
     *
     * @param entity the entity
     * @return true if the entity is a member
     */
    public boolean entityIsMember(IEntity entity) {
        return entities.contains(entity);
    }

    /**
     * Purge all entities.
     */
    public void purgeAllEntities() {
        entities.clear();
        entitiesToAddQueue.clear();
        entitiesToRemoveQueue.clear();
    }

}
