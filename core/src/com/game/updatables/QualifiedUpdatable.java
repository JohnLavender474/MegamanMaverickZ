package com.game.updatables;

import com.game.utils.interfaces.Updatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QualifiedUpdatable implements Updatable {

    @Getter
    private Updatable updatable = delta -> {};
    private Supplier<Boolean> doUpdateSupplier = () -> true;
    private Supplier<Boolean> doRemoveSupplier = () -> false;

    public QualifiedUpdatable(Updatable updatable) {
        setUpdatable(updatable);
    }

    public boolean doUpdate() {
        return doUpdateSupplier != null && doUpdateSupplier.get();
    }

    public boolean doRemove() {
        return doRemoveSupplier != null && doRemoveSupplier.get();
    }

    @Override
    public void update(float delta) {
        updatable.update(delta);
    }

}
