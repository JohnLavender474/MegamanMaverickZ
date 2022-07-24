package com.game.graph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

public class Graph {

    private final Vector2 dimensions = new Vector2();

    private Node[][] nodes;

    public Graph(Vector2 dimensions, int x, int y) {
        this.dimensions.set(dimensions);
        nodes = new Node[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                nodes[i][j] = new Node(i, j, this, new Rectangle(
                        dimensions.x * i, dimensions.y * j, dimensions.x, dimensions.y));
            }
        }
    }

    public int getCost(Node n1, Node n2) {
        return Math.abs(n1.getX() - n2.getX()) + Math.abs(n1.getY() - n2.getY());
    }

    public Set<Node> getNeighbors(Node node, boolean allowDiagonal) {
        int x = node.getX();
        int y = node.getY();
        Set<Node> neighbors = new HashSet<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((i == x && j == y) || i < 0 || i >= nodes.length || j < 0 || j >= nodes[0].length) {
                    continue;
                }
                if (!allowDiagonal && (i == x - 1 || i == x + 1) && (j == y - 1 || j == y + 1)) {
                    continue;
                }
                neighbors.add(nodes[i][j]);
            }
        }
        return neighbors;
    }

    public Node getNode(int row, int col) {
        return nodes[row][col];
    }

    public Node getNodeOfPos(float x, float y) {
        int row = (int) (x / dimensions.x);
        if (row < 0) {
            row = 0;
        } else if (row >= nodes.length) {
            row = nodes.length - 1;
        }
        int col = (int) (y / dimensions.y);
        if (col < 0) {
            col = 0;
        } else if (col >= nodes[0].length) {
            col = nodes[0].length - 1;
        }
        return getNode(row, col);
    }

    public Node getNodeOfPos(Vector2 pos) {
        return getNodeOfPos(pos.x, pos.y);
    }

    public List<Node> getNodesOverlapping(Rectangle rectangle) {
        List<Node> overlappingNodes = new ArrayList<>();
        forEach(node -> {
            if (node.getBounds().overlaps(rectangle)) {
                overlappingNodes.add(node);
            }
        });
        return overlappingNodes;
    }

    public void forEach(Consumer<Node> consumer) {
        for (Node[] arr : nodes) {
            for (Node node : arr) {
                consumer.accept(node);
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer, Color color) {
        draw(shapeRenderer, node -> color);
    }

    public void draw(ShapeRenderer shapeRenderer, Function<Node, Color> colorFunc) {
        boolean isDrawing = shapeRenderer.isDrawing();
        if (!isDrawing) {
            shapeRenderer.begin(Line);
        }
        forEach(node -> {
            Color color = colorFunc.apply(node);
            if (color == null) {
                return;
            }
            shapeRenderer.setColor(color);
            Rectangle bounds = node.getBounds();
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        });
        if (!isDrawing) {
            shapeRenderer.end();
        }
    }

}
