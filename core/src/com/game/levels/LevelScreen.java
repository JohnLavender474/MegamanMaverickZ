package com.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.ConstVals;
import com.game.GameContext2d;
import com.game.MessageListener;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerSystem;
import com.game.entities.blocks.Block;
import com.game.entities.megaman.Megaman;
import com.game.entities.sensors.DeathSensor;
import com.game.entities.sensors.WallSlideSensor;
import com.game.health.HealthComponent;
import com.game.spawns.EntitySpawn;
import com.game.spawns.EntitySpawnFunctionFactory;
import com.game.spawns.EntitySpawnManager;
import com.game.trajectories.TrajectoryComponent;
import com.game.updatables.UpdatableSystem;
import com.game.utils.enums.Direction;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import com.game.world.WorldSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.ConstVals.RenderingGround.PLAYGROUND;
import static com.game.ConstVals.TextureAssets.BITS_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.FEET_STICKER;
import static com.game.world.FixtureType.WALL_SLIDE_SENSOR;

public class LevelScreen extends ScreenAdapter implements MessageListener {

    public static final String BLOCKS = "Blocks";
    public static final String GAME_ROOMS = "GameRooms";
    public static final String ENEMY_SPAWNS = "EnemySpawns";
    public static final String PLAYER_SPAWNS = "PlayerSpawns";
    public static final String DEATH_SENSORS = "DeathSensors";
    public static final String WALL_SLIDE_SENSORS = "WallSlideSensors";

    public static final float LEVEL_CAM_TRANS_DURATION = 1f;
    public static final float MEGAMAN_DELTA_ON_CAM_TRANS = 3f;

    private final GameContext2d gameContext;
    private final String tmxFile;
    private final String musicSrc;
    private final Sprite blackBoxSprite = new Sprite();
    private final Timer deathTimer = new Timer(4f);
    private final Timer fadeTimer = new Timer();

    private Megaman megaman;

    private Music music;
    private BitsBarUi healthBar;
    private LevelTiledMap levelTiledMap;
    private EntitySpawnManager entitySpawnManager;
    private LevelCameraManager levelCameraManager;

    private boolean isPaused;
    private boolean fadingIn;
    private boolean fadingOut;

    public LevelScreen(GameContext2d gameContext, String tmxFile, String musicSrc) {
        this.gameContext = gameContext;
        this.tmxFile = tmxFile;
        this.musicSrc = musicSrc;
    }

    @Override
    public void show() {
        gameContext.getSystems().forEach(system -> system.setOn(true));
        music = gameContext.getAsset(musicSrc, Music.class);
        music.play();
        deathTimer.setToEnd();
        // define level tiled map
        levelTiledMap = new LevelTiledMap(tmxFile);
        // define entity spawns manager
        List<Rectangle> playerSpawns = new ArrayList<>();
        levelTiledMap.getObjectsOfLayer(PLAYER_SPAWNS).stream().filter(playerSpawnObj -> {
            playerSpawns.add(playerSpawnObj.getRectangle());
            return playerSpawnObj.getName().equals("start");
        }).findFirst().map(RectangleMapObject::getRectangle).ifPresentOrElse(
                entitySpawnManager::setCurrentPlayerSpawn, () -> {throw new IllegalStateException();});
        EntitySpawnFunctionFactory factory = new EntitySpawnFunctionFactory();
        List<EntitySpawn> enemySpawns = levelTiledMap.getObjectsOfLayer(ENEMY_SPAWNS).stream().map(enemySpawnObj ->
                new EntitySpawn(gameContext, enemySpawnObj, factory.getFunction(enemySpawnObj.getName()))).toList();
        entitySpawnManager = new EntitySpawnManager(gameContext.getViewport(PLAYGROUND).getCamera(),
                playerSpawns, enemySpawns);
        // define blocks
        levelTiledMap.getObjectsOfLayer(BLOCKS).forEach(blockObj -> {
            Boolean wallSlideLeft = blockObj.getProperties().get("wallSlideLeft", Boolean.class);
            Boolean wallSlideRight = blockObj.getProperties().get("wallSlideRight", Boolean.class);
            Boolean affectedByResistance = blockObj.getProperties().get("abr", Boolean.class);
            Boolean gravityOn = blockObj.getProperties().get("gravityOn", Boolean.class);
            Boolean feetSticky = blockObj.getProperties().get("feetSticky", Boolean.class);
            Float frictionX = blockObj.getProperties().get("frictionX", Float.class);
            Float frictionY = blockObj.getProperties().get("frictionY", Float.class);
            Block block = new Block(blockObj.getRectangle(),
                    new Vector2(frictionX != null ? frictionX : .035f, frictionY != null ? frictionY : 0f),
                    affectedByResistance != null && affectedByResistance, gravityOn != null && gravityOn,
                    wallSlideLeft != null && wallSlideLeft, wallSlideRight != null && wallSlideRight,
                    feetSticky != null && feetSticky);
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
        // spawn Megaman
        spawnMegaman();
        // health bar ui
        TextureRegion healthBit = gameContext.getAsset(BITS_ATLAS, TextureAtlas.class).findRegion("HealthBit");
        healthBar = new BitsBarUi(gameContext, () -> megaman.getComponent(HealthComponent.class).getCurrentHealth(),
                () -> healthBit, new Vector2(8f, 2f), new Rectangle(0f, 0f, 8f, 60f));
    }

    private void spawnMegaman() {
        Vector2 spawnPos = new Vector2();
        entitySpawnManager.getCurrentPlayerSpawn().getPosition(spawnPos);
        megaman = new Megaman(gameContext, spawnPos);
        levelCameraManager.setFocusable(megaman);
        gameContext.addEntity(megaman);
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

    private void onGameRunning(float delta) {
        levelTiledMap.draw((OrthographicCamera) gameContext.getViewport(PLAYGROUND).getCamera(),
                gameContext.getSpriteBatch());
        levelCameraManager.update(delta);
        gameContext.updateSystems(delta);
        healthBar.draw();
        if (!deathTimer.isFinished()) {
            deathTimer.update(delta);
        }
        if (deathTimer.isJustFinished()) {
            music.play();
            spawnMegaman();
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
                    gameContext.getSystem(ControllerSystem.class).setOn(true);
                    gameContext.getSystem(BehaviorSystem.class).setOn(true);
                    gameContext.getSystem(WorldSystem.class).setOn(true);
                    gameContext.getSystem(UpdatableSystem.class).setOn(true);
                }
            }
        }
    }

    private void onGamePaused(float delta) {

    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (owner.equals(megaman) && message.equals(ConstVals.Events.PLAYER_DEAD)) {
            gameContext.getAsset(ConstVals.SoundAssets.MEGAMAN_DEFEAT_SOUND, Sound.class).play();
            music.stop();
        }
    }

    @Override
    public void dispose() {
        music.dispose();
        music = null;
        levelTiledMap.dispose();
        levelTiledMap = null;
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
