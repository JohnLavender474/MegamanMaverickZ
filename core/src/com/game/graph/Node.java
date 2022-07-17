package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.core.IEntity;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Node extends Rectangle {

    private final int row;
    private final int col;
    private final Set<IEntity> entities = new HashSet<>();

    public Node(Vector2 dimensions, int row, int col) {
        this.row = row;
        this.col = col;
        set(dimensions.x * row, dimensions.y * col, dimensions.x, dimensions.y);
    }

    public void addEntity(IEntity entity) {
        entities.add(entity);
    }

    public boolean containsEntity(IEntity entity) {
        return entities.contains(entity);
    }

    public void clearEntities() {
        entities.clear();
    }

}
