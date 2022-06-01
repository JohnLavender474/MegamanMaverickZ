package com.mygdx.game;

import com.mygdx.game.utils.Updatable;
import com.mygdx.game.utils.exceptions.InvalidActionException;
import lombok.*;

import java.util.*;

import static com.mygdx.game.utils.UtilMethods.objName;

/**
 * The base class of game systems. Instances of this class perform game logic on a set of {@link Entity} instances.
 * Entities are eligible to be added to a System only if {@link Entity#hasAllComponents(Collection)} contains all the
 * elements of {@link #getComponentMask}.
 */
@ToString
@RequiredArgsConstructor
public abstract class System implements Updatable {

    @Getter
    private boolean updating;
    @Getter
    private final SystemType systemType;
    @Setter
    private Comparator<Entity> entityComparator;
    private final List<Entity> entities = new ArrayList<>();
    private final Queue<Entity> entitiesToAddQueue = new LinkedList<>();
    private final Queue<Entity> entitiesToRemoveQueue = new LinkedList<>();

    /**
     * Defines the set of {@link GameState} that designates when this System should not be processed.
     *
     * @return the set of switch off game states
     */
    public abstract Set<GameState> getSwitchOffStates();

    /**
     * Defines the set of {@link Class<Component>} instances that designates the component mask of this System.
     * See {@link #qualifiesMembership(Entity)}.
     *
     * @return the set of component class instances for masking
     */
    public abstract Set<Class<? extends Component>> getComponentMask();

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

    @Override
    public void update(float delta) {
        while (!entitiesToAddQueue.isEmpty()) {
            entities.add(entitiesToAddQueue.poll());
        }
        while (!entitiesToRemoveQueue.isEmpty()) {
            entities.remove(entitiesToRemoveQueue.poll());
        }
        if (entityComparator != null) {
            entities.sort(entityComparator);
        }
        updating = true;
        preProcess(delta);
        entities.forEach(entity -> processEntity(entity, delta));
        postProcess(delta);
        updating = false;
    }

    /**
     * Returns if the {@link Entity} can be accepted as a member of this System by comparing {@link #getComponentMask()}
     * to {@link Entity#hasAllComponents(Collection)}. If the Entity's set of component keys contains all the component
     * classes contained in this System's component mask, then the Entity is accepted, otherwise the Entity is rejected.
     *
     * @param entity the entity
     * @return true if the Entity can be added, else false
     */
    public boolean qualifiesMembership(Entity entity) {
        return entity.hasAllComponents(getComponentMask());
    }

    /**
     * Attempts to add the {@link Entity} as a member, returns true if the attempt is successful, else false.
     *
     * @param entity the entity
     * @throws InvalidActionException the invalid action exception
     */
    public void addEntity(Entity entity)
            throws InvalidActionException {
        if (!qualifiesMembership(entity)) {
            throw new InvalidActionException("Cannot add " + objName(entity) + " as member of " + this);
        }
        if (isUpdating()) {
            entitiesToAddQueue.add(entity);
        } else {
            entities.add(entity);
        }
    }

    /**
     * Attempts to remove the {@link Entity} from membership to this System. If this System is currently in an
     * update cycle, then the Entity is queued to be removed on the next update cycle, else it is removed immediately.
     *
     * @param entity the entity
     */
    public void removeEntity(Entity entity) {
        if (isUpdating()) {
            entitiesToRemoveQueue.add(entity);
        } else {
            entities.remove(entity);
        }
    }

    /**
     * Copy of {@link Entity} list.
     *
     * @return the copy set of entities
     */
    public List<Entity> getUnmodifiableCopyOfListOfEntities() {
        return Collections.unmodifiableList(entities);
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
     * Returns if the {@link Entity} is queued for membership but not yet a member.
     *
     * @param entity the entity
     * @return true if the entity is queued for membership
     */
    public boolean entityIsQueuedForMembership(Entity entity) {
        return entitiesToAddQueue.contains(entity);
    }

    /**
     * Returns if the {@link Entity} is queued for removal.
     *
     * @param entity the entity
     * @return true if the entity is queued for removal
     */
    public boolean entityIsQueuedForRemoval(Entity entity) {
        return entitiesToRemoveQueue.contains(entity);
    }

}
