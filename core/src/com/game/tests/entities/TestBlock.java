package com.game.tests.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.core.IEntity;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.world.FixtureType.BLOCK;

@Getter
@Setter
public class TestBlock implements IEntity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    public TestBlock(Rectangle bounds, boolean affectedByResistance, boolean gravityOn, Vector2 friction) {
        addComponent(defineBodyComponent(bounds, affectedByResistance, gravityOn, friction));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, boolean affectedByResistance, boolean gravityOn,
                                              Vector2 friction) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setFriction(friction);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setAffectedByResistance(affectedByResistance);
        Fixture block = new Fixture(this, BLOCK);
        block.set(bodyComponent.getCollisionBox());
        bodyComponent.addFixture(block);
        return bodyComponent;
    }

}
