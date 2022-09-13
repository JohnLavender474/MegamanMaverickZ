package com.game.entities.hazards;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.damage.Damager;
import com.game.debugging.DebugLinesComponent;
import com.game.debugging.DebugShapesComponent;
import com.game.debugging.DebugShapesHandle;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Pair;
import com.game.utils.objects.RotatingLine;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.core.constants.MiscellaneousVals.BLOCK_CONTACT_POINTS;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.utils.ShapeUtils.*;
import static com.game.utils.UtilMethods.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class LaserBeamer extends Entity implements Damager {

    public static final float SPEED = 2f;
    public static final float RADIUS = 10f;
    public static final float SWITCH_TIME = 1f;
    public static final float MIN_DEGREES = 200f;
    public static final float MAX_DEGREES = 340f;
    public static final float INIT_DEGREES = 270f;
    public static final float THICKNESS = PPM / 16f;

    private final Queue<Vector2> blockContactPoints;
    private final Polyline laser = new Polyline();
    private final RotatingLine rotatingLine;
    private final Timer switchTimer;

    private boolean clockwise;
    private Circle blockHit;

    public LaserBeamer(GameContext2d gameContext, Vector2 spawn) {
        super(gameContext);
        switchTimer = new Timer(SWITCH_TIME);
        switchTimer.setToEnd();
        rotatingLine = new RotatingLine(spawn, RADIUS * PPM, SPEED * PPM, INIT_DEGREES);
        float[] v = rotatingLine.getPolyline().getVertices();
        laser.setVertices(Arrays.copyOf(v, v.length));
        laser.setOrigin(spawn.x, spawn.y);
        blockContactPoints = new PriorityQueue<>((p1, p2) -> Float.compare(p1.dst2(spawn), p2.dst2(spawn)));
        addComponent(defineBodyComponent(spawn));
        addComponent(defineUpdatableComponent());
        addComponent(defineDebugLinesComponent());
        addComponent(defineDebugShapesComponent());
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(PPM, PPM);
        setTopCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        Fixture laserFixture = new Fixture(this, rotatingLine.getPolyline(), LASER);
        laserFixture.setOffset(0f, PPM / 2f);
        laserFixture.putUserData(BLOCK_CONTACT_POINTS, blockContactPoints);
        bodyComponent.addFixture(laserFixture);
        bodyComponent.addFixture(new Fixture(this, laser, DAMAGER));
        return bodyComponent;
    }

    private DebugShapesComponent defineDebugShapesComponent() {
        return new DebugShapesComponent(new DebugShapesHandle(() -> blockHit, Filled, () -> WHITE));
    }

    private DebugLinesComponent defineDebugLinesComponent() {
        DebugLinesComponent debugLinesComponent = new DebugLinesComponent();
        debugLinesComponent.setThickness(THICKNESS);
        debugLinesComponent.setShapeType(Line);
        debugLinesComponent.addDebugLine(() -> {
            Pair<Vector2> p = polylineToPointPair(laser);
            return List.of(p.getFirst(), p.getSecond());
        }, () -> BLUE);
        return debugLinesComponent;
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            // block contact
            Vector2 origin = rotatingLine.getPos();
            Vector2 endPos = rotatingLine.getEndPoint();
            Vector2 closestBlockHit = blockContactPoints.peek();
            if (closestBlockHit != null) {
                endPos.set(closestBlockHit);
                blockHit = new Circle();
                blockHit.setPosition(closestBlockHit.x, closestBlockHit.y);
                blockHit.setRadius(5f);
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
