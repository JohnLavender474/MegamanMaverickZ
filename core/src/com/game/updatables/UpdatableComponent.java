package com.game.updatables;

import com.game.Component;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static lombok.AccessLevel.*;

/** {@link Component} implementation for updates that do not pertain to any other component type. */
@Getter(PACKAGE)
@NoArgsConstructor
public class UpdatableComponent extends Component {

    private final List<QualifiedUpdatable> updatables = new ArrayList<>();

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
