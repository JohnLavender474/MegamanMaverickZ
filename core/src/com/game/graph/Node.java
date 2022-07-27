package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.game.utils.UtilMethods.*;

@Getter
public class Node {

    private final int x;
    private final int y;
    private final Graph graph;
    private final Rectangle bounds = new Rectangle();
    private final Set<Object> objects = new HashSet<>();

    public Node(int x, int y, Graph graph, Rectangle bounds) {
        this.x = x;
        this.y = y;
        this.graph = graph;
        this.bounds.set(bounds);
    }

    public Set<Node> getNeighbors(boolean allowDiagonal) {
        return graph.getNeighbors(this, allowDiagonal);
    }

    public Node getNodeOffset(int offsetX, int offsetY) {
        return graph.getNode(x + offsetX, y + offsetY);
    }

    public void addAll(Collection<Object> objs) {
        objs.forEach(this::add);
    }

    public void add(Object o) {
        objects.add(o);
    }

    public void clear() {
        objects.clear();
    }

    public Vector2 getCenter() {
        return centerPoint(bounds);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Node node && x == node.getX() && y == node.getY();
    }

    @Override
    public int hashCode() {
        int hashcode = 7;
        hashcode += 27 * x;
        hashcode += 49 * y;
        return hashcode;
    }

}
