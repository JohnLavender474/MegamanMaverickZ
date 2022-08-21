package com.game.entities.sensors;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import static com.game.world.FixtureType.WALL_SLIDE_SENSOR;

public class WallSlideSensor extends Entity {

    public WallSlideSensor(Rectangle bounds) {
        addComponent(defineBodyComponent(bounds));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setAffectedByResistance(false);
        bodyComponent.setGravityOn(false);
        bodyComponent.set(bounds);
        Fixture wallSlideSensor = new Fixture(this, WALL_SLIDE_SENSOR);
        wallSlideSensor.setBounds(bounds);
        bodyComponent.addFixture(wallSlideSensor);
        return bodyComponent;
    }

}
