package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.graph.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Setter
@RequiredArgsConstructor
public class PathfindingComponent implements Component {

    private final Supplier<Vector2> startSupplier;
    private final Supplier<Vector2> targetSupplier;
    private final Consumer<Vector2> targetConsumer;
    private final Function<Vector2, Boolean> hasReachedTarget;

    private Supplier<Boolean> doAllowDiagonal = () -> true;
    private Function<Node, Boolean> doAvoidFunc = node -> false;
    private Function<Float, Boolean> doUpdateFunc = delta -> true;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Deque<Vector2> currentPath;

    /**
     * Returns a list copy of the current path. Modifications to the returned list are NOT reflected in the original.
     *
     * @return list copy of the current path
     */
    public List<Vector2> getCurrentPathCpy() {
        return currentPath != null ? new ArrayList<>(currentPath) : new ArrayList<>();
    }

    Vector2 getStart() {
        return startSupplier.get();
    }

    Vector2 getTarget() {
        return targetSupplier.get();
    }

    boolean doAvoid(Node node) {
        return doAvoidFunc.apply(node);
    }

    void consumeTarget(Vector2 target) {
        targetConsumer.accept(target);
    }

    boolean doUpdate(float delta) {
        return doUpdateFunc.apply(delta);
    }

    boolean hasReachedTarget(Vector2 target) {
        return hasReachedTarget.apply(target);
    }

    boolean allowDiagonal() {
        return doAllowDiagonal.get();
    }

}
