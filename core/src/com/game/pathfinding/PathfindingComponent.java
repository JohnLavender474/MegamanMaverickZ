package com.game.pathfinding;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Component;
import com.game.graph.Node;
import com.game.utils.UtilMethods;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Setter
@RequiredArgsConstructor
public class PathfindingComponent extends Component {

    private final Supplier<Vector2> startSupplier;
    private final Supplier<Vector2> targetSupplier;
    private final Consumer<Rectangle> targetConsumer;
    private final Predicate<Rectangle> hasReachedTarget;

    private Supplier<Boolean> persistOldPath = () -> true;
    private Supplier<Boolean> doAllowDiagonal = () -> true;
    private Predicate<Node> doAcceptPredicate = objs -> true;
    private Predicate<Float> doRefreshPredicate = delta -> true;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Deque<Rectangle> currentPath;


    public List<Vector2> getPathPoints() {
        return currentPath != null ? currentPath.stream().map(UtilMethods::centerPoint)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    boolean persistOldPath() {
        return persistOldPath.get();
    }

    Vector2 getStart() {
        return startSupplier.get();
    }

    Vector2 getTarget() {
        return targetSupplier.get();
    }

    boolean doAccept(Node node) {
        return doAcceptPredicate.test(node);
    }

    void consumeTarget(Rectangle target) {
        targetConsumer.accept(target);
    }

    boolean doRefresh(float delta) {
        return doRefreshPredicate.test(delta);
    }

    boolean hasReachedTarget(Rectangle target) {
        return hasReachedTarget.test(target);
    }

    boolean allowDiagonal() {
        return doAllowDiagonal.get();
    }

}
