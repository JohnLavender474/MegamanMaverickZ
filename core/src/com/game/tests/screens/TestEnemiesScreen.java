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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
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
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOnCamTransSystem;
import com.game.cull.CullOnOutOfCamBoundsSystem;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.debugging.DebugLinesSystem;
import com.game.debugging.DebugRectComponent;
import com.game.debugging.DebugRectSystem;
import com.game.graph.Graph;
import com.game.graph.GraphSystem;
import com.game.health.HealthComponent;
import com.game.health.HealthSystem;
import com.game.levels.LevelCameraManager;
import com.game.levels.LevelTiledMap;
import com.game.pathfinding.PathfindingSystem;
import com.game.sprites.SpriteSystem;
import com.game.tests.core.*;
import com.game.tests.entities.*;
import com.game.trajectories.TrajectoryComponent;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.enums.Direction;
import com.game.utils.objects.FontHandle;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.WorldSystem;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.*;
import static com.game.levels.LevelScreen.LEVEL_CAM_TRANS_DURATION;
import static com.game.levels.LevelScreen.MEGAMAN_DELTA_ON_CAM_TRANS;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
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
    private final List<Runnable> runOnShutdown = new ArrayList<>();
    private final Map<String, FontHandle> messages = new HashMap<>();

    private LevelTiledMap levelTiledMap;
    private TestController testController;
    private LevelCameraManager levelCameraManager;
    private TestMessageDispatcher messageDispatcher;
    private TestEntitySpawnManager entitySpawnManager;
    private TestEntitiesAndSystemsManager entitiesAndSystemsManager;
    private ShapeRenderer shapeRenderer;
    private TestAssetLoader assetLoader;
    private Viewport playgroundViewport;
    private TestBlock testMovingBlock;
    private SpriteBatch spriteBatch;
    private Viewport uiViewport;
    private TestPlayer player;
    private Graph levelGraph;
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
        entitiesAndSystemsManager.addSystem(new PathfindingSystem(runOnShutdown));
        entitiesAndSystemsManager.addSystem(new UpdatableSystem());
        entitiesAndSystemsManager.addSystem(new GraphSystem());
        entitiesAndSystemsManager.addSystem(new ControllerSystem(testController));
        entitiesAndSystemsManager.addSystem(new HealthSystem());
        entitiesAndSystemsManager.addSystem(new BehaviorSystem());
        entitiesAndSystemsManager.addSystem(new TrajectorySystem());
        entitiesAndSystemsManager.addSystem(new SpriteSystem(
                (OrthographicCamera) playgroundViewport.getCamera(), spriteBatch));
        entitiesAndSystemsManager.addSystem(new AnimationSystem());
        entitiesAndSystemsManager.addSystem(new DebugRectSystem(playgroundViewport.getCamera(), shapeRenderer));
        entitiesAndSystemsManager.addSystem(new DebugLinesSystem(playgroundViewport.getCamera(), shapeRenderer));
        levelTiledMap = new LevelTiledMap("tiledmaps/tmx/test1.tmx");
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
        entitySpawnManager = new TestEntitySpawnManager(
                playgroundViewport.getCamera(), shapeRenderer, playerSpawns, enemySpawns);
        entitySpawnManager.setCurrentPlayerSpawn(startPlayerSpawn);
        // define player
        player = new TestPlayer(getPoint(entitySpawnManager.getCurrentPlayerSpawn(), BOTTOM_CENTER), music,
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
            // decorative sprites (if applicable)
            MapProperties properties = movingBlockObj.getProperties();
            if (properties.containsKey("DecorativeSrc") && properties.containsKey("DecorativeRegion")) {
                String decorativeSrc = properties.get("DecorativeSrc", String.class);
                String decorativeRegion = properties.get("DecorativeRegion", String.class);
                TextureRegion textureRegion = assetLoader.getAsset(decorativeSrc, TextureAtlas.class)
                        .findRegion(decorativeRegion);
                List<TestDecorativeSprite> decorativeSprites = testMovingBlock.generateDecorativeBlocks(textureRegion);
                entitiesAndSystemsManager.addEntities(decorativeSprites);
            }
            // trajectory
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
            // debug rect
            BodyComponent bodyComponent = testMovingBlock.getComponent(BodyComponent.class);
            DebugRectComponent debugRectComponent = new DebugRectComponent();
            debugRectComponent.addDebugHandle(bodyComponent::getCollisionBox, () -> Color.BLUE);
            bodyComponent.getFixtures().forEach(fixture -> {
                if (fixture.getFixtureType() != BLOCK) {
                    debugRectComponent.addDebugHandle(fixture::getFixtureBox, () -> Color.GREEN);
                }
            });
            testMovingBlock.addComponent(debugRectComponent);
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
        // Level graph
        levelGraph = new Graph(new Vector2(PPM, PPM),
                levelTiledMap.getWidthInTiles(), levelTiledMap.getHeightInTiles());
        entitiesAndSystemsManager.getSystem(GraphSystem.class).setGraph(levelGraph);
        entitiesAndSystemsManager.getSystem(PathfindingSystem.class).setGraph(levelGraph);
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
        levelTiledMap.draw((OrthographicCamera) playgroundViewport.getCamera(), spriteBatch);
        if (levelCameraManager.getTransitionState() != null) {
            BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> {
                    player.getChargingSound().stop();
                    bodyComponent.getVelocity().setZero();
                    player.getMegaBusterChargingTimer().reset();
                    entitiesAndSystemsManager.getSystem(ControllerSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(TrajectorySystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(UpdatableSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(BehaviorSystem.class).setOn(false);
                    entitiesAndSystemsManager.getSystem(WorldSystem.class).setOn(false);
                }
                case CONTINUE -> {
                    Direction direction = levelCameraManager.getTransitionDirection();
                    switch (direction) {
                        case DIR_UP -> bodyComponent.getCollisionBox().y +=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case DIR_DOWN -> bodyComponent.getCollisionBox().y -=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case DIR_LEFT -> bodyComponent.getCollisionBox().x -=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case DIR_RIGHT -> bodyComponent.getCollisionBox().x +=
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
        if (levelCameraManager.getTransitionState() == null) {
            entitySpawnManager.update();
            testController.updateController();
        }
        entitiesAndSystemsManager.updateSystems(delta);
        levelGraph.draw(shapeRenderer, Color.BLUE);
        messageDispatcher.updateMessageDispatcher(delta);
        deathTimer.update(delta);
        if (deathTimer.isJustFinished()) {
            music.play();
            Vector2 spawn = bottomCenterPoint(entitySpawnManager.getCurrentPlayerSpawn());
            player = new TestPlayer(spawn, music, testController, assetLoader,
                    messageDispatcher, entitiesAndSystemsManager);
            levelCameraManager.setFocusable(player);
            entitiesAndSystemsManager.addEntity(player);
            entitySpawnManager.reset();
            entitiesAndSystemsManager.getEntities().forEach(entity -> {
                if (entity.hasComponent(CullOnCamTransComponent.class) ||
                        entity.hasComponent(CullOutOfCamBoundsComponent.class)) {
                    entity.setDead(true);
                }
            });
            blackTimer.reset();
        }
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        HealthComponent health = player.getComponent(HealthComponent.class);
        messages.get("Health").setText("Health: " + health.getCurrentHealth() + "/" + health.getMaxHealth());
        messages.get("Health").draw(spriteBatch);
        messages.get("AmountInCamBounds").setText("AbstractEnemy spawns in cam bounds: " +
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
        assetLoader.dispose();
        shapeRenderer.dispose();
        levelTiledMap.dispose();
        runOnShutdown.forEach(Runnable::run);
    }

    private Supplier<IEntity> getEntitySpawnSupplier(RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "test" -> {
                return () -> new TestDamager(spawnObj.getRectangle());
            }
            case "met" -> {
                return () -> new TestMet(entitiesAndSystemsManager, assetLoader, () -> player,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "sniper_joe" -> {
                return () -> new TestSniperJoe(entitiesAndSystemsManager, assetLoader, () -> player,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "suction_roller" -> {
                return () -> new TestSuctionRoller(entitiesAndSystemsManager, assetLoader, () -> player,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "floating_can" -> {
                return () ->  new TestSpawnLocation(entitiesAndSystemsManager,
                        playgroundViewport.getCamera(), spawnObj.getRectangle(), 4, 3f,
                        () -> new TestFloatingCan(assetLoader, () -> player,
                                getPoint(spawnObj.getRectangle(), BOTTOM_CENTER)));
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
