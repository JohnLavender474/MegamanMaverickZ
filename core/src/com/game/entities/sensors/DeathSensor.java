package com.game.entities.sensors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import static com.game.world.FixtureType.DEATH;

public class DeathSensor extends Entity {

    public DeathSensor(GameContext2d gameContext, Rectangle bounds) {
        super(gameContext);
        addComponent(defineBodyComponent(bounds));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setGravityOn(false);
        bodyComponent.setAffectedByResistance(false);
        bodyComponent.set(bounds);
        Fixture death = new Fixture(this, DEATH);
        death.setBounds(bounds);
        bodyComponent.addFixture(death);
        return bodyComponent;
    }

}
