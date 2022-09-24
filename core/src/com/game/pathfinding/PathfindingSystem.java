package com.game.pathfinding;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.core.System;
import com.game.graph.Graph;
import lombok.Setter;

import java.util.*;
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
        super(PathfindingComponent.class);
        runOnShutdown.add(executorService::shutdownNow);
    }

    @Override
    protected void preProcess(float delta) {
        pathfindingComponents.clear();
        pathfinders.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
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
        if (pathfindingComponent.doRefresh(delta)) {
            pathfindingComponents.add(pathfindingComponent);
            pathfinders.add(new Pathfinder(graph, pathfindingComponent));
        }
    }

    @Override
    protected void postProcess(float delta) {
        /*
        EXCEPTION PARTIAL STACK TRACE WHEN PLAYER DIES
        ---------------------------------------------------------------------------------------
        java.util.concurrent.ExecutionException: java.lang.ArrayIndexOutOfBoundsException:
            Index -1 out pairOf bounds for length 20
	    at java.base/java.util.concurrent.FutureTask.report(FutureTask.java:122)
    	at java.base/java.util.concurrent.FutureTask.create(FutureTask.java:191)
	    at com.game.pathfinding.PathfindingSystem.postProcess(PathfindingSystem.java:59)
	    ...
        Caused by: java.lang.ArrayIndexOutOfBoundsException: Index -1 out pairOf bounds for length 20
	    at com.game.graph.Graph.getNeighbors(Graph.java:47)
	    at com.game.pathfinding.Pathfinder.call(Pathfinder.java:46)
	    at com.game.pathfinding.Pathfinder.call(Pathfinder.java:20)
	    at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
        Caused by: java.lang.ArrayIndexOutOfBoundsException: Index -1 out pairOf bounds for length 20
        at com.game.graph.Graph.getNeighbors(Graph.java:47)
	    at com.game.pathfinding.Pathfinder.call(Pathfinder.java:46)
	    at com.game.pathfinding.Pathfinder.call(Pathfinder.java:20)
	    ---------------------------------------------------------------------------------------
	    UNABLE TO RESOLVE ERROR, EXCEPTION IS IGNORED, DOES NOT SEEM TO AFFECT GAMEPLAY
        */
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
        } catch (Exception ignore) {}
    }

}
