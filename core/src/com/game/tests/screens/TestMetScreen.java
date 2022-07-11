package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.WorldVals;
import com.game.MessageListener;
import com.game.animations.AnimationSystem;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerSystem;
import com.game.core.IEntity;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugSystem;
import com.game.health.HealthComponent;
import com.game.health.HealthSystem;
import com.game.sound.SoundSystem;
import com.game.sprites.SpriteSystem;
import com.game.tests.core.*;
import com.game.tests.entities.*;
import com.game.trajectories.TrajectoryComponent;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.*;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import com.game.world.WorldSystem;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import com.game.levels.LevelCameraManager;
import com.game.levels.LevelTiledMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.*;
import static com.game.utils.UtilMethods.*;
import static com.game.world.FixtureType.*;
import static com.game.levels.LevelScreen.LEVEL_CAM_TRANS_DURATION;
import static com.game.levels.LevelScreen.MEGAMAN_DELTA_ON_CAM_TRANS;

/**
 * When the player dies, there should be 8 explosion orbs and about 3 seconds of delay before switching back to
 * the last spawn point.
 * <p>
 * Process:
 * 1. Player dies, explosion orb decorations are spawned with trajectories, and timer is reset
 * 2. When timer reaches zero, player is respawned with health at 100%
 */
public class TestMetScreen extends ScreenAdapter implements MessageListener {

    private static final String GAME_ROOMS = "GameRooms";
    private static final String ENEMY_SPAWNS = "EnemySpawns";
    private static final String PLAYER_SPAWNS = "PlayerSpawns";
    private static final String DEATH_SENSORS = "DeathSensors";
    private static final String STATIC_BLOCKS = "StaticBlocks";
    private static final String MOVING_BLOCKS = "MovingBlocks";
    private static final String WALL_SLIDE_SENSORS = "WallSlideSensors";

    private final Timer deathTimer = new Timer(4f);
    private final List<FontHandle> messages = new ArrayList<>();

    private LevelTiledMap levelTiledMap;
    private LevelCameraManager levelCameraManager;
    private TestController testController;
    private TestMessageDispatcher messageDispatcher;
    private TestEntitySpawnManager entitySpawnManager;
    private TestEntitiesAndSystemsManager entitiesAndSystemsManager;
    private SpriteBatch spriteBatch;
    private TestAssetLoader assetLoader;
    private Viewport uiViewport;
    private Viewport playgroundViewport;
    private TestBlock testMovingBlock;
    private TestPlayer player;
    private boolean isPaused;
    private Music music;

