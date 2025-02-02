package com.game.entities.sensors;

import com.badlogic.gdx.math.Rectangle;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import static com.game.world.FixtureType.DEATH;

public class DeathSensor extends Entity {

    public DeathSensor(GameContext2d gameContext, Rectangle bounds) {
        super(gameContext);
        addComponent(bodyComponent(bounds));
    }

    private BodyComponent bodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setGravityOn(false);
        bodyComponent.setAffectedByResistance(false);
        bodyComponent.set(bounds);
        Fixture death = new Fixture(this, new Rectangle(bounds), DEATH);
        bodyComponent.addFixture(death);
        return bodyComponent;
    }

}
