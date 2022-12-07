package com.game.levels;

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
import com.game.backgrounds.BackgroundFactory;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerSystem;
import com.game.entities.interactive.Door;
import com.game.messages.Message;
import com.game.updatables.UpdatableComponent;
import com.game.utils.DebugLogger;
import com.game.GameContext2d;
import com.game.entities.special.AbstractBounds;
import com.game.entities.blocks.BlockFactory;
import com.game.entities.enemies.EnemyFactory;
import com.game.entities.hazards.HazardFactory;
import com.game.entities.special.SpecialFactory;
import com.game.entities.megaman.Megaman;
import com.game.entities.sensors.DeathSensor;
import com.game.graph.Graph;
import com.game.graph.GraphSystem;
import com.game.health.HealthComponent;
import com.game.backgrounds.Background;
import com.game.messages.MessageListener;
import com.game.movement.TrajectorySystem;
import com.game.pathfinding.PathfindingSystem;
import com.game.sounds.SoundSystem;
import com.game.spawns.Spawn;
import com.game.spawns.SpawnManager;
import com.game.updatables.UpdatableSystem;
import com.game.text.MegaTextHandle;
import com.game.utils.objects.Timer;
import com.game.world.*;

import java.util.ArrayList;
import java.util.List;

import static com.game.GlobalKeys.NEXT;
import static com.game.assets.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.controllers.ControllerButton.START;
import static com.game.messages.MessageType.*;
import static com.game.GameScreen.PAUSE_MENU;
import static com.game.levels.LevelStatus.*;
import static com.game.sprites.RenderingGround.PLAYGROUND;
import static com.game.sprites.RenderingGround.UI;
import static com.game.assets.SoundAsset.MEGAMAN_DEFEAT_SOUND;
import static com.game.assets.TextureAsset.BITS;
import static com.game.ViewVals.*;
import static com.game.levels.LevelLayers.*;

import static com.game.utils.UtilMethods.*;
import static com.game.utils.UtilMethods.bottomCenterPoint;
import static java.lang.Math.*;

public class LevelScreen extends ScreenAdapter implements MessageListener {

    public static final float LEVEL_CAM_TRANS_DURATION = 1f;
    public static final float MEGAMAN_DIST_FROM_EDGE_ON_GAME_ROOM_TRANS = 3f;

    private static final String BOSS_ROOM = "BossRoom";

    private final String tmxFile;
    private final String musicSrc;
    private final GameContext2d gameContext;
    private final Timer deathTimer = new Timer(4f);

