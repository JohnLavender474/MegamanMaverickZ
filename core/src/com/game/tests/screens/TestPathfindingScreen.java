package com.game.tests.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Entity;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugSystem;
import com.game.graph.GraphComponent;
import com.game.pathfinding.PathfindingComponent;
import com.game.tests.core.TestEntitiesAndSystemsManager;
import com.game.trajectories.TrajectorySystem;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.WorldSystem;

import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.*;
import static com.game.world.BodyType.*;

public class TestPathfindingScreen extends ScreenAdapter {

    private static final String TARGET = "TARGET";

    private Viewport viewport;
    private IEntitiesAndSystemsManager entitiesAndSystemsManager;

    @Override
    public void show() {
        viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        entitiesAndSystemsManager = new TestEntitiesAndSystemsManager();
        entitiesAndSystemsManager.addSystem(new WorldSystem(null, Vector2.Zero, 1f / 120f));
        entitiesAndSystemsManager.addSystem(new TrajectorySystem());
        entitiesAndSystemsManager.addSystem(new DebugSystem(new ShapeRenderer(),
                (OrthographicCamera) viewport.getCamera()));

    }

    static class TestPathfindingEntity extends Entity {

        TestPathfindingEntity(Rectangle bounds, Supplier<Vector2> targetSupplier) {
            addComponent(defineBodyComponent(bounds));
            addComponent(defineGraphComponent());
            addComponent(defineDebugComponent());
        }

        private BodyComponent defineBodyComponent(Rectangle bounds) {
            BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
            bodyComponent.set(bounds);
            return bodyComponent;
        }

        private GraphComponent defineGraphComponent() {
            return new GraphComponent(() -> getComponent(BodyComponent.class).getCollisionBox());
        }

        private PathfindingComponent definePathfindingComponent(Supplier<Vector2> targetSupplier) {
            Timer timer = new Timer(5f);
            return new PathfindingComponent(vector2s -> {

            }, delta -> {
                if (timer.isFinished()) {
                    timer.reset();
                }
                timer.update(delta);
                return timer.isFinished();
            }, obj -> true, () -> 50f * PPM, () -> true,
                    () -> getComponent(BodyComponent.class).getCenter(), targetSupplier);
        }

        private DebugComponent defineDebugComponent() {
            return new DebugComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> Color.RED);
        }

    }

}
