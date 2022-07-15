package com.game.hits;

import com.game.Component;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class HitComponent implements Component {

    private final Consumer<Fixture> hitConsumer;
    private final Queue<Fixture> hitFixtures = new ArrayDeque<>();

}
