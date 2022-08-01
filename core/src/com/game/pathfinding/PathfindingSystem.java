package com.game.pathfinding;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.System;
import com.game.core.IEntity;
import com.game.graph.Graph;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        Deque<Rectangle> path = pathfindingComponent.getCurrentPath();
        if (path != null) {
            while (!path.isEmpty() && pathfindingComponent.hasReachedTarget(path.peek())) {
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
        try {
            List<Future<Deque<Rectangle>>> pathfindingResults = executorService.invokeAll(pathfinders);
            for (int i = 0; i < pathfindingResults.size(); i++) {
                PathfindingComponent pathfindingComponent = pathfindingComponents.get(i);
                Deque<Rectangle> pathFindingResult = pathfindingResults.get(i).get();
                if (pathFindingResult == null && pathfindingComponent.persistOldPath()) {
                    continue;
                }
                pathfindingComponent.setCurrentPath(pathFindingResult);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
