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
import com.game.movement.Pendulum;
import com.game.movement.RotatingLine;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.core.ConstVals.TextureAsset.*;
import static com.game.core.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static java.lang.Float.parseFloat;

public class Saw extends Entity {

    public Saw(GameContext2d gameContext, RectangleMapObject sawObj) {
        this(gameContext, sawObj.getRectangle());
        MapProperties properties = sawObj.getProperties();
        if (properties.containsKey("p")) {
            int length = properties.get("length", Integer.class);
            float scalar = properties.get("scalar", Float.class);
            Pendulum pendulum = new Pendulum(length * PPM, 10f * PPM, bottomCenterPoint(sawObj.getRectangle()), scalar);
            UpdatableConsumer<Pendulum> updatableConsumer = (pendulum1, delta) ->
                    getComponent(BodyComponent.class).setCenter(pendulum1.getEnd());
            addComponent(new PendulumComponent(pendulum, updatableConsumer));
            addComponent(new DebugLinesComponent(pendulum.getAnchor(), pendulum.getEnd(),
                    () -> DARK_GRAY, PPM / 8f, Filled));
            Circle circle1 = new Circle(pendulum.getAnchor(), PPM / 4f);
            Circle circle2 = new Circle();
            circle2.setRadius(PPM / 4f);
            addComponent(new DebugShapesComponent(new DebugShapesHandle(circle1, Filled, () -> DARK_GRAY),
                    new DebugShapesHandle(circle2, Filled, () -> DARK_GRAY, (shape2D, delta) ->
                        ((Circle) shape2D).setPosition(pendulum.getEnd()))));
        } else if (properties.containsKey("r")) {
            float radius = properties.get("radius", Integer.class);
            float speed = properties.get("speed", Float.class);
            RotatingLine rotatingLine = new RotatingLine(centerPoint(sawObj.getRectangle()), radius, speed);
            UpdatableConsumer<RotatingLine> updatableConsumer = (rotatingLine1, delta) ->
                    getComponent(BodyComponent.class).setCenter(rotatingLine1.getEndPoint());
            addComponent(new RotatingLineComponent(rotatingLine, updatableConsumer));
        } else if (properties.containsKey("t")) {
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
    }

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
        TextureRegion textureRegion = assetLoader.getAsset(HAZARDS_1_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                .findRegion("Saw");
        TimedAnimation timedAnimation = new TimedAnimation(textureRegion, 2, .1f);
        return new AnimationComponent(timedAnimation);
    }

    private BodyComponent defineBodyComponent(Vector2 center) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(2f * PPM, 2f * PPM);
        bodyComponent.setCenter(center);
        // Death 1
        Fixture death1 = new Fixture(this, DEATH);
        death1.setSize(2f * PPM, PPM);
        death1.setCenter(center);
        bodyComponent.addFixture(death1);
        // Death 2
        Fixture death2 = new Fixture(this, DEATH);
        death2.setSize(PPM, 2f * PPM);
        death2.setCenter(center);
        bodyComponent.addFixture(death2);
        // Shield 1
        Fixture shield1 = new Fixture(this, SHIELD);
        shield1.setSize(PPM, PPM);
        setBottomCenterToPoint(shield1.getFixtureBox(), bodyComponent.getCenter());
        shield1.putUserData("reflectDir", "up");
        bodyComponent.addFixture(shield1);
        // Shield 2
        Fixture shield2 = new Fixture(this, SHIELD);
        shield2.setSize(PPM, PPM);
        setTopCenterToPoint(shield2.getFixtureBox(), bodyComponent.getCenter());
        shield2.putUserData("reflectDir", "down");
        bodyComponent.addFixture(shield2);
        return bodyComponent;
    }

}
