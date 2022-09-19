package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.graph.Graph;
import com.game.graph.Node;
import com.game.levels.LevelTiledMap;
import com.game.utils.objects.Pair;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.math.Intersector.*;
import static com.game.core.constants.ViewVals.PPM;

public class WorldGraph {

    private final Graph graph;

    public WorldGraph(LevelTiledMap levelMap) {
        graph = new Graph(new Vector2(PPM, PPM), levelMap.getWidthInTiles(), levelMap.getHeightInTiles());
    }

    public void addBody(BodyComponent bodyComponent) {
        Pair<Pair<Integer>> indexes = graph.getNodeIndexes(bodyComponent.getCollisionBox());
        Pair<Integer> min = indexes.getFirst();
        Pair<Integer> max = indexes.getSecond();
        for (int i = min.getFirst(); i <= max.getFirst(); i++) {
            for (int j = min.getSecond(); j <= max.getSecond(); j++) {
                Node node = graph.getNode(i, j);
                node.add(bodyComponent);
            }
        }
    }

    public Map<BodyComponent, Rectangle> getBodiesOverlapping(BodyComponent bodyComponent) {
        Pair<Pair<Integer>> indexes = graph.getNodeIndexes(bodyComponent.getCollisionBox());
        Pair<Integer> min = indexes.getFirst();
        Pair<Integer> max = indexes.getSecond();
        Map<BodyComponent, Rectangle> map = new HashMap<>();
        for (int i = min.getFirst(); i <= max.getFirst(); i++) {
            for (int j = min.getSecond(); j <= max.getSecond(); j++) {
                Node node = graph.getNode(i, j);
                node.getObjects().forEach(o -> {
                    BodyComponent c = (BodyComponent) o;
                    Rectangle overlap = new Rectangle();
                    if (intersectRectangles(bodyComponent.getCollisionBox(), c.getCollisionBox(), overlap)) {
                        map.put(c, overlap);
                    }
                });
            }
        }
        return map;
    }

    public void clear() {
        graph.clearNodeObjs();
    }

}
