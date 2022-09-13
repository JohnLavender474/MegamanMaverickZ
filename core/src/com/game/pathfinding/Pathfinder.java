package com.game.pathfinding;

import com.badlogic.gdx.math.Rectangle;
import com.game.graph.Graph;
import com.game.graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Pathfinder uses Djikstra's algorithm, based off pairOf implementation in link below.
 *
 * <a href="https://www.happycoders.eu/algorithms/dijkstras-algorithm-java/
 * #Dijkstras_Algorithm_-_Java_Source_Code_With_PriorityQueue">...</a>
 */
@RequiredArgsConstructor
public class Pathfinder implements Callable<Deque<Rectangle>> {

    private final Graph graph;
    private final PathfindingComponent pathfindingComponent;

    @Override
    public Deque<Rectangle> call() {
        PriorityQueue<NodeHandle> open = new PriorityQueue<>();
        Map<Node, NodeHandle> nodeHandleMap = new HashMap<>();
        Set<Node> closed = new HashSet<>();
        Node targetNode = graph.getNodeOfPos(pathfindingComponent.getTarget());
        NodeHandle startNodeHandle = new NodeHandle(graph.getNodeOfPos(pathfindingComponent.getStart()), 0, null);
        open.add(startNodeHandle);
        while (!open.isEmpty()) {
            NodeHandle currentNodeHandle = open.poll();
            Node currentNode = currentNodeHandle.getNode();
            closed.add(currentNode);
            if (currentNode.equals(targetNode)) {
                Deque<Rectangle> path = new ArrayDeque<>();
                while (currentNodeHandle != null) {
                    path.addFirst(currentNodeHandle.getNode().getBounds());
                    currentNodeHandle = currentNodeHandle.getPredecessor();
                }
                return path;
            }
            Set<Node> neighbors = graph.getNeighbors(currentNode, pathfindingComponent.allowDiagonal());
            for (Node neighbor : neighbors) {
                if (closed.contains(neighbor) || !pathfindingComponent.doAccept(neighbor)) {
                    continue;
                }
                int totalDistance = currentNodeHandle.getDistance() + graph.getCost(currentNode, neighbor);
                NodeHandle neighborNodeHandle = nodeHandleMap.get(neighbor);
                if (neighborNodeHandle == null) {
                    neighborNodeHandle = new NodeHandle(neighbor, totalDistance, currentNodeHandle);
                    nodeHandleMap.put(neighbor, neighborNodeHandle);
                    open.add(neighborNodeHandle);
                } else if (totalDistance < neighborNodeHandle.getDistance()) {
                    neighborNodeHandle.setDistance(totalDistance);
                    neighborNodeHandle.setPredecessor(currentNodeHandle);
                    open.remove(neighborNodeHandle);
                    open.add(neighborNodeHandle);
                }
            }
        }
        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static final class NodeHandle implements Comparable<NodeHandle> {

        private final Node node;
        private Integer distance;
        private NodeHandle predecessor;

        @Override
        public int compareTo(NodeHandle o) {
            return distance.compareTo(o.getDistance());
        }

        @Override
        public int hashCode() {
            return node.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof NodeHandle nodeHandle && node.equals(nodeHandle.getNode());
        }

    }

}
