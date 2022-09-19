package com.game.updatables;

import com.game.core.Component;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** @link Component} implementation for pre-processing. */
@Getter
@NoArgsConstructor
public class UpdatableComponent extends Component {

    private final Set<QualifiedUpdatable> updatables = new HashSet<>();

    public UpdatableComponent(Updatable updatable) {
        putUpdatable(updatable);
    }

    public UpdatableComponent(Updatable updatable, Supplier<Boolean> doUpdate) {
        putUpdatable(updatable, doUpdate);
    }

    public UpdatableComponent(Updatable updatable, Supplier<Boolean> doUpdate, Supplier<Boolean> doRemove) {
        putUpdatable(updatable, doUpdate, doRemove);
    }

    public void putUpdatable(Updatable updatable) {
        updatables.add(new QualifiedUpdatable(updatable));
    }

    public void putUpdatable(Updatable updatable, Supplier<Boolean> doUpdate) {
        updatables.add(new QualifiedUpdatable(updatable, doUpdate, () -> false));
    }

    public void putUpdatable(Updatable updatable, Supplier<Boolean> doUpdate, Supplier<Boolean> doRemove) {
        updatables.add(new QualifiedUpdatable(updatable, doUpdate, doRemove));
    }

}
