package com.game.entities.hazards;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.core.IAssetLoader;
import com.game.debugging.DebugLinesComponent;
import com.game.debugging.DebugShapesComponent;
import com.game.debugging.DebugShapesHandle;
import com.game.movement.PendulumComponent;
import com.game.movement.RotatingLineComponent;
import com.game.movement.TrajectoryComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.UpdatableConsumer;
import com.game.utils.objects.Pendulum;
import com.game.utils.objects.RotatingLine;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.List;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.core.constants.TextureAsset.*;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static java.lang.Float.parseFloat;

public class Saw extends Entity {

    public Saw(GameContext2d gameContext, Rectangle rectangle) {
        this(gameContext, centerPoint(rectangle));
    }

    public Saw(GameContext2d gameContext, float centerX, float centerY) {
        this(gameContext, new Vector2(centerX, centerY));
    }

    public Saw(GameContext2d gameContext, Vector2 center) {
        super(gameContext);
        addComponent(defineAnimationComponent(gameContext));
        addComponent(defineBodyComponent(center));
        addComponent(defineSpriteComponent());
    }

    public Saw(GameContext2d gameContext, RectangleMapObject sawObj) {
        this(gameContext, sawObj.getRectangle());
        MapProperties properties = sawObj.getProperties();
        Rectangle rect = sawObj.getRectangle();
        if (properties.containsKey("p")) {
            setToPendulum(properties, rect);
        } else if (properties.containsKey("r")) {
            setToRotation(properties, rect);
        } else if (properties.containsKey("t")) {
            setToTrajectory(properties);
        }
    }

    private void setToPendulum(MapProperties properties, Rectangle rect) {
        int length = properties.get("length", Integer.class);
        float scalar = properties.get("scalar", Float.class);
        Pendulum pendulum = new Pendulum(length * PPM, 10f * PPM, bottomCenterPoint(rect), scalar);
        UpdatableConsumer<Pendulum> updatableConsumer = (pendulum1, delta) ->
                getComponent(BodyComponent.class).setCenter(pendulum1.getEnd());
        addComponent(new PendulumComponent(pendulum, updatableConsumer));
        addComponent(new DebugLinesComponent(pendulum.getAnchor(), pendulum.getEnd(),
                () -> DARK_GRAY, PPM / 8f, Filled));
        Circle circle1 = new Circle(pendulum.getAnchor(), PPM / 4f);
        Circle circle2 = new Circle();
        circle2.setRadius(PPM / 4f);
        addComponent(new DebugShapesComponent(new DebugShapesHandle(() -> circle1, Filled, () -> DARK_GRAY),
                new DebugShapesHandle(() -> circle2, Filled, () -> DARK_GRAY, (shape2D, delta) ->
                        ((Circle) shape2D).setPosition(pendulum.getEnd()))));
    }

    private void setToRotation(MapProperties properties, Rectangle rect) {
        float radius = properties.get("radius", Float.class);
        float speed = properties.get("speed", Float.class);
        RotatingLine rotatingLine = new RotatingLine(centerPoint(rect), radius * PPM, speed * PPM);
        UpdatableConsumer<RotatingLine> updatableConsumer = (rotatingLine1, delta) ->
                getComponent(BodyComponent.class).setCenter(rotatingLine1.getEndPoint());
        addComponent(new RotatingLineComponent(rotatingLine, updatableConsumer));
        addComponent(new DebugLinesComponent(() -> List.of(rotatingLine.getPos(), rotatingLine.getEndPoint()),
                () -> DARK_GRAY, PPM / 8f, Filled));
        Circle circle1 = new Circle();
        circle1.setRadius(PPM / 4f);
        Circle circle2 = new Circle();
        circle2.setRadius(PPM / 4f);
        addComponent(new DebugShapesComponent(
                new DebugShapesHandle(() -> circle1, Filled, () -> DARK_GRAY, ((shape2D, delta) ->
                        ((Circle) shape2D).setPosition(rotatingLine.getPos()))),
                new DebugShapesHandle(() -> circle2, Filled, () -> DARK_GRAY, (shape2D, delta) ->
                        ((Circle) shape2D).setPosition(rotatingLine.getEndPoint()))));
    }

    private void setToTrajectory(MapProperties properties) {
        TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
        String[] trajectories = properties.get("trajectory", String.class).split(";");
        for (String trajectory : trajectories) {
            String[] params = trajectory.split(",");
            float x = parseFloat(params[0]);
            float y = parseFloat(params[1]);
            float time = parseFloat(params[2]);
            trajectoryComponent.addTrajectory(new Vector2(x * PPM, y * PPM), time);
        }
        addComponent(trajectoryComponent);
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(2f * PPM, 2f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(CENTER);
                return true;
            }

        });
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        TextureRegion textureRegion = assetLoader.getAsset(HAZARDS_1.getSrc(), TextureAtlas.class)
                .findRegion("Saw");
        TimedAnimation timedAnimation = new TimedAnimation(textureRegion, 2, .1f);
        return new AnimationComponent(timedAnimation);
    }

    private BodyComponent defineBodyComponent(Vector2 center) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(2f * PPM, 2f * PPM);
        bodyComponent.setCenter(center);
        // Death 1
        Rectangle death1 = new Rectangle();
        death1.setSize(2f * PPM, PPM);
        death1.setCenter(center);
        bodyComponent.addFixture(new Fixture(this, death1, DEATH));
        // Death 2
        Rectangle death2 = new Rectangle();
        death2.setSize(PPM, 2f * PPM);
        death2.setCenter(center);
        bodyComponent.addFixture(new Fixture(this, death2, DEATH));
        // Shield 1
        Rectangle shield1 = new Rectangle();
        shield1.setSize(PPM, PPM);
        setBottomCenterToPoint(shield1, bodyComponent.getCenter());
        Fixture shield1Fixture = new Fixture(this, shield1, SHIELD);
        shield1Fixture.putUserData("reflectDir", "up");
        bodyComponent.addFixture(shield1Fixture);
        // Shield 2
        Rectangle shield2 = new Rectangle();
        shield2.setSize(PPM, PPM);
        setTopCenterToPoint(shield2, bodyComponent.getCenter());
        Fixture shield2Fixture = new Fixture(this, shield2, SHIELD);
        shield2Fixture.putUserData("reflectDir", "down");
        bodyComponent.addFixture(shield2Fixture);
        return bodyComponent;
    }

}
