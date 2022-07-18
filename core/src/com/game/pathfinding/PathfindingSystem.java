package com.game.pathfinding;

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

    public PathfindingSystem() {
        super(Set.of(PathfindingComponent.class));
    }

    @Override
    protected void preProcess(float delta) {
        pathfindingComponents.clear();
        pathfinders.clear();
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
        pathfindingComponent.consumeCurrentPath();
        if (!pathfindingComponent.doUpdate(delta)) {
            return;
        }
        pathfindingComponents.add(pathfindingComponent);
        pathfinders.add(new Pathfinder(graph, pathfindingComponent));
    }

    @Override
    protected void postProcess(float delta) {
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
    }

}
