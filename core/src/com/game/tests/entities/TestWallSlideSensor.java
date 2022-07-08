package com.game.tests.entities;

import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.core.IEntity;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.world.FixtureType.WALL_SLIDE_SENSOR;

@Getter
@Setter
public class TestWallSlideSensor implements IEntity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    public TestWallSlideSensor(Rectangle bounds) {
        addComponent(defineBodyComponent(bounds));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setAffectedByResistance(false);
        bodyComponent.setGravityOn(false);
        bodyComponent.set(bounds);
        Fixture wallSlideSensor = new Fixture(this, WALL_SLIDE_SENSOR);
        wallSlideSensor.set(bounds);
        bodyComponent.addFixture(wallSlideSensor);
        return bodyComponent;
    }

}