package com.game.entities.blocks;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.debugging.DebugShapesComponent;
import com.game.debugging.DebugShapesHandle;
import com.game.entities.decorations.DecorativeSprite;
import com.game.graph.GraphComponent;
import com.game.movement.TrajectoryComponent;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.world.FixtureType.*;
import static java.lang.Float.parseFloat;

public class Block extends Entity {

    public Block(GameContext2d gameContext , RectangleMapObject blockObj, Vector2 friction, boolean resistance,
                 boolean gravityOn, boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        this(gameContext, blockObj.getRectangle(), friction, resistance, gravityOn,
                wallSlideLeft, wallSlideRight, feetSticky);
        setToBlockObj(gameContext, blockObj);
    }

    public Block(GameContext2d gameContext, Rectangle bounds, Vector2 friction) {
        this(gameContext, bounds, friction, false, false, false, false, false);
    }

    public Block(GameContext2d gameContext, Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn,
                 boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        super(gameContext);
        addComponent(defineGraphComponent());
        addComponent(defineBodyComponent(bounds, friction, resistance, gravityOn,
                wallSlideLeft, wallSlideRight, feetSticky));
        addComponent(defineDebugShapesComponent());
    }

    private void setToBlockObj(GameContext2d gameContext, RectangleMapObject blockObj) {
        MapProperties properties = blockObj.getProperties();
        // decorative sprites
        if (properties.containsKey("src") && properties.containsKey("region")) {
            String decorativeSrc = properties.get("src", String.class);
            String decorativeRegion = properties.get("region", String.class);
            TextureRegion textureRegion = gameContext.getAsset(decorativeSrc, TextureAtlas.class)
                    .findRegion(decorativeRegion);
            gameContext.addEntities(generateDecorativeBlocks(textureRegion));
        }
        // trajectory
        if (properties.containsKey("trajectory")) {
            TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
            String[] trajectories = blockObj.getProperties().get("trajectory", String.class).split(";");
            for (String trajectory : trajectories) {
                String[] params = trajectory.split(",");
                float x = parseFloat(params[0]);
                float y = parseFloat(params[1]);
                float time = parseFloat(params[2]);
                trajectoryComponent.addTrajectory(new Vector2(x * PPM, y * PPM), time);
            }
            addComponent(trajectoryComponent);
        }
    }

    public List<DecorativeSprite> generateDecorativeBlocks(TextureRegion textureRegion) {
        List<DecorativeSprite> decorativeSprites = new ArrayList<>();
        Vector2 size = getComponent(BodyComponent.class).getSize().scl(1f / PPM);
        for (int i = 0; i < (int) size.x; i++) {
            for (int j = 0; j < (int) size.y; j++) {
                final int finalI = i; final int finalJ = j;
                decorativeSprites.add(new DecorativeSprite(gameContext, textureRegion, new Vector2(PPM, PPM),
                        () -> getComponent(BodyComponent.class).getCenter().add(finalI * PPM, finalJ * PPM)));
            }
        }
        return decorativeSprites;
    }

    private DebugShapesComponent defineDebugShapesComponent() {
        DebugShapesComponent debugShapesComponent = new DebugShapesComponent();
        getComponent(BodyComponent.class).getFixtures().stream().filter(f -> f.isFixtureType(BLOCK)).forEach(f ->
                debugShapesComponent.addDebugShapeHandle(new DebugShapesHandle(f::getFixtureShape, Line, () -> BLUE)));
        return debugShapesComponent;
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn,
                                              boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setFriction(friction);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setAffectedByResistance(resistance);
        Fixture block = new Fixture(this, bodyComponent.getCollisionBox(), BLOCK);
        bodyComponent.addFixture(block);
        if (wallSlideLeft) {
            Fixture leftWallSlide = new Fixture(this, new Rectangle(0f, 0f, PPM / 3f,
                    bodyComponent.getCollisionBox().height - PPM / 3f), WALL_SLIDE_SENSOR);
            leftWallSlide.setOffset(-bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(leftWallSlide);
        }
        if (wallSlideRight) {
            Fixture rightWallSlide = new Fixture(this, new Rectangle(0f, 0f, PPM / 3f,
                    bodyComponent.getCollisionBox().height - PPM / 3f), WALL_SLIDE_SENSOR);
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
