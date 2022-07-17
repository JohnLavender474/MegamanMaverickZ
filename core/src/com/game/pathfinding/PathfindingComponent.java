package com.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.Entity;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public record PathfindingComponent(Function<Float, Boolean> doUpdate, Supplier<Vector2> start,
                                   Supplier<Vector2> target, Set<Entity> entitiesToAvoid,
                                   float maxSearchDist, boolean allowDiagonal) implements Component {}