    private Megaman megaman;
    private Music levelMusic;
    private BitsBarUi healthBar;
    private LevelTiledMap levelMap;
    private MegaTextHandle testText;
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
        testText = new MegaTextHandle(round(PPM / 2f), new Vector2(PPM, 14f * PPM));
        // level map
        levelMap = new LevelTiledMap((OrthographicCamera) gameContext.getViewport(PLAYGROUND).getCamera(),
                gameContext.getSpriteBatch(), tmxFile);
        // backgrounds
        backgrounds = new ArrayList<>();
        levelMap.getRectObjsOfLayer(BACKGROUNDS).forEach(backgroundObj ->
            BackgroundFactory.create(gameContext, backgrounds, backgroundObj));
        // set world graph and air resistance
        gameContext.getSystem(WorldSystem.class).setWorldGraph(levelMap);
        if (levelMap.hasMapProp("airResistance")) {
            String airResistStr = levelMap.getMapProp("airResistance", String.class);
            String[] airResistStrVals = airResistStr.split(",");
            float airResistX = Float.parseFloat(airResistStrVals[0]);
            float airResistY = Float.parseFloat(airResistStrVals[1]);
            gameContext.getSystem(WorldSystem.class).setAirResistance(new Vector2(airResistX, airResistY));
        }
        // set graph for graph and pathfinding systems
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
                new Spawn(gameContext, EnemyFactory.get(gameContext, enemySpawnObj, () -> megaman),
                        enemySpawnObj.getRectangle())).toList();
        spawnManager = new SpawnManager(gameContext.getViewport(PLAYGROUND).getCamera(), playerSpawns, enemySpawns);
        spawnManager.setCurrentPlayerSpawn(startPlayerSpawn);
        // abstract bounds
        levelMap.getRectObjsOfLayer(ABSTRACT_BOUNDS).forEach(abstractObj ->
                gameContext.addEntity(new AbstractBounds(gameContext, abstractObj.getRectangle())));
        // doors
        levelMap.getRectObjsOfLayer(DOORS).forEach(doorObj -> gameContext.addEntity(
                new Door(gameContext, doorObj, () -> megaman)));
        // blocks
        levelMap.getRectObjsOfLayer(BLOCKS).forEach(blockObj -> BlockFactory.create(gameContext, blockObj));
        // death sensors
        levelMap.getRectObjsOfLayer(DEATH_SENSORS).forEach(deathSensorObj ->
                gameContext.addEntity(new DeathSensor(gameContext, deathSensorObj.getRectangle())));
        // specials
        levelMap.getRectObjsOfLayer(SPECIAL).forEach(specialObj -> SpecialFactory.create(gameContext, specialObj));
        // hazards
        levelMap.getRectObjsOfLayer(HAZARDS).forEach(hazardObj -> HazardFactory.create(gameContext, hazardObj));
        // test objs
        levelMap.getRectObjsOfLayer(TEST).forEach(testObj -> {
            System.out.println(testObj.getName());
            System.out.println(testObj.getRectangle());
        });
        // level cam manager
        levelCameraManager = new LevelCameraManager(gameContext.getViewport(PLAYGROUND).getCamera(),
                new Timer(LEVEL_CAM_TRANS_DURATION), levelMap.getRectObjsOfLayer(GAME_ROOMS), megaman,
                MEGAMAN_DIST_FROM_EDGE_ON_GAME_ROOM_TRANS * PPM);
        // spawn Megaman
        spawnMegaman();
        // health bar ui
        TextureRegion healthBit = gameContext.getAsset(BITS.getSrc(), TextureAtlas.class).findRegion("StandardBit");
        healthBar = new BitsBarUi(gameContext, () -> megaman.getComponent(HealthComponent.class).getCurrentHealth(),
                () -> healthBit, new Vector2(PPM / 2f, PPM / 8f),
                new Rectangle(PPM * .4f, 9f * PPM, PPM / 2f, PPM * 3.75f));
        DebugLogger.getInstance().info("Entities size at level screen show end: " + gameContext.getEntities().size());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (gameContext.isControllerButtonJustPressed(START)) {
            boolean paused = gameContext.isLevelStatus(PAUSED);
            gameContext.setLevelStatus(paused ? UNPAUSED : PAUSED);
            if (paused) {
                gameContext.popOverlayScreen();
            } else {
                gameContext.putOverlayScreen(PAUSE_MENU);
            }
            if (levelCameraManager.getTransState() == null) {
                gameContext.getSystem(ControllerSystem.class).setOn(paused);
                gameContext.getSystem(TrajectorySystem.class).setOn(paused);
                gameContext.getSystem(UpdatableSystem.class).setOn(paused);
                gameContext.getSystem(BehaviorSystem.class).setOn(paused);
                gameContext.getSystem(WorldSystem.class).setOn(paused);
            }
            gameContext.sendMessage(new Message(paused ? LEVEL_UNPAUSED : LEVEL_PAUSED));
            Sound sound = gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class);
            gameContext.playSound(sound);
        } else if (!gameContext.isLevelStatus(PAUSED)) {
            levelCameraManager.update(delta);
            if (levelCameraManager.getTransState() == null) {
                spawnManager.update();
            } else {
                switch (levelCameraManager.getTransState()) {
                    case BEGIN -> {
                        gameContext.sendMessage(new Message(BEGIN_GAME_ROOM_TRANS));
                        gameContext.getSystem(ControllerSystem.class).setOn(false);
                        gameContext.getSystem(TrajectorySystem.class).setOn(false);
                        gameContext.getSystem(UpdatableSystem.class).setOn(false);
                        gameContext.getSystem(BehaviorSystem.class).setOn(false);
                        gameContext.getSystem(WorldSystem.class).setOn(false);
                        gameContext.getSystem(SoundSystem.class).requestToStopAllLoopingSounds();
                        BodyComponent bodyComponent = megaman.getComponent(BodyComponent.class);
                        bodyComponent.setVelocity(Vector2.Zero);
                        setBottomCenterToPoint(bodyComponent.getCollisionBox(),
                                levelCameraManager.getFocusableTransInterpolation());
                    }
                    case CONTINUE -> {
                        gameContext.sendMessage(new Message(CONTINUE_GAME_ROOM_TRANS));
                        gameContext.getSystem(ControllerSystem.class).setOn(false);
                        gameContext.getSystem(TrajectorySystem.class).setOn(false);
                        gameContext.getSystem(UpdatableSystem.class).setOn(false);
                        gameContext.getSystem(BehaviorSystem.class).setOn(false);
                        gameContext.getSystem(WorldSystem.class).setOn(false);
                        gameContext.getSystem(SoundSystem.class).requestToStopAllLoopingSounds();
                        BodyComponent bodyComponent = megaman.getComponent(BodyComponent.class);
                        bodyComponent.setVelocity(Vector2.Zero);
                        setBottomCenterToPoint(bodyComponent.getCollisionBox(),
                                levelCameraManager.getFocusableTransInterpolation());
                    }
                    case END -> {
                        gameContext.sendMessage(new Message(END_GAME_ROOM_TRANS));
                        gameContext.getSystem(ControllerSystem.class).setOn(true);
                        gameContext.getSystem(TrajectorySystem.class).setOn(true);
                        gameContext.getSystem(UpdatableSystem.class).setOn(true);
                        gameContext.getSystem(BehaviorSystem.class).setOn(true);
                        gameContext.getSystem(WorldSystem.class).setOn(true);
                        megaman.getComponent(UpdatableComponent.class).setOn(true);
                        RectangleMapObject currentGameRoom = levelCameraManager.getCurrentGameRoom();
                        if (currentGameRoom != null && currentGameRoom.getName() != null &&
                                currentGameRoom.getName().equals(BOSS_ROOM)) {
                            gameContext.sendMessage(new Message(ENTER_BOSS_ROOM));
                        }
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
            background.draw(spriteBatch);
        });
        levelMap.draw();
        showTestText();
        spriteBatch.end();
        gameContext.updateSystems(delta);
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        healthBar.draw();
        spriteBatch.end();
    }

    @Override
    public void listenToMessage(Message message) {
        switch (message.getMessageType()) {
            case PLAYER_DEAD -> {
                gameContext.getSystem(SoundSystem.class).requestToStopAllLoopingSounds();
                gameContext.getAsset(MEGAMAN_DEFEAT_SOUND.getSrc(), Sound.class).play();
                gameContext.stopMusic(levelMusic);
                deathTimer.reset();
            }
            case GATE_INIT_OPENING -> {
                gameContext.getSystem(ControllerSystem.class).setOn(false);
                gameContext.getSystem(TrajectorySystem.class).setOn(false);
                gameContext.getSystem(BehaviorSystem.class).setOn(false);
                gameContext.getSystem(WorldSystem.class).setOn(false);
                megaman.getComponent(UpdatableComponent.class).setOn(false);
                megaman.getComponent(BodyComponent.class).setVelocity(Vector2.Zero);
                gameContext.getSystem(SoundSystem.class).requestToStopAllLoopingSounds();
            }
            case NEXT_GAME_ROOM_REQUEST -> {
                String nextGameRoom = message.getContent(NEXT, String.class);
                levelCameraManager.transToGameRoomWithName(nextGameRoom);
            }
            case ENTER_BOSS_ROOM -> {
                System.out.println("Enter boss room");
            }
        }
    }

    @Override
    public void dispose() {
        levelMap.dispose();
        gameContext.purgeAllEntities();
        gameContext.setLevelStatus(NONE);
        gameContext.stopMusic(levelMusic);
        gameContext.removeMessageListener(this);
    }

    private void spawnMegaman() {
        Vector2 spawnPos = bottomCenterPoint(spawnManager.getCurrentPlayerSpawn());
        megaman = new Megaman(gameContext, spawnPos);
        levelCameraManager.setFocusable(megaman);
        gameContext.addEntity(megaman);
        gameContext.sendMessage(new Message(PLAYER_SPAWN));
    }

    private void showTestText() {
        RectangleMapObject currentGameRoom = levelCameraManager.getCurrentGameRoom();
        String currentGameRoomName = currentGameRoom != null ? currentGameRoom.getName() : "NULL";
        testText.setText("Current game room: " + currentGameRoomName);
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(gameContext.getViewport(UI).getCamera().combined);
        testText.draw(spriteBatch);
    }

}
