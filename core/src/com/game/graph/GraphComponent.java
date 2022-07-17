package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public record GraphComponent(Supplier<Rectangle> boundsSupplier) implements Component {}
