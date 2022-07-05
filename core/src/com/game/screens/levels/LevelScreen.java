package com.game.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.GameState;
import com.game.MessageListener;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerSystem;
import com.game.entities.blocks.Block;
import com.game.entities.megaman.Megaman;
import com.game.entities.sensors.DeathSensor;
import com.game.entities.sensors.WallSlideSensor;
import com.game.trajectories.TrajectoryComponent;
import com.game.updatables.UpdatableSystem;
import com.game.utils.Direction;
import com.game.utils.Timer;
import com.game.utils.UtilMethods;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import com.game.world.WorldSystem;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.RenderingGround.PLAYGROUND;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.*;

// TODO: Dispose of assets in level screen when leaving level

public class LevelScreen extends ScreenAdapter implements MessageListener {

    public static final String BLOCKS = "Blocks";
    public static final String GAME_ROOMS = "GameRooms";
    public static final String ENEMY_SPAWNS = "EnemySpawns";
    public static final String PLAYER_SPAWNS = "PlayerSpawns";
    public static final String DEATH_SENSORS = "DeathSensors";
    public static final String WALL_SLIDE_SENSORS = "WallSlideSensors";

    public static final float LEVEL_CAM_TRANS_DURATION = 1f;
    public static final float MEGAMAN_DELTA_ON_CAM_TRANS = 3f;

    private final Map<Integer, Rectangle> playerSpawns = new HashMap<>();
    private final Sprite blackBoxSprite = new Sprite();
    private final Timer deathTimer = new Timer(4f);
    private final Timer fadeTimer = new Timer();
    private final GameContext2d gameContext;
    private final String tmxFile;
    private final Music music;

    private Megaman megaman;
    private boolean fadingIn;
    private boolean fadingOut;
    private int currentPlayerSpawn = 0;
    private LevelTiledMap levelTiledMap;
    private LevelCameraManager levelCameraManager;

    public LevelScreen(GameContext2d gameContext, String tmxFile, String music) {
        this.gameContext = gameContext;
        this.tmxFile = tmxFile;
        this.music = gameContext.getAsset(music, Music.class);
    }

    @Override
    public void show() {
        music.play();
        deathTimer.setToEnd();
        levelTiledMap = new LevelTiledMap((OrthographicCamera) gameContext.getViewport(PLAYGROUND).getCamera(),
                gameContext.getSpriteBatch(), tmxFile);
        // define player spawns
        levelTiledMap.getObjectsOfLayer(PLAYER_SPAWNS).forEach(playerSpawnObj -> {
           Integer key = playerSpawnObj.getProperties().get("key", Integer.class);
           playerSpawns.put(key, playerSpawnObj.getRectangle());
        });
        // define blocks
        levelTiledMap.getObjectsOfLayer(BLOCKS).forEach(blockObj -> {
            Boolean wallSlideLeft = blockObj.getProperties().get("wallSlideLeft", Boolean.class);
            Boolean wallSlideRight = blockObj.getProperties().get("wallSlideRight", Boolean.class);
            Boolean affectedByResistance = blockObj.getProperties().get("abr", Boolean.class);
            Boolean gravityOn = blockObj.getProperties().get("gravityOn", Boolean.class);
            Float frictionX = blockObj.getProperties().get("frictionX", Float.class);
            Float frictionY = blockObj.getProperties().get("frictionY", Float.class);
            Block block = new Block(blockObj.getRectangle(),
                    wallSlideLeft != null && wallSlideLeft, wallSlideRight != null && wallSlideRight,
                    affectedByResistance != null && affectedByResistance, gravityOn != null && gravityOn,
                    frictionX != null ? frictionX : .035f, frictionY != null ? frictionY : 0f);
            gameContext.addEntity(block);
            if (blockObj.getProperties().containsKey("trajectory")) {
                TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
                String[] trajectories = blockObj.getProperties().get("trajectory", String.class).split(";");
                for (String trajectory : trajectories) {
                    String[] params = trajectory.split(",");
                    float x = Float.parseFloat(params[0]);
                    float y = Float.parseFloat(params[1]);
                    float time = Float.parseFloat(params[2]);
                    trajectoryComponent.addTrajectory(new Vector2(x * PPM, y * PPM), time);
                }
                block.addComponent(trajectoryComponent);
                BodyComponent bodyComponent = block.getComponent(BodyComponent.class);
                Fixture leftWallSlide = new Fixture(block, WALL_SLIDE_SENSOR);
                leftWallSlide.setSize(PPM / 2f, bodyComponent.getCollisionBox().height - PPM / 3f);
                leftWallSlide.setOffset(-bodyComponent.getCollisionBox().width / 2f, 0f);
                bodyComponent.addFixture(leftWallSlide);
                Fixture rightWallSlide = new Fixture(block, WALL_SLIDE_SENSOR);
                rightWallSlide.setSize(PPM / 2f, bodyComponent.getCollisionBox().height - PPM / 3f);
                rightWallSlide.setOffset(bodyComponent.getCollisionBox().width / 2f, 0f);
                bodyComponent.addFixture(rightWallSlide);
                Fixture feetSticker = new Fixture(block, FEET_STICKER);
                feetSticker.setSize(bodyComponent.getCollisionBox().width, PPM / 3f);
                feetSticker.setOffset(0f, (bodyComponent.getCollisionBox().height / 2f) - 2f);
                bodyComponent.addFixture(feetSticker);
            }
        });
        // define wall slide sensors
        levelTiledMap.getObjectsOfLayer(WALL_SLIDE_SENSORS).forEach(wallSlideSensorObj ->
                gameContext.addEntity(new WallSlideSensor(wallSlideSensorObj.getRectangle())));
        // define death sensors
        levelTiledMap.getObjectsOfLayer(DEATH_SENSORS).forEach(deathSensorObj ->
                gameContext.addEntity(new DeathSensor(deathSensorObj.getRectangle())));
        // define game rooms
        Map<Rectangle, String> gameRooms = new HashMap<>();
        levelTiledMap.getObjectsOfLayer(GAME_ROOMS).forEach(gameRoomObj ->
                gameRooms.put(gameRoomObj.getRectangle(), gameRoomObj.getName()));
        levelCameraManager = new LevelCameraManager(gameContext.getViewport(PLAYGROUND).getCamera(),
                new Timer(1f), gameRooms, megaman);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (gameContext.getGameState() == GameState.RUNNING) {
           onGameRunning(delta);
        }
    }

