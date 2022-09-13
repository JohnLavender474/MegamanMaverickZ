package com.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerSystem;
import com.game.core.DebugLogger;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOnCamTransSystem;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.entities.blocks.Block;
import com.game.entities.bosses.TimberWoman;
import com.game.entities.enemies.*;
import com.game.entities.hazards.LaserBeamer;
import com.game.entities.hazards.Saw;
import com.game.entities.megaman.Megaman;
import com.game.entities.sensors.DeathSensor;
import com.game.entities.sensors.WallSlideSensor;
import com.game.entities.specials.SpringyBouncer;
import com.game.graph.Graph;
import com.game.graph.GraphSystem;
import com.game.health.HealthComponent;
import com.game.levels.BitsBarUi;
import com.game.levels.LevelCameraManager;
import com.game.levels.LevelTiledMap;
import com.game.backgrounds.Background;
import com.game.backgrounds.WindyClouds;
import com.game.messages.Message;
import com.game.messages.MessageListener;
import com.game.movement.TrajectorySystem;
import com.game.pathfinding.PathfindingSystem;
import com.game.sounds.SoundSystem;
import com.game.spawns.Spawn;
import com.game.spawns.SpawnLocation;
import com.game.spawns.SpawnManager;
import com.game.updatables.UpdatableSystem;
import com.game.utils.UtilMethods;
import com.game.utils.enums.Direction;
import com.game.core.MegaTextHandle;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.WorldSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.controllers.ControllerButton.START;
import static com.game.core.constants.Events.*;
import static com.game.core.constants.GameScreen.PAUSE_MENU;
import static com.game.core.constants.LevelStatus.*;
import static com.game.core.constants.RenderingGround.PLAYGROUND;
import static com.game.core.constants.RenderingGround.UI;
import static com.game.core.constants.SoundAsset.MEGAMAN_DEFEAT_SOUND;
import static com.game.core.constants.TextureAsset.BITS;
import static com.game.core.constants.ViewVals.*;
import static com.game.backgrounds.WindyClouds.WINDY_CLOUDS;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
import static java.lang.Math.*;

public class LevelScreen extends ScreenAdapter implements MessageListener {

    private static final String TEST = "Test";
    private static final String SPECIAL = "Special";
    private static final String HAZARDS = "Hazards";
    private static final String GAME_ROOMS = "GameRooms";
    private static final String BACKGROUNDS = "Backgrounds";
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
    private final Timer deathTimer = new Timer(4f);

    private Megaman megaman;
    private Music levelMusic;
    private BitsBarUi healthBar;
    private MegaTextHandle fpsText;
    private LevelTiledMap levelMap;
    private SpawnManager spawnManager;
    private List<Background> backgrounds;
    private LevelCameraManager levelCameraManager;

    public LevelScreen(GameContext2d gameContext, String tmxFile, String musicSrc) {
        this.gameContext = gameContext;
        this.musicSrc = musicSrc;
        this.tmxFile = tmxFile;
    }

