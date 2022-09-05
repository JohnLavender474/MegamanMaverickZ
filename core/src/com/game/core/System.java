package com.game.core;

import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

import static com.game.utils.UtilMethods.objName;

/**
 * The base class of game systems. Instances of this class perform game logic on a setBounds of {@link Entity} instances.
 * Entities are eligible to be added to a System only if {@link Entity#hasAllComponents(Collection)} contains all the
 * elements of {@link #componentMask}. Because the behavior of systems is independent of game state, systems should
 * only be initialized once.
 */
@RequiredArgsConstructor
public abstract class System implements Updatable {

    private final Set<Entity> entities = new HashSet<>();
    private final Set<Class<? extends Component>> componentMask;
    private final Queue<Entity> entitiesToAddQueue = new LinkedList<>();
    private final Queue<Entity> entitiesToRemoveQueue = new LinkedList<>();

    @Setter
    @Getter
    private boolean isOn = true;

    /**
     * Constructor for var args components.
     *
     * @param componentMask the components to mask
     */
    @SafeVarargs
    public System(Class<? extends Component>... componentMask) {
        this(Set.of(componentMask));
    }

    /**
     * Process each {@link Entity} during the update cycle.
     *
     * @param entity the entity
     * @param delta  the delta time
     */
    protected abstract void processEntity(Entity entity, float delta);

    /**
     * Optional method. Called once before {@link #entities} is filtered through {@link #processEntity(Entity, float)}.
     *
     * @param delta the delta time
     */
    protected void preProcess(float delta) {
    }

    /**
     * Optional method. Called once after {@link #entities} is filtered through {@link #processEntity(Entity, float)}.
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
        preProcess(delta);
        entities.stream().filter(entity -> componentMask.stream().allMatch(
                        cClass -> entity.getComponent(cClass).isOn()))
                .forEach(entity -> processEntity(entity, delta));
        postProcess(delta);
        while (!entitiesToAddQueue.isEmpty()) {
            entities.add(entitiesToAddQueue.poll());
        }
        while (!entitiesToRemoveQueue.isEmpty()) {
            entities.remove(entitiesToRemoveQueue.poll());
        }
        entities.removeIf(entity -> !qualifiesMembership(entity) || entity.isDead());
    }

    /**
     * Returns if the {@link Entity} can be accepted as a member of this System by comparing {@link #componentMask}
     * to {@link Entity#hasAllComponents(Collection)}. If the com.game.core.Entity's setBounds of component keys contains all
     * the component
     * classes contained in this System's component mask, then the com.game.core.Entity is accepted, otherwise the com
     * .game.Entity is rejected.
     *
     * @param entity the entity
     * @return true if the com.game.core.Entity can be added, else false
     */
    public boolean qualifiesMembership(Entity entity) {
        return entity.hasAllComponents(componentMask);
    }

    /**
     * Attempts to add the {@link Entity} as a member, returns true if the attempt is successful, else false.
     *
     * @param entity the entity
     */
    public void addEntity(Entity entity) {
        if (!qualifiesMembership(entity)) {
            throw new IllegalStateException("Cannot add " + objName(entity) + " as member of " + this);
        }
        entitiesToAddQueue.add(entity);
    }

    /**
     * Attempts to remove the {@link Entity} from membership to this System. If this System is currently in an
     * update cycle, then the com.game.core.Entity is queued to be removed on the next update cycle, else it is removed
     * immediately.
     *
     * @param entity the entity
     */
    public void removeEntity(Entity entity) {
        entitiesToRemoveQueue.add(entity);
    }

    /**
     * Returns if the {@link Entity} is a member of {@link #entities}. Returns false if the entity is queued
     * for membership.
     *
     * @param entity the entity
     * @return true if the entity is a member
     */
    public boolean entityIsMember(Entity entity) {
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
