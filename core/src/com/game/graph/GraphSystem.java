package com.game.graph;

import com.game.System;
import com.game.core.IEntity;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
public class GraphSystem extends System {

    private Graph graph;

    public GraphSystem() {
        super(Set.of(GraphComponent.class));
    }

    @Override
    protected void preProcess(float delta) {
        graph.forEach(Node::clear);
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        GraphComponent graphComponent = entity.getComponent(GraphComponent.class);
        graphComponent.getSuppliers().forEach((boundsSupplier, objsSupplier) -> {
            List<Node> nodes = graph.getNodesOverlapping(boundsSupplier.get());
            nodes.forEach(node -> node.addAll(objsSupplier.get()));
        });
    }

}
