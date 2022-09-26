package com.game.entities.sensors;

import com.badlogic.gdx.math.Rectangle;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import static com.game.world.FixtureType.WALL_SLIDE_SENSOR;

public class WallSlideSensor extends Entity {

    public WallSlideSensor(GameContext2d gameContext, Rectangle bounds) {
        super(gameContext);
        addComponent(bodyComponent(bounds));
    }

    private BodyComponent bodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setAffectedByResistance(false);
        bodyComponent.setGravityOn(false);
        bodyComponent.set(bounds);
        Fixture wallSlideSensor = new Fixture(this, new Rectangle(bounds), WALL_SLIDE_SENSOR);
        bodyComponent.addFixture(wallSlideSensor);
        return bodyComponent;
    }

}
