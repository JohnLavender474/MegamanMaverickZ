package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.System;
import com.game.core.IEntity;
import com.game.graph.Graph;
import com.game.graph.Node;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Setter
public class PathfindingSystem extends System {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<PathfindingComponent, Future<List<Vector2>>> pathfindingResults = new HashMap<>();

    private Graph graph;

    public PathfindingSystem() {
        super(Set.of(PathfindingComponent.class));
    }

    @Override
    protected void preProcess(float delta) {
        pathfindingResults.clear();
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
        if (!pathfindingComponent.doUpdate().apply(delta)) {
            return;
        }

    }

    private Callable<List<Vector2>> findPath(PathfindingComponent pathfindingComponent) {
        return () -> {
            List<Vector2> path = new ArrayList<>();
            PriorityQueue<NodeHandle> open = new PriorityQueue<>((n1, n2) ->
                    Float.compare(n1.getFunction(), n2.getFunction()));
            open.addAll(graph.getNodes().stream().map(NodeHandle::new).toList());
            Set<NodeHandle> closed = new HashSet<>();
            return path;
        };
    }

}
