package com.game.movement;

import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.System;
import com.game.world.BodyComponent;

public class TrajectorySystem extends System {

    public TrajectorySystem() {
        super(TrajectoryComponent.class, BodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        TrajectoryComponent trajectoryComponent = entity.getComponent(TrajectoryComponent.class);
        Vector2 pos = trajectoryComponent.getPos(delta);
        BodyComponent bodyComponent = entity.getComponent(BodyComponent.class);
        bodyComponent.setCenter(pos);
    }

}
