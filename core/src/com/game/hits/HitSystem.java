package com.game.hits;

import com.game.System;
import com.game.core.IEntity;
import com.game.world.Fixture;

import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class HitSystem extends System {

    public HitSystem() {
        super(Set.of(HitComponent.class));
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        HitComponent hitComponent = entity.getComponent(HitComponent.class);
        Consumer<Fixture> hitConsumer = hitComponent.getHitConsumer();
        Queue<Fixture> hitFixtures = hitComponent.getHitFixtures();
        while (!hitFixtures.isEmpty()) {
            Fixture hitFixture = hitFixtures.poll();
            hitConsumer.accept(hitFixture);
        }
    }

}
