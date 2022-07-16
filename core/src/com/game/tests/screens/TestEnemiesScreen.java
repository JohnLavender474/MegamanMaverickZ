package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.WorldVals;
import com.game.MessageListener;
import com.game.animations.AnimationSystem;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerSystem;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransSystem;
import com.game.cull.CullOnOutOfCamBoundsSystem;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugSystem;
import com.game.health.HealthComponent;
import com.game.health.HealthSystem;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import com.game.levels.LevelCameraManager;
import com.game.levels.LevelTiledMap;
import com.game.sprites.SpriteSystem;
import com.game.tests.core.*;
import com.game.tests.entities.*;
import com.game.trajectories.TrajectoryComponent;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.Direction;
import com.game.utils.FontHandle;
import com.game.utils.Position;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.WorldSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.*;
import static com.game.levels.LevelScreen.LEVEL_CAM_TRANS_DURATION;
import static com.game.levels.LevelScreen.MEGAMAN_DELTA_ON_CAM_TRANS;
import static com.game.utils.UtilMethods.*;
import static com.game.world.FixtureType.BLOCK;

/**
 * When the player dies, there should be 8 explosion orbs and about 3 seconds of delay before switching back to
 * the last spawn point.
 * <p>
 * Process:
 * 1. Player dies, explosion orb decorations are spawned with trajectories, and timer is reset
 * 2. When timer reaches zero, player is respawned with health at 100%
 */
public class TestEnemiesScreen extends ScreenAdapter implements MessageListener {

    private static final String GAME_ROOMS = "GameRooms";
    private static final String ENEMY_SPAWNS = "EnemySpawns";
    private static final String PLAYER_SPAWNS = "PlayerSpawns";
    private static final String DEATH_SENSORS = "DeathSensors";
    private static final String STATIC_BLOCKS = "StaticBlocks";
    private static final String MOVING_BLOCKS = "MovingBlocks";
    private static final String WALL_SLIDE_SENSORS = "WallSlideSensors";

    private final Timer deathTimer = new Timer(4f);
    private final Timer blackTimer = new Timer(.3f);
    private final Map<String, FontHandle> messages = new HashMap<>();

    private LevelTiledMap levelTiledMap;
    private TestController testController;
    private LevelCameraManager levelCameraManager;
    private TestMessageDispatcher messageDispatcher;
    private TestEntitySpawnManager entitySpawnManager;
    private TestEntitiesAndSystemsManager entitiesAndSystemsManager;
    private ShapeRenderer shapeRenderer;
    private TestAssetLoader assetLoader;
    private SpriteBatch spriteBatch;
    private Viewport playgroundViewport;
    private Viewport uiViewport;
    private TestBlock testMovingBlock;
    private TestPlayer player;
    private boolean isPaused;
    private Sprite black;
    private Music music;