    private void onGameRunning(float delta) {
        levelTiledMap.draw();
        levelCameraManager.update(delta);
        gameContext.updateSystems(delta);
        gameContext.getEntities().forEach(entity -> {
            if (entity instanceof CullOnOutOfGameCamBounds cull &&
                    !gameContext.getViewport(PLAYGROUND).getCamera().frustum.boundsInFrustum(
                            UtilMethods.rectToBBox(cull.getCullBoundingBox()))) {
                entity.setDead(true);
            }
        });
        deathTimer.update(delta);
        if (deathTimer.isJustFinished()) {
            music.play();
            Vector2 spawnPos = new Vector2();
            playerSpawns.get(currentPlayerSpawn).getPosition(spawnPos);
            megaman = new Megaman(gameContext, spawnPos);
            levelCameraManager.setFocusable(megaman);
            gameContext.addEntity(megaman);
        }
        if (levelCameraManager.getTransitionState() != null) {
            BodyComponent bodyComponent = megaman.getComponent(BodyComponent.class);
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> {
                    bodyComponent.getVelocity().setZero();
                    gameContext.getSystem(ControllerSystem.class).setOn(false);
                    gameContext.getSystem(BehaviorSystem.class).setOn(false);
                    gameContext.getSystem(WorldSystem.class).setOn(false);
                    gameContext.getSystem(UpdatableSystem.class).setOn(false);
                    gameContext.getEntities().forEach(entity -> {
                        if (entity instanceof CullOnLevelCamTrans) {
                            entity.setDead(true);
                        }
                    });
                }
                case CONTINUE -> {
                    gameContext.getEntities().forEach(entity -> {
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
                    gameContext.getSystem(ControllerSystem.class).setOn(true);
                    gameContext.getSystem(BehaviorSystem.class).setOn(true);
                    gameContext.getSystem(WorldSystem.class).setOn(true);
                    gameContext.getSystem(UpdatableSystem.class).setOn(true);
                }
            }
        }
    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {

    }

    @Override
    public void dispose() {
        super.dispose();
        levelTiledMap.dispose();
        gameContext.purgeAllEntities();
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
