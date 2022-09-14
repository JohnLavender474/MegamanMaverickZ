package com.game.entities.blocks;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.graph.GraphComponent;
import com.game.movement.TrajectoryComponent;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import java.util.List;

import static com.game.core.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.world.FixtureType.*;
import static java.lang.Float.parseFloat;

public class Block extends Entity {

    public static final Vector2 STANDARD_FRICTION = new Vector2(.035f, 0f);

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
        boolean resistance = false, gravityOn = false, isAbstract = false,
                wallSlideLeft = true, wallSlideRight = true, feetSticky = false;
        if (properties.containsKey("resistance")) {
            resistance = properties.get("resist", Boolean.class);
        }
        if (properties.containsKey("gravityOn")) {
            gravityOn = properties.get("gravityOn", Boolean.class);
        }
        if (properties.containsKey("isAbstract")) {
            isAbstract = properties.get("isAbstract", Boolean.class);
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
        set(bounds, properties, friction, resistance,
                gravityOn, isAbstract, wallSlideLeft, wallSlideRight, feetSticky);
    }

    private void set(Rectangle bounds, MapProperties properties, Vector2 friction, boolean resistance,
                     boolean gravityOn, boolean isAbstract, boolean wallSlideLeft,
                     boolean wallSlideRight, boolean feetSticky) {
        addComponent(defineGraphComponent());
        addComponent(defineBodyComponent(bounds, friction, resistance, gravityOn, isAbstract,
                wallSlideLeft, wallSlideRight, feetSticky));
        if (properties != null && properties.containsKey("trajectory")) {
            String[] trajectories = properties.get("trajectory", String.class).split(";");
            addComponent(defineTrajectoryComponent(trajectories));
        }
    }

    private TrajectoryComponent defineTrajectoryComponent(String[] trajectories) {
        TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
        for (String trajectory : trajectories) {
            String[] params = trajectory.split(",");
            float x = parseFloat(params[0]);
            float y = parseFloat(params[1]);
            float time = parseFloat(params[2]);
            trajectoryComponent.addTrajectory(new Vector2(x * PPM, y * PPM), time);
        }
        return trajectoryComponent;
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, Vector2 friction, boolean resistance,
                                              boolean gravityOn, boolean isAbstract, boolean wallSlideLeft,
                                              boolean wallSlideRight, boolean feetSticky) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setFriction(friction);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setAffectedByResistance(resistance);
        if (!isAbstract) {
            bodyComponent.addFixture(new Fixture(this, bodyComponent.getCollisionBox(), BLOCK));
        }
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

    private GraphComponent defineGraphComponent() {
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
