package com.game.pathfinding;

import com.game.graph.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeHandle {
    private float cost;
    private float function;
    private float heuristic;
    private Node predecessor;
    private boolean discovered;
}
