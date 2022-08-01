package com.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.MessageListener;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerSystem;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOnCamTransSystem;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.entities.blocks.Block;
import com.game.entities.decorations.DecorativeSprite;
import com.game.entities.enemies.FloatingCan;
import com.game.entities.enemies.Met;
import com.game.entities.enemies.SniperJoe;
import com.game.entities.enemies.SuctionRoller;
import com.game.entities.megaman.Megaman;
import com.game.entities.sensors.DeathSensor;
import com.game.entities.sensors.WallSlideSensor;
import com.game.graph.Graph;
import com.game.graph.GraphSystem;
import com.game.health.HealthComponent;
import com.game.pathfinding.PathfindingSystem;
import com.game.sounds.SoundSystem;
import com.game.spawns.Spawn;
import com.game.spawns.SpawnManager;
import com.game.spawns.SpawnLocation;
import com.game.trajectories.TrajectoryComponent;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.enums.Direction;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.WorldSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.ConstVals.Events.*;
import static com.game.ConstVals.RenderingGround.PLAYGROUND;
import static com.game.ConstVals.SoundAsset.*;
import static com.game.ConstVals.TextureAsset.BITS_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.entities.megaman.MegamanWeapon.*;
import static com.game.utils.UtilMethods.bottomCenterPoint;
import static com.game.utils.UtilMethods.getPoint;
import static com.game.utils.enums.Position.BOTTOM_CENTER;

public class LevelScreen extends ScreenAdapter implements MessageListener {

    private static final String GAME_ROOMS = "GameRooms";
    private static final String ENEMY_SPAWNS = "EnemySpawns";
    private static final String PLAYER_SPAWNS = "PlayerSpawns";
    private static final String DEATH_SENSORS = "DeathSensors";
    private static final String STATIC_BLOCKS = "StaticBlocks";
    private static final String MOVING_BLOCKS = "MovingBlocks";
    private static final String WALL_SLIDE_SENSORS = "WallSlideSensors";

    public static final float LEVEL_CAM_TRANS_DURATION = 1f;
    public static final float MEGAMAN_DELTA_ON_CAM_TRANS = 3f;

    private final String tmxFile;
    private final String musicSrc;
    private final GameContext2d gameContext;
    private final Timer fadeTimer = new Timer();
    private final Timer deathTimer = new Timer(4f);
    private final Sprite blackBoxSprite = new Sprite();

    private Megaman megaman;
    private Music levelMusic;
    private BitsBarUi healthBar;
    private LevelTiledMap levelMap;
    private SpawnManager spawnManager;
    private LevelCameraManager levelCameraManager;

    private boolean isPaused;
    private boolean fadingIn;
    private boolean fadingOut;

    public LevelScreen(GameContext2d gameContext, String tmxFile, String musicSrc) {
        this.gameContext = gameContext;
        this.musicSrc = musicSrc;
        this.tmxFile = tmxFile;
    }

