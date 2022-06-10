package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.function.Supplier;

public record DebugHandle(Supplier<Rectangle> rectangleSupplier, Color color) {}