    @Override
    public void show() {
        DebugLogger.getInstance().info("Entities size at level screen show init: " + gameContext.getEntities().size());
        // init
        gameContext.addMessageListener(this);
        gameContext.setLevelStatus(UNPAUSED);
        gameContext.setDoUpdateController(true);
        gameContext.getSystems().forEach(system -> system.setOn(true));
        levelMusic = gameContext.getAsset(musicSrc, Music.class);
        gameContext.playMusic(levelMusic, true);
        deathTimer.setToEnd();
        fpsText = new MegaTextHandle(round(PPM / 2f), new Vector2(PPM, 14f * PPM));
        // level map
        levelMap = new LevelTiledMap((OrthographicCamera) gameContext.getViewport(PLAYGROUND).getCamera(),
                gameContext.getSpriteBatch(), tmxFile);
        // backgrounds
        backgrounds = new ArrayList<>();
        levelMap.getRectObjsOfLayer(BACKGROUNDS).forEach(backgroundObj -> {
            switch (backgroundObj.getName()) {
                case WINDY_CLOUDS -> backgrounds.add(new WindyClouds(gameContext, backgroundObj));
            }
        });
        // level graph
        Graph levelGraph = new Graph(new Vector2(PPM, PPM), levelMap.getWidthInTiles(), levelMap.getHeightInTiles());
        gameContext.getSystem(PathfindingSystem.class).setGraph(levelGraph);
        gameContext.getSystem(GraphSystem.class).setGraph(levelGraph);
        // spawns and spawn manager
        Rectangle startPlayerSpawn = new Rectangle();
        List<Rectangle> playerSpawns = levelMap.getRectObjsOfLayer(PLAYER_SPAWNS).stream().map(playerSpawnObj -> {
            Rectangle rect = playerSpawnObj.getRectangle();
            if (playerSpawnObj.getName().equals("start")) {
                startPlayerSpawn.set(rect);
            }
            return rect;
        }).toList();
        List<Spawn> enemySpawns = levelMap.getRectObjsOfLayer(ENEMY_SPAWNS).stream().map(enemySpawnObj ->
                new Spawn(gameContext, getEnemySpawnSupplier(enemySpawnObj), enemySpawnObj.getRectangle())).toList();
        spawnManager = new SpawnManager(gameContext.getViewport(PLAYGROUND).getCamera(), playerSpawns, enemySpawns);
        spawnManager.setCurrentPlayerSpawn(startPlayerSpawn);
        // static blocks
        levelMap.getRectObjsOfLayer(STATIC_BLOCKS).forEach(staticBlockObj ->
                gameContext.addEntity(new Block(gameContext, staticBlockObj.getRectangle(), new Vector2(.035f, 0f))));
        // moving blocks
        levelMap.getRectObjsOfLayer(MOVING_BLOCKS).forEach(blockObj ->
            gameContext.addEntity(new Block(gameContext, blockObj, new Vector2(.035f, 0f),
                    false, false, true, true, true)));
        // wall slide sensors
        levelMap.getRectObjsOfLayer(WALL_SLIDE_SENSORS).forEach(wallSlideSensorObj ->
                gameContext.addEntity(new WallSlideSensor(gameContext, wallSlideSensorObj.getRectangle())));
        // death sensors
        levelMap.getRectObjsOfLayer(DEATH_SENSORS).forEach(deathSensorObj ->
                gameContext.addEntity(new DeathSensor(gameContext, deathSensorObj.getRectangle())));
        // specials
        levelMap.getRectObjsOfLayer(SPECIAL).forEach(specialObj ->
            gameContext.addEntity(getSpecial(specialObj)));
        // hazards
        levelMap.getRectObjsOfLayer(HAZARDS).forEach(hazardObj ->
            gameContext.addEntity(getHazard(hazardObj)));
        // test
        levelMap.getRectObjsOfLayer(TEST).forEach(testObj -> {
            if (testObj.getName().equals("TimberWoman")) {
                Vector2 spawn = bottomCenterPoint(testObj.getRectangle());
                gameContext.addEntity(new TimberWoman(gameContext, spawn));
            }
        });
        // game rooms
        Map<Rectangle, String> gameRooms = new HashMap<>();
        levelMap.getRectObjsOfLayer(GAME_ROOMS).forEach(gameRoomObj ->
                gameRooms.put(gameRoomObj.getRectangle(), gameRoomObj.getName()));
        // level cam manager
        levelCameraManager = new LevelCameraManager(gameContext.getViewport(PLAYGROUND).getCamera(),
                new Timer(1f), gameRooms, megaman);
        gameContext.getSystem(CullOnCamTransSystem.class).setTransitionStateSupplier(
                () -> levelCameraManager.getTransitionState());
        // spawn Megaman
        spawnMegaman();
        // health bar ui
        TextureRegion healthBit = gameContext.getAsset(BITS.getSrc(), TextureAtlas.class).findRegion("HealthBit");
        healthBar = new BitsBarUi(gameContext, () -> megaman.getComponent(HealthComponent.class).getCurrentHealth(),
                () -> healthBit, new Vector2(PPM / 2f, PPM / 8f), new Rectangle(PPM, 9f * PPM, PPM / 2f, PPM * 3.75f));
        DebugLogger.getInstance().info("Entities size at level screen show end: " + gameContext.getEntities().size());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (gameContext.isJustPressed(START)) {
            boolean paused = gameContext.isLevelStatus(PAUSED);
            gameContext.setLevelStatus(paused ? UNPAUSED : PAUSED);
            if (paused) {
                gameContext.popOverlayScreen();
            } else {
                gameContext.putOverlayScreen(PAUSE_MENU);
            }
            if (levelCameraManager.getTransitionState() == null) {
                gameContext.getSystem(ControllerSystem.class).setOn(paused);
                gameContext.getSystem(TrajectorySystem.class).setOn(paused);
                gameContext.getSystem(UpdatableSystem.class).setOn(paused);
                gameContext.getSystem(BehaviorSystem.class).setOn(paused);
                gameContext.getSystem(WorldSystem.class).setOn(paused);
            }
            gameContext.addMessage(new Message(this, paused ? LEVEL_UNPAUSED : LEVEL_PAUSED));
        } else if (!gameContext.isLevelStatus(PAUSED)) {
            levelCameraManager.update(delta);
            if (levelCameraManager.getTransitionState() == null) {
                spawnManager.update();
            } else {
                switch (levelCameraManager.getTransitionState()) {
                    case BEGIN -> {
                        gameContext.getSystem(ControllerSystem.class).setOn(false);
                        gameContext.getSystem(TrajectorySystem.class).setOn(false);
                        gameContext.getSystem(UpdatableSystem.class).setOn(false);
                        gameContext.getSystem(BehaviorSystem.class).setOn(false);
                        gameContext.getSystem(WorldSystem.class).setOn(false);
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
                    }
                }
            }
            deathTimer.update(delta);
            if (deathTimer.isJustFinished()) {
                gameContext.playMusic(levelMusic, true);
                spawnMegaman();
            }
        }
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(PLAYGROUND);
        spriteBatch.begin();
        backgrounds.forEach(background -> {
            background.update(delta);
            background.draw(gameContext.getSpriteBatch());
        });
        levelMap.draw();
        healthBar.draw();
        showFpsText();
        spriteBatch.end();
        gameContext.updateSystems(delta);
    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (owner.equals(megaman) && message.equals(PLAYER_DEAD)) {
            gameContext.getSystem(SoundSystem.class).requestToStopAllLoopingSounds();
            gameContext.getAsset(MEGAMAN_DEFEAT_SOUND.getSrc(), Sound.class).play();
            deathTimer.reset();
            gameContext.stopMusic(levelMusic);
        }
    }

