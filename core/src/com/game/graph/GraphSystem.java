package com.game.graph;

import com.game.core.Entity;
import com.game.core.System;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
public class GraphSystem extends System {

    private Graph graph;

    public GraphSystem() {
        super(GraphComponent.class);
    }

    @Override
    protected void preProcess(float delta) {
        graph.forEach(Node::clear);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        GraphComponent graphComponent = entity.getComponent(GraphComponent.class);
        graphComponent.getSuppliers().forEach((boundsSupplier, objsSupplier) -> {
            List<Node> nodes = graph.getNodesOverlapping(boundsSupplier.get());
            nodes.forEach(node -> node.addAll(objsSupplier.get()));
        });
    }

}
