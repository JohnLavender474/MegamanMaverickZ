package com.game.entities.hazards;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.damage.Damager;
import com.game.shapes.LineComponent;
import com.game.shapes.LineHandle;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.RotatingLine;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.GlobalKeys.COLLECTION;
import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.PPM;
import static com.game.utils.ShapeUtils.*;
import static com.game.utils.UtilMethods.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static java.util.Arrays.*;

public class LaserBeamer extends Entity implements Damager {

    public static final Map<Integer, Float> blockHitRadii = Map.of(0, 2f, 1, 5f, 2, 8f);

    public static final float SPEED = 2f;
    public static final float RADIUS = 10f;
    public static final float BLOCK_HIT = .05f;
    public static final float SWITCH_TIME = 1f;
    public static final float MIN_DEGREES = 200f;
    public static final float MAX_DEGREES = 340f;
    public static final float INIT_DEGREES = 270f;
    public static final float THICKNESS = PPM / 32f;

    private final Queue<Vector2> blockContactPoints;
    private final Polyline laser = new Polyline();
    private final RotatingLine rotatingLine;
    private final Timer blockHitTimer;
    private final Timer switchTimer;

    private Circle blockHit;
    private boolean clockwise;
    private int blockHitIndex;

    public LaserBeamer(GameContext2d gameContext, RectangleMapObject spawnObj) {
        super(gameContext);
        Vector2 spawn = bottomCenterPoint(spawnObj.getRectangle());
        switchTimer = new Timer(SWITCH_TIME, true);
        blockHitTimer = new Timer(BLOCK_HIT);
        rotatingLine = new RotatingLine(spawn, RADIUS * PPM, SPEED * PPM, INIT_DEGREES);
        float[] v = rotatingLine.getPolyline().getVertices();
        laser.setVertices(copyOf(v, v.length));
        laser.setOrigin(spawn.x, spawn.y);
        blockContactPoints = new PriorityQueue<>((p1, p2) -> Float.compare(p1.dst2(spawn), p2.dst2(spawn)));
        addComponent(bodyComponent(spawn));
        addComponent(updatableComponent());
        addComponent(linesComponent());
        addComponent(spriteComponent());
        addComponent(defineDebugShapesComponent());
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setPosition(spawn);
        // rotating line laser
        Fixture laserFixture = new Fixture(this, rotatingLine.getPolyline(), LASER);
        laserFixture.setOffset(0f, PPM / 16f);
        laserFixture.putUserData(COLLECTION, blockContactPoints);
        bodyComponent.addFixture(laserFixture);
        // damager laser
        bodyComponent.addFixture(new Fixture(this, laser, DAMAGER));
        // shield
        Rectangle shield = new Rectangle();
        shield.setSize(PPM, PPM * .85f);
        Fixture shieldFixture = new Fixture(this, shield, SHIELD);
        shieldFixture.setOffset(0f, PPM / 2f);
        shieldFixture.putUserData("reflectDir", "up");
        bodyComponent.addFixture(shieldFixture);
        return bodyComponent;
    }

    public SpriteComponent spriteComponent() {
        TextureRegion laserBeamer = gameContext.getAsset(HAZARDS_1.getSrc(), TextureAtlas.class)
                .findRegion("LaserBeamer");
        Sprite sprite = new Sprite(laserBeamer);
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

        });
    }

    private ShapeComponent defineDebugShapesComponent() {
        ShapeHandle shapeHandle1 = new ShapeHandle();
        shapeHandle1.setShapeSupplier(() -> blockHit);
        shapeHandle1.setPrioritySupplier(() -> 1);
        shapeHandle1.setColorSupplier(() -> WHITE);
        shapeHandle1.setShapeTypeSupplier(() -> Filled);
        Fixture shield = getComponent(BodyComponent.class).getFirstMatchingFixture(SHIELD).orElseThrow();
        ShapeHandle shapeHandle2 = new ShapeHandle();
        shapeHandle2.setShapeSupplier(shield::getFixtureShape);
        shapeHandle2.setColorSupplier(() -> GREEN);
        return new ShapeComponent(shapeHandle1, shapeHandle2);
    }

    private LineComponent linesComponent() {
        LineHandle lineHandle = new LineHandle();
        lineHandle.setLineSupplier(() -> polylineToPointPair(laser));
        lineHandle.setThicknessSupplier(() -> THICKNESS);
        lineHandle.setShapeTypeSupplier(() -> Filled);
        lineHandle.setColorSupplier(() -> RED);
        return new LineComponent(lineHandle);
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(delta -> {
            // block hit flash
            blockHitTimer.update(delta);
            if (blockHitTimer.isFinished()) {
                blockHitIndex++;
            }
            if (blockHitIndex > 2) {
                blockHitIndex = 0;
            }
            // block contact
            Vector2 origin = rotatingLine.getPos();
            Vector2 endPos = rotatingLine.getEndPoint();
            Vector2 closestBlockHit = blockContactPoints.peek();
            if (closestBlockHit != null) {
                endPos.set(closestBlockHit);
                blockHit = new Circle();
                blockHit.setPosition(closestBlockHit.x, closestBlockHit.y);
                blockHit.setRadius(blockHitRadii.get(blockHitIndex));
            } else {
                blockHit = null;
            }
            laser.setVertices(new float[]{origin.x, origin.y, endPos.x, endPos.y});
            blockContactPoints.clear();
            // rotation
            switchTimer.update(delta);
            if (!switchTimer.isFinished()) {
                return;
            }
            if (switchTimer.isJustFinished()) {
                clockwise = !clockwise;
                rotatingLine.setSpeed(clockwise ? -SPEED * PPM : SPEED * PPM);
            }
            rotatingLine.update(delta);
            if (clockwise && rotatingLine.getDegrees() <= MIN_DEGREES) {
                rotatingLine.setDegrees(MIN_DEGREES);
                switchTimer.reset();
            } else if (!clockwise && rotatingLine.getDegrees() >= MAX_DEGREES) {
                rotatingLine.setDegrees(MAX_DEGREES);
                switchTimer.reset();
            }
        });
    }

}
