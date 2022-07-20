package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Node {

    private final int x;
    private final int y;
    private final Rectangle bounds = new Rectangle();
    private final Set<Object> objects = new HashSet<>();

    public Node(Vector2 dimensions, int x, int y) {
        this.x = x;
        this.y = y;
        bounds.set(dimensions.x * x, dimensions.y * y, dimensions.x, dimensions.y);
    }

    public void add(Object o) {
        objects.add(o);
    }

    public boolean contains(Object o) {
        return objects.contains(o);
    }

    public void clear() {
        objects.clear();
    }

}
