package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.graph.Graph;
import com.game.graph.Node;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.Float.*;

@RequiredArgsConstructor
public class Pathfinder implements Callable<Deque<Vector2>> {

    private final Graph graph;
    private final PathfindingComponent pathfindingComponent;

    @Override
    public Deque<Vector2> call() {
        Map<Node, NodeHandle> nodeHandles = new HashMap<>() {{
            graph.getNodes().forEach(node -> put(node, new NodeHandle(node)));
        }};
        Vector2 startPoint = pathfindingComponent.getStart();
        Vector2 targetPoint = pathfindingComponent.getTarget();
        NodeHandle startNodeHandle = nodeHandles.get(graph.getNode((int) startPoint.x, (int) startPoint.y));
        NodeHandle targetNodeHandle = nodeHandles.get(graph.getNode((int) targetPoint.x, (int) targetPoint.y));
        float xDistSquared = (float) Math.pow(targetNodeHandle.getNode().getX() - startNodeHandle.getNode().getX(), 2);
        float yDistSquared = (float) Math.pow(targetNodeHandle.getNode().getY() - startNodeHandle.getNode().getY(), 2);
        if (xDistSquared + yDistSquared > Math.pow(pathfindingComponent.getMaxSearchDistance(), 2)) {
            return new ArrayDeque<>();
        }
        Deque<NodeHandle> pathOfNodeHandles = new ArrayDeque<>();
        PriorityQueue<NodeHandle> open = new PriorityQueue<>((n1, n2) -> compare(n1.getFunction(), n2.getFunction()));
        open.add(startNodeHandle);
        while (!open.isEmpty()) {
            NodeHandle currentNodeHandle = open.poll();
            if (currentNodeHandle.equals(targetNodeHandle)) {
                while (currentNodeHandle != null) {
                    pathOfNodeHandles.addLast(currentNodeHandle);
                    currentNodeHandle = nodeHandles.get(currentNodeHandle.getPredecessor());
                }
                break;
            }
            open.remove(currentNodeHandle);
            currentNodeHandle.setDiscovered(true);
            List<Node> neighbours = graph.getNeighborsOf(currentNodeHandle.getNode());
            final NodeHandle refCurrentNodeHandle = currentNodeHandle;
            neighbours.stream().map(nodeHandles::get).forEach(nodeHandle -> {
                if (nodeHandle.isDiscovered() ||
                        nodeHandle.getNode().getObjects().stream().anyMatch(pathfindingComponent::doAvoid)) {
                    return;
                }
                float distance = Vector2.dst(refCurrentNodeHandle.getNode().getX(),
                        refCurrentNodeHandle.getNode().getY(),
                        nodeHandle.getNode().getX(), nodeHandle.getNode().getY());
                float tempScore = refCurrentNodeHandle.getCost() + distance;
                if (open.contains(nodeHandle)) {
                    if (tempScore < nodeHandle.getCost()) {
                        nodeHandle.setCost(tempScore);
                        nodeHandle.setPredecessor(refCurrentNodeHandle.getPredecessor());
                    }
                } else {
                    open.add(nodeHandle);
                    nodeHandle.setCost(tempScore);
                    nodeHandle.setPredecessor(refCurrentNodeHandle.getNode());
                }
                float heuristic = Vector2.dst(nodeHandle.getNode().getX(), nodeHandle.getNode().getY(),
                        targetNodeHandle.getNode().getX(), targetNodeHandle.getNode().getY());
                nodeHandle.setHeuristic(heuristic);
                nodeHandle.setFunction(nodeHandle.getCost() + nodeHandle.getHeuristic());
            });
        }
        return pathOfNodeHandles.stream().map(nodeHandle ->
                        new Vector2(nodeHandle.getNode().getX(), nodeHandle.getNode().getY()))
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

}
