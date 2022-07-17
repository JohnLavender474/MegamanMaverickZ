package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.game.utils.UtilMethods.*;
import static java.util.Arrays.*;

public class Graph {

    private Node[][] nodes;

    public void set(Vector2 dimensions, int rows, int cols) {
        nodes = new Node[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                nodes[i][j] = new Node(dimensions, i, j);
            }
        }
    }

    public List<Node> getNodes() {
        List<Node> nodeList = new ArrayList<>();
        for (Node[] row : nodes) {
            nodeList.addAll(Arrays.asList(row));
        }
        return nodeList;
    }

    public float distanceBetween(Node node1, Node node2) {
        return centerPoint(node1).dst(centerPoint(node2));
    }

    public List<Node> getNodesOverlapping(Rectangle rectangle) {
        int minRow = Math.max(0, (int) Math.floor(rectangle.x));
        int minCol = Math.max(0, (int) Math.floor(rectangle.y));
        int maxRow = Math.min(nodes.length, (int) Math.ceil(rectangle.x + rectangle.width));
        int maxCol = Math.min(nodes[0].length, (int) Math.ceil(rectangle.y + rectangle.height));
        List<Node> overlappingNodes = new ArrayList<>();
        for (int i = minRow; i < maxRow; i++) {
            overlappingNodes.addAll(asList(nodes[i]).subList(minCol, maxCol));
        }
        return overlappingNodes;
    }

    public List<Node> getNeighborsOf(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (int i = node.getRow() - 1; i <= node.getRow() + 1; i++) {
            neighbors.addAll(asList(nodes[i]).subList(node.getCol() - 1, node.getCol() + 2));
        }
        return neighbors;
    }

    public void forEach(Consumer<Node> consumer) {
        for (Node[] row : nodes) {
            for (Node node : row) {
                consumer.accept(node);
            }
        }
    }

}
