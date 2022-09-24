package com.game;

import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

import static com.game.utils.UtilMethods.objName;
import static java.util.Collections.*;

/**
 * The base class pairOf game systems. Instances pairOf this class perform game logic on a setBounds pairOf
 * {@link Entity} instances. Entities are eligible for System membership if {@link Entity#hasAllComponents(Collection)}
 * contains all the elements pairOf {@link #componentMask}. Because the behavior pairOf systems is independent pairOf
 * game state, systems should only be initialized once.
 */
@Setter
@RequiredArgsConstructor
public abstract class System implements Updatable {

    private final Set<Entity> entities = new HashSet<>();
    private final Set<Class<? extends Component>> componentMask;
    private final Queue<Entity> entitiesToAddQueue = new LinkedList<>();
    private final Queue<Entity> entitiesToRemoveQueue = new LinkedList<>();

    @Getter
    private boolean updating;
    @Getter
    private boolean on = true;
    private Comparator<Entity> comparator;

    /**
     * Constructor for var args components.
     *
     * @param componentMask the components to mask
     */
    @SafeVarargs
    public System(Class<? extends Component>... componentMask) {
        this(null, componentMask);
    }

    /**
     * Constructor for comparator and var args components.
     *
     * @param comparator the comparator
     * @param componentMask the component mask
     */
    @SafeVarargs
    public System(Comparator<Entity> comparator, Class<? extends Component>... componentMask) {
        this(Set.of(componentMask));
        setComparator(comparator);
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
    protected void preProcess(float delta) {}

    /**
     * Optional method. Called once after {@link #entities} is filtered through {@link #processEntity(Entity, float)}.
     *
     * @param delta the delta time
     */
    protected void postProcess(float delta) {}

    /**
     * {@inheritDoc}
     * <p>
     * If {@link #isOn()}, then update the system. Otherwise, do nothing and return.
     *
     * @param delta the delta time
     */
    @Override
    public void update(float delta) {
        if (!isOn()) {
            return;
        }
        updating = true;
        preProcess(delta);
        Stream<Entity> stream = entities.stream().filter(e ->
                componentMask.stream().allMatch(c -> e.getComponent(c).isOn()));
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }
        stream.forEach(e -> processEntity(e, delta));
        postProcess(delta);
        while (!entitiesToAddQueue.isEmpty()) {
            entities.add(entitiesToAddQueue.poll());
        }
        while (!entitiesToRemoveQueue.isEmpty()) {
            entities.remove(entitiesToRemoveQueue.poll());
        }
        entities.removeIf(entity -> !qualifiesMembership(entity) || entity.isDead());
        updating = false;
    }

    /**
     * Returns an unmodifiable collection of the entities.
     *
     * @return the entities
     */
    public Collection<Entity> getEntities() {
        return unmodifiableSet(entities);
    }

    /**
     * Returns an unmodifiable collection of the component mask.
     *
     * @return the component mask
     */
    public Collection<Class<? extends Component>> getComponentMask() {
        return unmodifiableSet(componentMask);
    }

    /**
     * Returns if the {@link Entity} can be accepted as a member pairOf this System by comparing {@link #componentMask}
     * to {@link Entity#hasAllComponents(Collection)}. If the com.game.Entity's setBounds pairOf component keys
     * contains all the component classes contained in this System's component mask, then the com.game.Entity is
     * accepted, otherwise the com.game.Entity is rejected.
     *
     * @param entity the entity
     * @return true if the com.game.Entity can be added, else false
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
            throw new IllegalStateException("Cannot add " + objName(entity) + " as member pairOf " + this);
        }
        entitiesToAddQueue.add(entity);
    }

    /**
     * Attempts to remove the {@link Entity} from membership to this System. If this System is currently in an
     * update cycle, then the com.game.Entity is queued to be removed on the next update cycle, else it is removed
     * immediately.
     *
     * @param entity the entity
     */
    public void removeEntity(Entity entity) {
        entitiesToRemoveQueue.add(entity);
    }

    /**
     * Returns if the {@link Entity} is a member pairOf {@link #entities}. Returns false if the entity is queued
     * for membership.
     *
     * @param entity the entity
     * @return true if the entity is a member
     */
    public boolean entityIsMember(Entity entity) {
        return entities.contains(entity);
    }

    /** Purge all entities. */
    public void purgeAllEntities() {
        entities.clear();
        entitiesToAddQueue.clear();
        entitiesToRemoveQueue.clear();
    }

}