    @Override
    public void show() {
        messages.put("Health", new FontHandle("Megaman10Font.ttf", 20, new Vector2(-800, 400)));
        messages.put("AmountInCamBounds", new FontHandle("Megaman10Font.ttf", 20, new Vector2(-800, 350)));
        messages.put("EntityCount", new FontHandle("Megaman10Font.ttf", 20, new Vector2(-800, 300)));
        messages.put("Behaviors", new FontHandle("Megaman10Font.ttf", 20, new Vector2(-800, 250)));
        messages.put("BodySenses", new FontHandle("Megaman10Font.ttf", 20, new Vector2(-800, 200)));
        music = Gdx.audio.newMusic(Gdx.files.internal("music/MMX5_VoltKraken.mp3"));
        music.play();
        deathTimer.setToEnd();
        testController = new TestController();
        messageDispatcher = new TestMessageDispatcher();
        messageDispatcher.addListener(this);
        entitiesAndSystemsManager = new TestEntitiesAndSystemsManager();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        uiViewport = new FitViewport(1920, 1080);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        assetLoader = new TestAssetLoader();
        black = new Sprite(assetLoader.getAsset(DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class).findRegion("Black"));
        black.setSize(1920f, 1080f);
        black.setCenter(0f, 0f);
        entitiesAndSystemsManager.addSystem(new WorldSystem(new TestWorldContactListener(),
                WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP));
        entitiesAndSystemsManager.addSystem(new CullOnCamTransSystem(() -> levelCameraManager.getTransitionState()));
        entitiesAndSystemsManager.addSystem(new CullOnOutOfCamBoundsSystem(playgroundViewport.getCamera()));
        entitiesAndSystemsManager.addSystem(new UpdatableSystem());
        entitiesAndSystemsManager.addSystem(new ControllerSystem(testController));
        entitiesAndSystemsManager.addSystem(new HealthSystem());
        entitiesAndSystemsManager.addSystem(new BehaviorSystem());
        entitiesAndSystemsManager.addSystem(new TrajectorySystem());
        entitiesAndSystemsManager.addSystem(new SpriteSystem(
                (OrthographicCamera) playgroundViewport.getCamera(), spriteBatch));
        entitiesAndSystemsManager.addSystem(new AnimationSystem());
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
        entitySpawnManager = new TestEntitySpawnManager(playgroundViewport.getCamera(), shapeRenderer,
                playerSpawns, enemySpawns);
        entitySpawnManager.setCurrentPlayerSpawn(startPlayerSpawn);
        // define player
        player = new TestPlayer(getPoint(entitySpawnManager.getCurrentPlayerSpawn(), Position.BOTTOM_CENTER), music,
                testController, assetLoader, messageDispatcher, entitiesAndSystemsManager);
        entitiesAndSystemsManager.addEntity(player);
        // define static blocks
        levelTiledMap.getObjectsOfLayer(STATIC_BLOCKS).forEach(staticBlockObj ->
                entitiesAndSystemsManager.addEntity(new TestBlock(
                        staticBlockObj.getRectangle(), new Vector2(.035f, 0f))));
        // define moving blocks
        levelTiledMap.getObjectsOfLayer(MOVING_BLOCKS).forEach(movingBlockObj -> {
            testMovingBlock = new TestBlock(movingBlockObj.getRectangle(), new Vector2(.035f, 0f),
                    false, false, true, true, true);
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
            Gdx.audio.newSound(Gdx.files.internal("sounds/PauseMenu.mp3")).play();
            isPaused = !isPaused;
        }
        if (isPaused) {
            return;
        }
        levelCameraManager.update(delta);
        levelTiledMap.draw();
        if (levelCameraManager.getTransitionState() == null) {
            entitySpawnManager.update();
            testController.updateController();
        }
        entitiesAndSystemsManager.updateSystems(delta);
        messageDispatcher.updateMessageDispatcher(delta);
        entitiesAndSystemsManager.getEntities().stream()
                .filter(entity -> entity instanceof CullOnOutOfCamBounds)
                .map(entity -> (CullOnOutOfCamBounds) entity)
                .forEach(cull -> {
                    if (!playgroundViewport.getCamera().frustum.boundsInFrustum(
                            rectToBBox(cull.getCullBoundingBox()))) {
                        cull.getCullTimer().update(delta);
                    } else if (deathTimer.isFinished()) {
                        cull.getCullTimer().reset();
                    }
                    if (cull.getCullTimer().isFinished()) {
                        ((IEntity) cull).setDead(true);
                    }
                });
        deathTimer.update(delta);
        if (deathTimer.isJustFinished()) {
            music.play();
            player = new TestPlayer(bottomCenterPoint(entitySpawnManager.getCurrentPlayerSpawn()),
                    music, testController, assetLoader, messageDispatcher, entitiesAndSystemsManager);
            levelCameraManager.setFocusable(player);
            entitiesAndSystemsManager.addEntity(player);
            entitySpawnManager.reset();
            entitiesAndSystemsManager.getEntities().forEach(entity -> {
                if (entity instanceof CullOnLevelCamTrans || entity instanceof CullOnOutOfCamBounds) {
                    entity.setDead(true);
                    if (entity instanceof CullOnOutOfCamBounds cull) {
                        cull.getCullTimer().setToEnd();
                    }
                }
            });
            blackTimer.reset();
        }
        if (levelCameraManager.getTransitionState() != null) {
            BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> {
                    bodyComponent.getVelocity().setZero();
                    entitiesAndSystemsManager.getSystem(ControllerSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(TrajectorySystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(UpdatableSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(BehaviorSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(WorldSystem.class).setOn(false);
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
                    entitiesAndSystemsManager.getSystem(TrajectorySystem.class).setOn(true);
                    entitiesAndSystemsManager.getSystem(UpdatableSystem.class).setOn(true);
                    entitiesAndSystemsManager.getSystem(BehaviorSystem.class).setOn(true);
                    entitiesAndSystemsManager.getSystem(WorldSystem.class).setOn(true);
                }
            }
        }
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        HealthComponent health = player.getComponent(HealthComponent.class);
        messages.get("Health").setText("Health: " + health.getCurrentHealth() + "/" + health.getMaxHealth());
        messages.get("Health").draw(spriteBatch);
        messages.get("AmountInCamBounds").setText("Enemy spawns in cam bounds: " +
                entitySpawnManager.amountOfEnemySpawnsInCamBounds());
        messages.get("AmountInCamBounds").draw(spriteBatch);
        messages.get("EntityCount").setText("Entity count: " + entitiesAndSystemsManager.getEntities().size());
        messages.get("EntityCount").draw(spriteBatch);
        messages.get("Behaviors").setText("Behaviors: " + player.getComponent(
                BehaviorComponent.class).getActiveBehaviors());
        messages.get("Behaviors").draw(spriteBatch);
        messages.get("BodySenses").setText("Body senses: " + player.getComponent(
                BodyComponent.class).getBodySenses());
        messages.get("BodySenses").draw(spriteBatch);
        if (!blackTimer.isFinished()) {
            blackTimer.update(delta);
            black.draw(spriteBatch);
        }
        spriteBatch.end();
        uiViewport.apply();
        playgroundViewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        levelTiledMap.dispose();
        assetLoader.dispose();
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
            case "sniper_joe" -> {
                return () -> new TestSniperJoe(entitiesAndSystemsManager, assetLoader, () -> player,
                        getPoint(spawnObj.getRectangle(), Position.BOTTOM_CENTER));
            }
            case "suction_roller" -> {
                return () -> new TestSuctionRoller(assetLoader, () -> player, getPoint(
                        spawnObj.getRectangle(), Position.BOTTOM_CENTER));
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (owner.equals(player) && message.equals("DEAD")) {
            deathTimer.reset();
        }
    }

}
