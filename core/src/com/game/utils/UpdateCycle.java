package com.game.utils;

import com.game.updatables.Updatable;
import com.game.utils.exceptions.InvalidArgumentException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A container for all {@link Updatable} objects to be updated each frame in the game cycle. The objects are updated
 * in the order in which they are inserted.
 */
public class UpdateCycle implements Updatable {

    private final Map<Updatable, Supplier<Boolean>> updatables = new LinkedHashMap<>();

    /**
     * Adds {@link Updatable} and new {@link Supplier<Boolean>} that always returns true to end of
     * {@link #updatables} list.
     *
     * @param updatable the updatable
     */
    public void addUpdatable(Updatable updatable) {
        addUpdatable(updatable, () -> true);
    }

    /**
     * Adds {@link Updatable} and {@link Supplier<Boolean>} to end of {@link #updatables} list.
     *
     * @param updatable the updatable
     * @param doUpdate  the do update boolean supplier
     * @throws InvalidArgumentException thrown if updatable or doUpdate is null
     */
    public void addUpdatable(Updatable updatable, Supplier<Boolean> doUpdate) throws InvalidArgumentException {
        if (updatable == null || doUpdate == null) {
            throw new InvalidArgumentException(updatable + " and " + doUpdate, "updatable and doUpdate");
        }
        updatables.put(updatable, doUpdate);
    }

    /**
     * Clears all entries from {@link #updatables} list.
     */
    public void clearUpdatables() {
        updatables.clear();
    }

    @Override
    public void update(float delta) {
        updatables.forEach((key, value) -> {
            if (value.get()) {
                key.update(delta);
            }
        });
    }

}
