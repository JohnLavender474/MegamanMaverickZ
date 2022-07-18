package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.game.Component;

import java.util.function.Supplier;

public record GraphComponent(Supplier<Rectangle> boundsSupplier) implements Component {}