    @Override
    public void show() {
        gameContext.addMessageListener(this);
        gameContext.setDoUpdateController(true);
        gameContext.getSystems().forEach(system -> system.setOn(true));
        levelMusic = gameContext.getAsset(musicSrc, Music.class);
        levelMusic.play();
        deathTimer.setToEnd();
        // define level map
        levelMap = new LevelTiledMap(tmxFile);
        // define level graph
        Graph levelGraph = new Graph(new Vector2(PPM, PPM), levelMap.getWidthInTiles(), levelMap.getHeightInTiles());
        gameContext.getSystem(PathfindingSystem.class).setGraph(levelGraph);
        gameContext.getSystem(GraphSystem.class).setGraph(levelGraph);
        // define spawns and spawn manager
        Rectangle startPlayerSpawn = new Rectangle();
        List<Rectangle> playerSpawns = levelMap.getObjectsOfLayer(PLAYER_SPAWNS).stream().map(playerSpawnObj -> {
            Rectangle rect = playerSpawnObj.getRectangle();
            if (playerSpawnObj.getName().equals("start")) {
                startPlayerSpawn.set(rect);
            }
            return rect;
        }).toList();
        List<Spawn> enemySpawns = levelMap.getObjectsOfLayer(ENEMY_SPAWNS).stream().map(enemySpawnObj ->
                new Spawn(gameContext, getEntitySpawnSupplier(enemySpawnObj), enemySpawnObj.getRectangle())).toList();
        spawnManager = new SpawnManager(gameContext.getViewport(PLAYGROUND).getCamera(), playerSpawns, enemySpawns);
        spawnManager.setCurrentPlayerSpawn(startPlayerSpawn);
        // define static blocks
        levelMap.getObjectsOfLayer(STATIC_BLOCKS).forEach(staticBlockObj ->
                gameContext.addEntity(new Block(staticBlockObj.getRectangle(), new Vector2(.035f, 0f))));
        // define moving blocks
        levelMap.getObjectsOfLayer(MOVING_BLOCKS).forEach(movingBlockObj -> {
            Block movingBlock = new Block(movingBlockObj.getRectangle(), new Vector2(.035f, 0f),
                    false, false, true, true, true);
            // decorative sprites (if applicable)
            MapProperties properties = movingBlockObj.getProperties();
            if (properties.containsKey("DecorativeSrc") && properties.containsKey("DecorativeRegion")) {
                String decorativeSrc = properties.get("DecorativeSrc", String.class);
                String decorativeRegion = properties.get("DecorativeRegion", String.class);
                TextureRegion textureRegion = gameContext.getAsset(decorativeSrc, TextureAtlas.class)
                        .findRegion(decorativeRegion);
                List<DecorativeSprite> decorativeSprites = movingBlock.generateDecorativeBlocks(textureRegion);
                gameContext.addEntities(decorativeSprites);
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
            movingBlock.addComponent(trajectoryComponent);
            // add to game context
            gameContext.addEntity(movingBlock);
        });
        // define wall slide sensors
        levelMap.getObjectsOfLayer(WALL_SLIDE_SENSORS).forEach(wallSlideSensorObj ->
                gameContext.addEntity(new WallSlideSensor(wallSlideSensorObj.getRectangle())));
        // define death sensors
        levelMap.getObjectsOfLayer(DEATH_SENSORS).forEach(deathSensorObj ->
                gameContext.addEntity(new DeathSensor(deathSensorObj.getRectangle())));
        // define game rooms
        Map<Rectangle, String> gameRooms = new HashMap<>();
        levelMap.getObjectsOfLayer(GAME_ROOMS).forEach(gameRoomObj ->
                gameRooms.put(gameRoomObj.getRectangle(), gameRoomObj.getName()));
        // define level cam manager
        levelCameraManager = new LevelCameraManager(gameContext.getViewport(PLAYGROUND).getCamera(),
                new Timer(1f), gameRooms, megaman);
        gameContext.getSystem(CullOnCamTransSystem.class).setTransitionStateSupplier(
                () -> levelCameraManager.getTransitionState());
        // spawn Megaman
        spawnMegaman();
        // health bar ui
        TextureRegion healthBit = gameContext.getAsset(BITS_ATLAS.getSrc(), TextureAtlas.class).findRegion("HealthBit");
        healthBar = new BitsBarUi(gameContext, () -> megaman.getComponent(HealthComponent.class).getCurrentHealth(),
                () -> healthBit, new Vector2(8f, 2f), new Rectangle(PPM, 9f * PPM, 8f, 60f));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (isPaused) {
            onGamePaused(delta);
            if (gameContext.isJustPressed(ControllerButton.START)) {
                isPaused = false;
            }
        } else {
            onGameRunning(delta);
            if (gameContext.isJustPressed(ControllerButton.START)) {
                isPaused = true;
            }
        }
    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (owner.equals(megaman) && message.equals(PLAYER_DEAD)) {
            gameContext.getSystem(SoundSystem.class).requestToStopAllLoopingSounds();
            gameContext.getAsset(MEGAMAN_DEFEAT_SOUND.getSrc(), Sound.class).play();
            deathTimer.reset();
            levelMusic.stop();
        }
    }

    @Override
    public void dispose() {
        levelMap.dispose();
        gameContext.purgeAllEntities();
        gameContext.removeMessageListener(this);
    }

    private void onGameRunning(float delta) {
        // TODO: Allow megaman to change weapons from menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (megaman.getCurrentWeapon().equals(MEGA_BUSTER)) {
                megaman.setCurrentWeapon(FLAME_BUSTER);
            } else {
                megaman.setCurrentWeapon(MEGA_BUSTER);
            }
        }
        levelCameraManager.update(delta);
        levelMap.draw((OrthographicCamera) gameContext.getViewport(PLAYGROUND).getCamera(),
                gameContext.getSpriteBatch());
        if (levelCameraManager.getTransitionState() != null) {
            gameContext.setDoUpdateController(false);
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> {
                    gameContext.getSystem(ControllerSystem.class).setOn(false);
                    gameContext.getSystem(TrajectorySystem.class).setOn(false);
                    gameContext.getSystem(UpdatableSystem.class).setOn(false);
                    gameContext.getSystem(BehaviorSystem.class).setOn(false);
                    gameContext.getSystem(WorldSystem.class).setOn(false);
                    gameContext.setDoUpdateController(false);
                    megaman.getChargingTimer().reset();
                    megaman.getComponent(BodyComponent.class).getVelocity().setZero();
                }
                case CONTINUE -> {
                    Direction direction = levelCameraManager.getTransitionDirection();
                    BodyComponent bodyComponent = megaman.getComponent(BodyComponent.class);
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
                    gameContext.getSystem(ControllerSystem.class).setOn(true);
                    gameContext.getSystem(TrajectorySystem.class).setOn(true);
                    gameContext.getSystem(UpdatableSystem.class).setOn(true);
                    gameContext.getSystem(BehaviorSystem.class).setOn(true);
                    gameContext.getSystem(WorldSystem.class).setOn(true);
                    gameContext.setDoUpdateController(true);
                }
            }
        }
        if (levelCameraManager.getTransitionState() == null) {
            spawnManager.update();
        }
        gameContext.updateSystems(delta);
        deathTimer.update(delta);
        if (deathTimer.isJustFinished()) {
            levelMusic.play();
            spawnMegaman();
        }
        healthBar.draw();

        // TODO: Handle fading in and out when level starts or player dies
    }

    private void onGamePaused(float delta) {

    }

    private void spawnMegaman() {
        Vector2 spawnPos = bottomCenterPoint(spawnManager.getCurrentPlayerSpawn());
        megaman = new Megaman(gameContext, spawnPos);
        levelCameraManager.setFocusable(megaman);
        gameContext.addEntity(megaman);
        gameContext.getEntities().forEach(entity -> {
            if (entity.hasComponent(CullOnCamTransComponent.class) ||
                    entity.hasComponent(CullOutOfCamBoundsComponent.class)) {
                entity.setDead(true);
            }
        });
    }

    private Supplier<IEntity> getEntitySpawnSupplier(RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "met" -> {
                return () -> new Met(gameContext, () -> megaman,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "sniper_joe" -> {
                return () -> new SniperJoe(gameContext, () -> megaman,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "suction_roller" -> {
                return () -> new SuctionRoller(gameContext, () -> megaman,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "floating_can" -> {
                return () ->  new SpawnLocation(gameContext, gameContext.getViewport(PLAYGROUND).getCamera(),
                        spawnObj.getRectangle(), 4, 3f, () -> new FloatingCan(gameContext, () -> megaman,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER)));
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

    private void fadeIn(float delta) {
        if (!fadingIn) {
            initFadeIn();
        }
        fadingIn = true;
        fadingOut = false;
        fadeTimer.update(delta);
        blackBoxSprite.setAlpha(Math.max(0f, 1f - fadeTimer.getRatio()));
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        boolean spriteBatchDrawing = spriteBatch.isDrawing();
        if (!spriteBatchDrawing) {
            spriteBatch.begin();
        }
        blackBoxSprite.draw(spriteBatch);
        if (!spriteBatchDrawing) {
            spriteBatch.end();
        }
        fadingIn = !fadeTimer.isFinished();
    }

    private void fadeOut(float delta) {
        if (!fadingOut) {
            initFadeOut();
        }
        fadingOut = true;
        fadingIn = false;
        fadeTimer.update(delta);
        blackBoxSprite.setAlpha(Math.min(1f, fadeTimer.getRatio()));
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        boolean spriteBatchDrawing = spriteBatch.isDrawing();
        if (!spriteBatchDrawing) {
            spriteBatch.begin();
        }
        blackBoxSprite.draw(spriteBatch);
        if (!spriteBatchDrawing) {
            spriteBatch.end();
        }
        fadingOut = !fadeTimer.isFinished();
    }

    private void initFadeIn() throws IllegalStateException {
        blackBoxSprite.setBounds(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fadeTimer.reset();
    }

    private void initFadeOut() throws IllegalStateException {
        blackBoxSprite.setBounds(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackBoxSprite.setAlpha(0f);
        fadeTimer.reset();
    }

}
