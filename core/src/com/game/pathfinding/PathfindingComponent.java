package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PathfindingComponent implements Component {

    private final Consumer<Deque<Vector2>> pathConsumer;
    private final Function<Float, Boolean> doUpdateFunc;
    private final Function<Object, Boolean> doAvoidFunc;
    private final Supplier<Float> maxSearchDistSupplier;
    private final Supplier<Boolean> allowDiagonalSupplier;
    private final Supplier<Vector2> startSupplier;
    private final Supplier<Vector2> targetSupplier;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Deque<Vector2> currentPath;

    void consumeCurrentPath() {
        if (currentPath == null) {
            return;
        }
        pathConsumer.accept(currentPath);
    }

    boolean doUpdate(float delta) {
        return doUpdateFunc.apply(delta);
    }

    boolean doAvoid(Object o) {
        return doAvoidFunc.apply(o);
    }

    float getMaxSearchDistance() {
        return maxSearchDistSupplier.get();
    }

    boolean doAllowDiagonal() {
        return allowDiagonalSupplier.get();
    }

    Vector2 getStart() {
        return startSupplier.get();
    }

    Vector2 getTarget() {
        return targetSupplier.get();
    }

}
