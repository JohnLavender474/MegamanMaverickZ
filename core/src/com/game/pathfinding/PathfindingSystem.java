package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.System;
import com.game.core.IEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PathfindingSystem extends System {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<PathfindingComponent, Future<List<Vector2>>> pathfindingResults = new HashMap<>();

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



}
