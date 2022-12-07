package com.game.entities.blocks;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.graph.GraphComponent;
import com.game.movement.TrajectoryComponent;
import com.game.movement.TrajectoryParser;
import com.game.utils.objects.KeyValuePair;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.Collection;
import java.util.List;

import static com.game.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class Block extends Entity {

    public static final Vector2 STANDARD_FRICTION = new Vector2(.035f, 0f);

    public Block(GameContext2d gameContext, Rectangle bounds, boolean wallslide) {
        super(gameContext);
        set(bounds, STANDARD_FRICTION.cpy(), false, false, wallslide, wallslide, false);
    }

    public Block(GameContext2d gameContext, RectangleMapObject rectObj) {
        this(gameContext, rectObj.getRectangle(), rectObj.getProperties());
    }

    public Block(GameContext2d gameContext, Rectangle bounds, MapProperties properties) {
        super(gameContext);
        Vector2 friction = STANDARD_FRICTION.cpy();
        if (properties.containsKey("frictionX")) {
            friction.x = properties.get("frictionX", Float.class);
        }
        if (properties.containsKey("frictionY")) {
            friction.y = properties.get("frictionY", Float.class);
        }
        boolean resistance = false, gravityOn = false, wallSlideLeft = true, wallSlideRight = true, feetSticky = false;
        if (properties.containsKey("resistance")) {
            resistance = properties.get("resist", Boolean.class);
        }
        if (properties.containsKey("gravityOn")) {
            gravityOn = properties.get("gravityOn", Boolean.class);
        }
        if (properties.containsKey("wallSlide")) {
            wallSlideLeft = wallSlideRight = properties.get("wallSlide", Boolean.class);
        }
        if (properties.containsKey("wallSlideLeft")) {
            wallSlideLeft = properties.get("wallSlideLeft", Boolean.class);
        }
        if (properties.containsKey("wallSlideRight")) {
            wallSlideRight = properties.get("wallSlideRight", Boolean.class);
        }
        if (properties.containsKey("feetSticky")) {
            feetSticky = properties.get("feetSticky", Boolean.class);
        }
        set(bounds, properties, friction, resistance, gravityOn, wallSlideLeft, wallSlideRight, feetSticky);
    }

    private void set(Rectangle bounds, MapProperties properties, Vector2 friction, boolean resistance,
                     boolean gravityOn, boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        set(bounds, friction, resistance, gravityOn, wallSlideLeft, wallSlideRight, feetSticky);
        if (properties != null && properties.containsKey("trajectory")) {
            String trajStr = properties.get("trajectory", String.class);
            addComponent(trajectoryComponent(trajStr));
        }
    }

    private void set(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn, boolean wallSlideLeft,
                     boolean wallSlideRight, boolean feetSticky) {
        addComponent(graphComponent());
        addComponent(bodyComponent(bounds, friction, resistance, gravityOn,
                wallSlideLeft, wallSlideRight, feetSticky));
    }

    protected TrajectoryComponent trajectoryComponent(String trajStr) {
        TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
        Collection<KeyValuePair<Vector2, Float>> trajectories = TrajectoryParser.parse(trajStr);
        trajectories.forEach(trajectoryDef ->
            trajectoryComponent.addTrajectory(trajectoryDef.key().scl(PPM), trajectoryDef.value()));
        return trajectoryComponent;
    }

    protected BodyComponent bodyComponent(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn,
                                              boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        BodyComponent bodyComponent = new BodyComponent(STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setFriction(friction);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setAffectedByResistance(resistance);
        bodyComponent.addFixture(new Fixture(this, bodyComponent.getCollisionBox(), BLOCK));
        if (wallSlideLeft) {
            Fixture leftWallSlide = new Fixture(this, new Rectangle(0f, 0f, PPM / 3f,
                    bodyComponent.getCollisionBox().height), WALL_SLIDE_SENSOR);
            leftWallSlide.setOffset(-bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(leftWallSlide);
        }
        if (wallSlideRight) {
            Fixture rightWallSlide = new Fixture(this, new Rectangle(0f, 0f, PPM / 3f,
                    bodyComponent.getCollisionBox().height), WALL_SLIDE_SENSOR);
            rightWallSlide.setOffset(bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(rightWallSlide);
        }
        if (feetSticky) {
            Fixture feetSticker = new Fixture(this, new Rectangle(0f, 0f, bodyComponent.getCollisionBox().width,
                    PPM / 3f), FEET_STICKER);
            feetSticker.setOffset(0f, (bodyComponent.getCollisionBox().height / 2f) - 2f);
            bodyComponent.addFixture(feetSticker);
        }
        return bodyComponent;
    }

    protected GraphComponent graphComponent() {
        GraphComponent graphComponent = new GraphComponent();
        graphComponent.addSupplier(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> List.of(this));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle groundBounds = new Rectangle();
            groundBounds.setSize(bodyBounds.width + 5f, 1f);
            Vector2 center = centerPoint(bodyBounds).add(0f, (bodyBounds.height / 2f) + (PPM / 2f));
            groundBounds.setCenter(center);
            return groundBounds;
        }, () -> List.of("Ground"));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle ceilingBounds = new Rectangle();
            ceilingBounds.setSize(bodyBounds.width + 5f, 1f);
            Vector2 center = centerPoint(bodyBounds).sub(0f, (bodyBounds.height / 2f) + (PPM / 2f));
            ceilingBounds.setCenter(center);
            return ceilingBounds;
        }, () -> List.of("Ceiling"));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle leftBounds = new Rectangle();
            leftBounds.setSize(1f, bodyBounds.height + 5f);
            Vector2 center = centerPoint(bodyBounds).sub((bodyBounds.width / 2f) + (PPM / 2f), 0f);
            leftBounds.setCenter(center);
            return leftBounds;
        }, () -> List.of("LeftWall"));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle rightBounds = new Rectangle();
            rightBounds.setSize(1f, bodyBounds.height + 5f);
            Vector2 center = centerPoint(bodyBounds).add((bodyBounds.width / 2f) + (PPM / 2f), 0f);
            rightBounds.setCenter(center);
            return rightBounds;
        }, () -> List.of("RightWall"));
        return graphComponent;
    }

}
