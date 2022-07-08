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

import static com.game.world.FixtureType.DEATH;

@Getter
@Setter
public class TestDeathSensor implements IEntity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    public TestDeathSensor(Rectangle bounds) {
        addComponent(defineBodyComponent(bounds));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.setGravityOn(false);
        bodyComponent.setAffectedByResistance(false);
        bodyComponent.set(bounds);
        Fixture death = new Fixture(this, DEATH);
        death.set(bounds);
        bodyComponent.addFixture(death);
        return bodyComponent;
    }

}
