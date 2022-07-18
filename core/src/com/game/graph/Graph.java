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

    public void set(Vector2 dimensions, int x, int y) {
        nodes = new Node[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
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

    public Node getNode(int row, int col) {
        return nodes[row][col];
    }

    public float distanceBetween(Node node1, Node node2) {
        return centerPoint(node1).dst(centerPoint(node2));
    }

    public List<Node> getNodesOverlapping(Rectangle rectangle) {
        int minX = Math.max(0, (int) Math.floor(rectangle.x));
        int minY = Math.max(0, (int) Math.floor(rectangle.y));
        int maxX = Math.min(nodes.length, (int) Math.ceil(rectangle.x + rectangle.width));
        int maxY = Math.min(nodes[0].length, (int) Math.ceil(rectangle.y + rectangle.height));
        List<Node> overlappingNodes = new ArrayList<>();
        for (int i = minX; i < maxX; i++) {
            overlappingNodes.addAll(asList(nodes[i]).subList(minY, maxY));
        }
        return overlappingNodes;
    }

    public List<Node> getNeighborsOf(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (int i = node.getX() - 1; i <= node.getX() + 1; i++) {
            neighbors.addAll(asList(nodes[i]).subList(node.getY() - 1, node.getY() + 2));
        }
        return neighbors;
    }

    public void forEach(Consumer<Node> consumer) {
        for (Node[] arr : nodes) {
            for (Node node : arr) {
                consumer.accept(node);
            }
        }
    }

}
