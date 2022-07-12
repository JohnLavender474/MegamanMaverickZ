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

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestBlock implements IEntity {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    public TestBlock(Rectangle bounds, Vector2 friction) {
        this(bounds, friction, false, false, false, false, false);
    }

    public TestBlock(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn, boolean wallSlideLeft,
                     boolean wallSlideRight, boolean feetSticky) {
        addComponent(defineBodyComponent(bounds, friction, resistance, gravityOn, wallSlideLeft,
                wallSlideRight, feetSticky));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn,
                                              boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setFriction(friction);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setAffectedByResistance(resistance);
        Fixture block = new Fixture(this, BLOCK);
        block.set(bodyComponent.getCollisionBox());
        bodyComponent.addFixture(block);
        if (wallSlideLeft) {
            Fixture leftWallSlide = new Fixture(this, WALL_SLIDE_SENSOR);
            leftWallSlide.setSize(PPM / 3f, bodyComponent.getCollisionBox().height - PPM / 3f);
            leftWallSlide.setOffset(-bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(leftWallSlide);
        }
        if (wallSlideRight) {
            Fixture rightWallSlide = new Fixture(this, WALL_SLIDE_SENSOR);
            rightWallSlide.setSize(PPM / 3f, bodyComponent.getCollisionBox().height - PPM / 3f);
            rightWallSlide.setOffset(bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(rightWallSlide);
        }
        if (feetSticky) {
            Fixture feetSticker = new Fixture(this, FEET_STICKER);
            feetSticker.setSize(bodyComponent.getCollisionBox().width, PPM / 3f);
            feetSticker.setOffset(0f, (bodyComponent.getCollisionBox().height / 2f) - 2f);
            bodyComponent.addFixture(feetSticker);
        }
        return bodyComponent;
    }

}
