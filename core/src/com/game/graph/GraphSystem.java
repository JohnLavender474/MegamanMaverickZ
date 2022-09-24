package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.game.Entity;
import com.game.System;
import com.game.utils.objects.Pair;
import lombok.Setter;

@Setter
public class GraphSystem extends System {

    private Graph graph;

    public GraphSystem() {
        super(GraphComponent.class);
    }

    @Override
    protected void preProcess(float delta) {
        graph.clearNodeObjs();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        GraphComponent graphComponent = entity.getComponent(GraphComponent.class);
        graphComponent.getSuppliers().forEach((boundsSupplier, objsSupplier) -> {
            Rectangle bounds = boundsSupplier.get();
            Pair<Pair<Integer>> indexes = graph.getNodeIndexes(bounds);
            Pair<Integer> min = indexes.getFirst();
            Pair<Integer> max = indexes.getSecond();
            for (int i = min.getFirst(); i <= max.getFirst(); i++) {
                for (int j = min.getSecond(); j <= max.getSecond(); j++) {
                    Node node = graph.getNode(i, j);
                    if (node.getBounds().overlaps(bounds)) {
                        node.addAll(objsSupplier.get());
                    }
                }
            }
        });
    }

}
