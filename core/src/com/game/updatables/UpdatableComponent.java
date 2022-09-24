package com.game.updatables;

import com.game.core.Component;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Arrays.stream;

/** @link Component} implementation for pre-processing. */
@Getter
@NoArgsConstructor
public class UpdatableComponent extends Component {

    private final Set<QualifiedUpdatable> updatables = new HashSet<>();

    public UpdatableComponent(Updatable... updatables) {
        stream(updatables).forEach(this::addUpdatable);
    }

    public UpdatableComponent(QualifiedUpdatable... qualifiedUpdatables) {
        stream(qualifiedUpdatables).forEach(this::addUpdatable);
    }

    public UpdatableComponent(Updatable updatable, Supplier<Boolean> doUpdate) {
        addUpdatable(updatable, doUpdate);
    }

    public UpdatableComponent(Updatable updatable, Supplier<Boolean> doUpdate, Supplier<Boolean> doRemove) {
        addUpdatable(updatable, doUpdate, doRemove);
    }

    public void addUpdatable(QualifiedUpdatable qualifiedUpdatable) {
        updatables.add(qualifiedUpdatable);
    }

    public void addUpdatable(Updatable updatable) {
        addUpdatable(new QualifiedUpdatable(updatable));
    }

    public void addUpdatable(Updatable updatable, Supplier<Boolean> doUpdate) {
        addUpdatable(new QualifiedUpdatable(updatable, doUpdate, () -> false));
    }

    public void addUpdatable(Updatable updatable, Supplier<Boolean> doUpdate, Supplier<Boolean> doRemove) {
        addUpdatable(new QualifiedUpdatable(updatable, doUpdate, doRemove));
    }

}
