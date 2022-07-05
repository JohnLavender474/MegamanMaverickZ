package com.game.entities.blocks;

import com.badlogic.gdx.math.Rectangle;
import com.game.Entity;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import static com.game.world.FixtureType.BLOCK;

public class Block extends Entity {

    public Block(Rectangle bounds, boolean wallSlideLeft, boolean wallSlideRight, boolean affectedByResistance,
                 boolean gravityOn, float frictionX, float frictionY) {
        addComponent(defineBodyComponent(bounds, wallSlideLeft, wallSlideRight,
                affectedByResistance, gravityOn, frictionX, frictionY));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, boolean wallSlideLeft, boolean wallSlideRight,
                                              boolean affectedByResistance, boolean gravityOn,
                                              float frictionX, float frictionY) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setFriction(frictionX, frictionY);
        bodyComponent.setAffectedByResistance(affectedByResistance);
        Fixture block = new Fixture(this, BLOCK);
        block.set(bodyComponent.getCollisionBox());
        bodyComponent.addFixture(block);
        return bodyComponent;
    }

}
