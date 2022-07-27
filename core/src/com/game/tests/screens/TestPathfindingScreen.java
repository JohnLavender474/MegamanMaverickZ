package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Entity;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.debugging.DebugRectComponent;
import com.game.debugging.DebugRectSystem;
import com.game.graph.Graph;
import com.game.graph.GraphComponent;
import com.game.graph.GraphSystem;
import com.game.levels.CameraFocusable;
import com.game.levels.LevelTiledMap;
import com.game.pathfinding.PathfindingComponent;
import com.game.pathfinding.PathfindingSystem;
import com.game.tests.core.TestEntitiesAndSystemsManager;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableComponent;
import com.game.updatables.UpdatableSystem;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.WorldSystem;
import lombok.Getter;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.world.BodyType.*;

public class TestPathfindingScreen extends ScreenAdapter {

    private static final String OBSTACLES = "Obstacles";
    private static final String TARGET_SPAWN = "TargetSpawn";
    private static final String PATHFINDING_SPAWN = "PathfindingSpawn";

    private final List<Runnable> runOnShutdown = new ArrayList<>();

    private Graph levelGraph;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private LevelTiledMap levelTiledMap;
    private IEntitiesAndSystemsManager entitiesAndSystemsManager;

    private TestEntity testEntity;
    private TestPathfindingEntity testPathfindingEntity;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        entitiesAndSystemsManager = new TestEntitiesAndSystemsManager();
        levelGraph = new Graph(new Vector2(PPM, PPM), 50, 50);
        GraphSystem graphSystem = new GraphSystem();
        graphSystem.setGraph(levelGraph);
        entitiesAndSystemsManager.addSystem(graphSystem);
        PathfindingSystem pathfindingSystem = new PathfindingSystem(runOnShutdown);
        pathfindingSystem.setGraph(levelGraph);
        entitiesAndSystemsManager.addSystem(pathfindingSystem);
        entitiesAndSystemsManager.addSystem(new WorldSystem(null, Vector2.Zero, 1f / 120f));
        entitiesAndSystemsManager.addSystem(new TrajectorySystem());
        entitiesAndSystemsManager.addSystem(new UpdatableSystem());
        entitiesAndSystemsManager.addSystem(new DebugRectSystem(viewport.getCamera(), shapeRenderer));
        levelTiledMap = new LevelTiledMap("tiledmaps/tmx/testpathfinding.tmx");
        levelTiledMap.getObjectsOfLayer(OBSTACLES).forEach(obstObj ->
            entitiesAndSystemsManager.addEntity(new TestObstacleEntity(obstObj.getRectangle())));
        levelTiledMap.getObjectsOfLayer(TARGET_SPAWN).stream().findFirst().ifPresentOrElse(targetSpawnObj -> {
            testEntity = new TestEntity(targetSpawnObj.getRectangle(), DYNAMIC, Color.GREEN);
            entitiesAndSystemsManager.addEntity(testEntity);
        }, () -> { throw new IllegalStateException(); });
        levelTiledMap.getObjectsOfLayer(PATHFINDING_SPAWN).stream().findFirst().ifPresentOrElse(pathfindObj -> {
            testPathfindingEntity = new TestPathfindingEntity(pathfindObj.getRectangle(), () -> testEntity.getFocus());
            entitiesAndSystemsManager.addEntity(testPathfindingEntity);
        }, () -> { throw new IllegalStateException(); });
    }

    @Override
    public void render(float delta) {
        BodyComponent bodyComponent = testEntity.getComponent(BodyComponent.class);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            bodyComponent.setVelocity(0f, 5f * PPM);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            bodyComponent.setVelocity(0f, -5f * PPM);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            bodyComponent.setVelocity(5f * PPM, 0f);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            bodyComponent.setVelocity(-5f * PPM, 0f);
        } else {
            bodyComponent.setVelocity(Vector2.Zero);
        }
        List<Rectangle> path = testPathfindingEntity.getComponent(PathfindingComponent.class).getPathCpy();
        levelGraph.draw(shapeRenderer, node -> {
            if (node.getObjects().stream().anyMatch(o -> o instanceof TestObstacleEntity)) {
                return Color.ORANGE;
            } else if (node.getObjects().stream().anyMatch(o -> o.equals(testEntity))) {
                return Color.GOLD;
            } else if (node.getObjects().stream().anyMatch(o -> o.equals(testPathfindingEntity))) {
                return Color.YELLOW;
            } else if (path.stream().anyMatch(point -> node.getBounds().contains(point))) {
                return Color.NAVY;
            }
            return null;
        });
        entitiesAndSystemsManager.updateSystems(delta);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        boolean isDrawing = shapeRenderer.isDrawing();
        if (!isDrawing) {
            shapeRenderer.begin(Line);
        }
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < path.size() - 1; i++) {
            shapeRenderer.line(centerPoint(path.get(i)), centerPoint(path.get(i + 1)));
        }
        if (!isDrawing) {
            shapeRenderer.end();
        }
        viewport.getCamera().position.set(testEntity.getFocus().x, testEntity.getFocus().y, 0f);
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        runOnShutdown.forEach(Runnable::run);
    }

    private static class TestObstacleEntity extends TestEntity {

        private TestObstacleEntity(Rectangle bounds) {
            super(bounds, STATIC, Color.GRAY);
        }

    }

    @Getter
    private static class TestPathfindingEntity extends TestEntity implements CameraFocusable {

        private static final float SPEED = 3f;

        private final Vector2 trajectory = new Vector2();

        private TestPathfindingEntity(Rectangle bounds, Supplier<Vector2> targetSupplier) {
            super(bounds, DYNAMIC, Color.RED);
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            bodyComponent.setPostProcess(delta -> bodyComponent.setVelocity(Vector2.Zero));
            addComponent(definePathfindingComponent(targetSupplier));
            addComponent(defineUpdatableComponent());
        }

        private UpdatableComponent defineUpdatableComponent() {
            return new UpdatableComponent(delta -> {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                bodyComponent.getCollisionBox().x += delta * trajectory.x;
                bodyComponent.getCollisionBox().y += delta * trajectory.y;
            });
        }

        private PathfindingComponent definePathfindingComponent(Supplier<Vector2> targetSupplier) {
            Timer updateTimer = new Timer(1f);
            PathfindingComponent pathfindingComponent = new PathfindingComponent(
                    this::getFocus, targetSupplier,
                    target -> {
                        Vector2 targetCenter = centerPoint(target);
                        float angle = MathUtils.atan2(targetCenter.y - getFocus().y, targetCenter.x - getFocus().x);
                        trajectory.set(MathUtils.cos(angle), MathUtils.sin(angle)).scl(SPEED * PPM);
                    },
                    target -> getComponent(BodyComponent.class).getCollisionBox().contains(centerPoint(target)));
            pathfindingComponent.setDoAcceptPredicate(node -> node.getObjects().stream().noneMatch(
                    o -> o instanceof TestObstacleEntity));
            pathfindingComponent.setDoUpdatePredicate(
                    delta -> {
                        updateTimer.update(delta);
                        boolean isFinished = updateTimer.isFinished();
                        if (isFinished) {
                            updateTimer.reset();
                        }
                        return isFinished;
                    });
            pathfindingComponent.setDoAllowDiagonal(() -> false);
            return pathfindingComponent;
        }

        @Override
        public Vector2 getFocus() {
            return getComponent(BodyComponent.class).getCenter();
        }

    }

    private static class TestEntity extends Entity implements CameraFocusable {

        private TestEntity(Rectangle bounds, BodyType bodyType, Color color) {
            addComponent(defineBodyComponent(bounds, bodyType));
            addComponent(defineGraphComponent());
            addComponent(defineDebugComponent(color));
        }

        private BodyComponent defineBodyComponent(Rectangle bounds, BodyType bodyType) {
            BodyComponent bodyComponent = new BodyComponent(bodyType, bounds);
            bodyComponent.setFriction(0f, 0f);
            return bodyComponent;
        }

        private GraphComponent defineGraphComponent() {
            return new GraphComponent(() -> getComponent(BodyComponent.class).getCollisionBox(),
                    () -> List.of(this));
        }

        private DebugRectComponent defineDebugComponent(Color color) {
            return new DebugRectComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> color);
        }

        @Override
        public Vector2 getFocus() {
            return getComponent(BodyComponent.class).getCenter();
        }

    }

}
