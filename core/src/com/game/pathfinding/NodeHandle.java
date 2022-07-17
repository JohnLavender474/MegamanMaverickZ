package com.game.pathfinding;

import com.game.graph.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NodeHandle {
    private final Node node;
    private Node predecessor;
    private boolean discovered;
    private float heuristic;
    private float function;
    private float cost;
}
