package com.game.updatables;

import com.game.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@link Component} implementation for pre-processing.
 */
@Getter
@NoArgsConstructor
public class UpdatableComponent implements Component {

    private final Map<Updatable, Supplier<Boolean>> updatables = new HashMap<>();

    public UpdatableComponent(Updatable updatable) {
        putUpdatable(updatable);
    }

    public UpdatableComponent(Updatable updatable, Supplier<Boolean> doUpdate) {
        putUpdatable(updatable, doUpdate);
    }

    public void putUpdatable(Updatable updatable) {
        updatables.put(updatable, () -> true);
    }

    public void putUpdatable(Updatable updatable, Supplier<Boolean> doUpdate) {
        updatables.put(updatable, doUpdate);
    }

}