    @Override
    public void dispose() {
        gameContext.stopMusic(levelMusic);
        levelMap.dispose();
        gameContext.purgeAllEntities();
        gameContext.setLevelStatus(NONE);
        gameContext.removeMessageListener(this);
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

    private void showFpsText() {
        fpsText.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(gameContext.getViewport(UI).getCamera().combined);
        fpsText.draw(spriteBatch);
    }

    private Supplier<Entity> getEnemySpawnSupplier(RectangleMapObject spawnObj) {
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
                return () -> new SpawnLocation(gameContext, gameContext.getViewport(PLAYGROUND).getCamera(),
                        spawnObj.getRectangle(), 4, 3f, () -> new FloatingCan(gameContext, () -> megaman,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER)));
            }
            case "bat" -> {
                return () -> new Bat(gameContext, () -> megaman, getPoint(spawnObj.getRectangle(), TOP_CENTER));
            }
            case "dragonfly" -> {
                return () -> new Dragonfly(gameContext, () -> megaman, getPoint(spawnObj.getRectangle(), CENTER));
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

    private Entity getHazard(RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "saw" -> {
                return new Saw(gameContext, spawnObj);
            }
            case "laser_beamer" -> {
                return new LaserBeamer(gameContext, centerPoint(spawnObj.getRectangle()));
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

    private Entity getSpecial(RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "bounce" -> {
                return new SpringyBouncer(gameContext, spawnObj);
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

}
