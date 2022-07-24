package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.System;
import com.game.core.IEntity;
import com.game.graph.Graph;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Setter
public class PathfindingSystem extends System {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<PathfindingComponent> pathfindingComponents = new ArrayList<>();
    private final List<Pathfinder> pathfinders = new ArrayList<>();

    private Graph graph;

    public PathfindingSystem(List<Runnable> runOnShutdown) {
        super(Set.of(PathfindingComponent.class));
        runOnShutdown.add(executorService::shutdownNow);
    }

    @Override
    protected void preProcess(float delta) {
        pathfindingComponents.clear();
        pathfinders.clear();
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
        Deque<Vector2> path = pathfindingComponent.getCurrentPath();
        if (path != null && !path.isEmpty()) {
            if (pathfindingComponent.hasReachedTarget(path.peek())) {
                path.poll();
            }
            if (path.peek() != null) {
                pathfindingComponent.consumeTarget(path.peek());
            }
        }
        if (pathfindingComponent.doUpdate(delta)) {
            pathfindingComponents.add(pathfindingComponent);
            pathfinders.add(new Pathfinder(graph, pathfindingComponent));
        }
    }

    @Override
    protected void postProcess(float delta) {
        for (int i = 0; i < pathfinders.size(); i++) {
            Pathfinder pathfinder = pathfinders.get(i);
            Deque<Vector2> path = pathfinder.call();
            pathfindingComponents.get(i).setCurrentPath(path);
        }
        /*
        try {
            List<Future<Deque<Vector2>>> pathfindingResults = executorService.invokeAll(pathfinders);
            for (int i = 0; i < pathfindingResults.size(); i++) {
                PathfindingComponent pathfindingComponent = pathfindingComponents.get(i);
                Future<Deque<Vector2>> pathFindingResult = pathfindingResults.get(i);
                pathfindingComponent.setCurrentPath(pathFindingResult.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
         */
    }

}
