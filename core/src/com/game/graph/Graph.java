package com.game.graph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.game.shapes.custom.Triangle;
import com.game.utils.objects.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Math.*;

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

    public Pair<Pair<Integer>> getNodeIndexes(Shape2D shape) {
        Rectangle bounds;
        if (shape instanceof Rectangle) {
            bounds = (Rectangle) shape;
        } else if (shape instanceof Circle circle) {
            bounds = new Rectangle(circle.x - circle.radius, circle.y - circle.radius,
                    circle.x + circle.radius, circle.y + circle.radius);
        } else if (shape instanceof Triangle triangle) {
            bounds = triangle.getBoundingRectangle();
        } else if (shape instanceof Polygon polygon) {
            bounds = polygon.getBoundingRectangle();
        } else if (shape instanceof Polyline polyline) {
            bounds = polyline.getBoundingRectangle();
        } else {
            throw new UnsupportedOperationException("Unsupported shape");
        }
        Pair<Integer> first = new Pair<>();
        int minX = Integer.max(0, (int) (bounds.x / dimensions.x));
        int minY = Integer.max(0, (int) (bounds.y / dimensions.y));
        first.set(minX, minY);
        Pair<Integer> second = new Pair<>();
        int maxX = Integer.min(nodes.length - 1,(int) ((bounds.x + bounds.width) / dimensions.x));
        int maxY = Integer.min(nodes[0].length - 1, (int) ((bounds.y + bounds.height) / dimensions.y));
        second.set(maxX, maxY);
        return new Pair<>(first, second);
    }

    public void addObjToNodes(Object o, Shape2D bounds) {
        Pair<Pair<Integer>> p = getNodeIndexes(bounds);
        addObjToNodes(o, p);
    }

    public void addObjToNodes(Object o, Pair<Pair<Integer>> p) {
        Pair<Integer> min = p.getFirst();
        Pair<Integer> max = p.getSecond();
        for (int i = min.getFirst(); i <= max.getFirst(); i++) {
            for (int j = min.getSecond(); j <= max.getSecond(); j++) {
                Node node = getNode(i, j);
                node.add(o);
            }
        }
    }

    public int getCost(Node n1, Node n2) {
        return abs(n1.getX() - n2.getX()) + abs(n1.getY() - n2.getY());
    }

    public Set<Node> getNeighbors(Node node, boolean allowDiagonal) {
        int x = node.getX();
        int y = node.getY();
        Set<Node> neighbors = new HashSet<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((i == x && j == y) || isOutOfBounds(i, y)) {
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
        if (isOutOfBounds(row, col)) {
            return null;
        }
        return nodes[row][col];
    }

    public boolean isOutOfBounds(int row, int col) {
        return row < 0 || col < 0 || row >= nodes.length || col >= nodes[0].length;
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

    public void forEach(Consumer<Node> consumer) {
        for (Node[] arr : nodes) {
            for (Node node : arr) {
                consumer.accept(node);
            }
        }
    }

    public void clearNodeObjs() {
        forEach(Node::clear);
    }

    public void draw(ShapeRenderer shapeRenderer, Color color) {
        draw(shapeRenderer, node -> color);
    }

    public void draw(ShapeRenderer shapeRenderer, Function<Node, Color> colorFunc) {
        forEach(node -> {
            Color color = colorFunc.apply(node);
            if (color == null) {
                return;
            }
            shapeRenderer.setColor(color);
            Rectangle bounds = node.getBounds();
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        });
    }

}