    @Override
    public void show() {
        music = Gdx.audio.newMusic(Gdx.files.internal("music/MMX5_VoltKraken.mp3"));
        music.play();
        for (int i = 0; i < 3; i++) {
            FontHandle message = new FontHandle("Megaman10Font.ttf", 6);
            message.setColor(Color.WHITE);
            message.setPosition(-VIEW_WIDTH * PPM / 3f, (-VIEW_HEIGHT * PPM / 4f) + (i * PPM));
            messages.add(message);
        }
        deathTimer.setToEnd();
        testController = new TestController();
        messageDispatcher = new TestMessageDispatcher();
        messageDispatcher.addListener(this);
        entitiesAndSystemsManager = new TestEntitiesAndSystemsManager();
        spriteBatch = new SpriteBatch();
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        uiViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        assetLoader = new TestAssetLoader();
        entitiesAndSystemsManager.addSystem(new WorldSystem(new TestWorldContactListener(),
                WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP));
        entitiesAndSystemsManager.addSystem(new UpdatableSystem());
        entitiesAndSystemsManager.addSystem(new ControllerSystem(testController));
        entitiesAndSystemsManager.addSystem(new HealthSystem());
        entitiesAndSystemsManager.addSystem(new BehaviorSystem());
        entitiesAndSystemsManager.addSystem(new TrajectorySystem());
        entitiesAndSystemsManager.addSystem(new SpriteSystem(
                (OrthographicCamera) playgroundViewport.getCamera(), spriteBatch));
        entitiesAndSystemsManager.addSystem(new AnimationSystem());
        entitiesAndSystemsManager.addSystem(new SoundSystem(assetLoader));
        entitiesAndSystemsManager.addSystem(new DebugSystem(shapeRenderer,
                (OrthographicCamera) playgroundViewport.getCamera()));
        levelTiledMap = new LevelTiledMap((OrthographicCamera) playgroundViewport.getCamera(),
                spriteBatch, "tiledmaps/tmx/test1.tmx");
        // define enemySpawns
        Rectangle startPlayerSpawn = new Rectangle();
        List<Rectangle> playerSpawns = levelTiledMap.getObjectsOfLayer(PLAYER_SPAWNS).stream().map(obj -> {
            Rectangle rect = obj.getRectangle();
            if (obj.getName().equals("start")) {
                startPlayerSpawn.set(rect);
            }
            return rect;
        }).toList();
        List<TestEntitySpawn> enemySpawns = levelTiledMap.getObjectsOfLayer(ENEMY_SPAWNS).stream().map(
                enemySpawnObj -> new TestEntitySpawn(entitiesAndSystemsManager, getEntitySpawnSupplier(enemySpawnObj),
                        enemySpawnObj.getRectangle())).toList();
        entitySpawnManager = new TestEntitySpawnManager(playgroundViewport.getCamera(), playerSpawns, enemySpawns);
        entitySpawnManager.setCurrentPlayerSpawn(startPlayerSpawn);
        // define player
        player = new TestPlayer(getPoint(entitySpawnManager.getCurrentPlayerSpawn(), Position.BOTTOM_CENTER), music,
                testController, assetLoader, messageDispatcher, entitiesAndSystemsManager);
        entitiesAndSystemsManager.addEntity(player);
        // define static blocks
        levelTiledMap.getObjectsOfLayer(STATIC_BLOCKS).forEach(staticBlockObj ->
                entitiesAndSystemsManager.addEntity(new TestBlock(staticBlockObj.getRectangle(),
                        false, false, new Vector2(.035f, 0f))));
        // define moving blocks
        levelTiledMap.getObjectsOfLayer(MOVING_BLOCKS).forEach(movingBlockObj -> {
            testMovingBlock = new TestBlock(movingBlockObj.getRectangle(), false, false, new Vector2(.035f, 0f));
            TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
            String[] trajectories = movingBlockObj.getProperties().get("Trajectory", String.class).split(";");
            for (String trajectory : trajectories) {
                String[] params = trajectory.split(",");
                float x = Float.parseFloat(params[0]);
                float y = Float.parseFloat(params[1]);
                float time = Float.parseFloat(params[2]);
                trajectoryComponent.addTrajectory(new Vector2(x * PPM, y * PPM), time);
            }
            testMovingBlock.addComponent(trajectoryComponent);
            BodyComponent bodyComponent = testMovingBlock.getComponent(BodyComponent.class);
            Fixture leftWallSlide = new Fixture(testMovingBlock, WALL_SLIDE_SENSOR);
            leftWallSlide.setSize(PPM / 2f, bodyComponent.getCollisionBox().height - PPM / 3f);
            leftWallSlide.setOffset(-bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(leftWallSlide);
            Fixture rightWallSlide = new Fixture(testMovingBlock, WALL_SLIDE_SENSOR);
            rightWallSlide.setSize(PPM / 2f, bodyComponent.getCollisionBox().height - PPM / 3f);
            rightWallSlide.setOffset(bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(rightWallSlide);
            Fixture feetSticker = new Fixture(testMovingBlock, FEET_STICKER);
            feetSticker.setSize(bodyComponent.getCollisionBox().width, PPM / 3f);
            feetSticker.setOffset(0f, (bodyComponent.getCollisionBox().height / 2f) - 2f);
            bodyComponent.addFixture(feetSticker);
            DebugComponent debugComponent = new DebugComponent();
            debugComponent.addDebugHandle(bodyComponent::getCollisionBox, () -> Color.BLUE);
            bodyComponent.getFixtures().forEach(fixture -> {
                if (fixture.getFixtureType() != BLOCK) {
                    debugComponent.addDebugHandle(fixture::getFixtureBox, () -> Color.GREEN);
                }
            });
            testMovingBlock.addComponent(debugComponent);
            entitiesAndSystemsManager.addEntity(testMovingBlock);
        });
        // define wall slide sensors
        levelTiledMap.getObjectsOfLayer(WALL_SLIDE_SENSORS).forEach(wallSlideSensorObj ->
                entitiesAndSystemsManager.addEntity(new TestWallSlideSensor(wallSlideSensorObj.getRectangle())));
        // define death sensors
        levelTiledMap.getObjectsOfLayer(DEATH_SENSORS).forEach(deathSensorObj ->
                entitiesAndSystemsManager.addEntity(new TestDeathSensor(deathSensorObj.getRectangle())));
        // define game rooms
        Map<Rectangle, String> gameRooms = new HashMap<>();
        levelTiledMap.getObjectsOfLayer(GAME_ROOMS).forEach(rectangleMapObject ->
                gameRooms.put(rectangleMapObject.getRectangle(), rectangleMapObject.getName()));
        // level camera manager
        Timer transitionTimer = new Timer(1f);
        levelCameraManager = new LevelCameraManager(playgroundViewport.getCamera(), transitionTimer, gameRooms, player);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
        }
        if (isPaused) {
            return;
        }
        if (levelCameraManager.getTransitionState() == null) {
            entitySpawnManager.update();
            testController.updateController();
        }
        levelTiledMap.draw();
        levelCameraManager.update(delta);
        entitiesAndSystemsManager.updateSystems(delta);
        messageDispatcher.updateMessageDispatcher(delta);
        entitiesAndSystemsManager.getEntities().forEach(entity -> {
            if (entity instanceof CullOnOutOfCamBounds cull &&
                    !playgroundViewport.getCamera().frustum.boundsInFrustum(rectToBBox(cull.getCullBoundingBox()))) {
                entity.setDead(true);
            }
        });
        deathTimer.update(delta);
        if (deathTimer.isJustFinished()) {
            music.play();
            player = new TestPlayer(UtilMethods.bottomCenterPoint(entitySpawnManager.getCurrentPlayerSpawn()),
                    music, testController, assetLoader, messageDispatcher, entitiesAndSystemsManager);
            levelCameraManager.setFocusable(player);
            entitiesAndSystemsManager.addEntity(player);
            entitySpawnManager.reset();
        }
        Rectangle playerBounds = player.getComponent(BodyComponent.class).getCollisionBox();
        for (int i = 0; i < 3; i++) {
            FontHandle fontHandle = messages.get(i);
            switch (i) {
                case 0 -> fontHandle.setText("Health: " + player.getComponent(HealthComponent.class).getCurrentHealth());
                case 1 -> fontHandle.setText("Entities: " + entitiesAndSystemsManager.getEntities().size());
                case 2 -> fontHandle.setText("Player: " + roundedFloat(playerBounds.x, 1) + ", " +
                        roundedFloat(playerBounds.y, 1));
            }
        }
        if (levelCameraManager.getTransitionState() != null) {
            BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> {
                    bodyComponent.getVelocity().setZero();
                    entitiesAndSystemsManager.getSystem(ControllerSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(BehaviorSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(WorldSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(UpdatableSystem.class).setOn(false);
                    entitiesAndSystemsManager.getEntities().forEach(entity -> {
                        if (entity instanceof CullOnLevelCamTrans) {
                            entity.setDead(true);
                        }
                    });
                }
                case CONTINUE -> {
                    entitiesAndSystemsManager.getEntities().forEach(entity -> {
                        if (entity instanceof CullOnLevelCamTrans) {
                            entity.setDead(true);
                        }
                    });
                    Direction direction = levelCameraManager.getTransitionDirection();
                    switch (direction) {
                        case UP -> bodyComponent.getCollisionBox().y +=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case DOWN -> bodyComponent.getCollisionBox().y -=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case LEFT -> bodyComponent.getCollisionBox().x -=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case RIGHT -> bodyComponent.getCollisionBox().x +=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                    }
                }
                case END -> {
                    entitiesAndSystemsManager.getSystem(ControllerSystem.class).setOn(true);
                    entitiesAndSystemsManager.getSystem(BehaviorSystem.class).setOn(true);
                    entitiesAndSystemsManager.getSystem(WorldSystem.class).setOn(true);
                    entitiesAndSystemsManager.getSystem(UpdatableSystem.class).setOn(true);
                }
            }
        }
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        messages.forEach(message -> message.draw(spriteBatch));
        spriteBatch.end();
        playgroundViewport.apply();
        uiViewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }

    private Supplier<IEntity> getEntitySpawnSupplier(RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "test" -> {
                return () -> new TestDamager(spawnObj.getRectangle());
            }
            case "met" -> {
                return () -> new TestMet(entitiesAndSystemsManager, assetLoader, () -> player,
                        getPoint(spawnObj.getRectangle(), Position.BOTTOM_CENTER));
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (owner.equals(player) && message.equals("DEAD")) {
            deathTimer.reset();
        }
    }

}
