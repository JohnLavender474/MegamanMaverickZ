package com.game.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.MegamanVals;
import com.game.ConstVals.RenderingGround;
import com.game.ConstVals.TextureAssets;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.blocks.StaticBlockFactory;
import com.game.megaman.Megaman;
import com.game.megaman.MegamanStats;
import com.game.utils.Direction;
import com.game.utils.FontHandle;
import com.game.utils.Timer;
import com.game.utils.UtilMethods;
import com.game.world.BodyComponent;
import com.game.world.WorldSystem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.game.ConstVals.LevelTiledMapLayer.*;
import static com.game.ConstVals.RenderingGround.PLAYGROUND;
import static com.game.ConstVals.ViewVals.*;

// TODO: Dispose of assets in level screen when leaving level

public class LevelScreen extends ScreenAdapter {

    // expressed in seconds
    private static final float DEATH_DELAY_DURATION = 3f;
    private static final float PLAYER_ENTRANCE_DURATION = 1f;
    // expressed in world units
    private static final float LEVEL_CAM_TRANS_DURATION = 1f;
    private static final float MEGAMAN_DELTA_ON_CAM_TRANS = 3f;

    private final FontHandle readyFont = new FontHandle("Megaman10Font.ttf", 8);
    private final Timer playerEntrance = new Timer(PLAYER_ENTRANCE_DURATION);
    private final Timer deathDelay = new Timer(DEATH_DELAY_DURATION);
    private final Sprite blackBoxSprite = new Sprite();
    private final Timer fadeTimer = new Timer();
    private final GameContext2d gameContext;
    private final String tmxFile;

    private Music music;
    private Megaman megaman;
    private boolean fadingIn;
    private boolean fadingOut;
    private boolean readyVisible;
    private HealthBarUi healthBarUi;
    private LevelTiledMap levelTiledMap;
    private LevelCameraManager levelCameraManager;

    public LevelScreen(GameContext2d gameContext, String tmxFile) {
        this.gameContext = gameContext;
        this.tmxFile = tmxFile;
    }

    @Override
    public void show() {
        // Level tiled map
        levelTiledMap = new LevelTiledMap(
                (OrthographicCamera) gameContext.getViewport(
                        PLAYGROUND).getCamera(), tmxFile);
        // Megaman
        Map<String, Rectangle> megamanSpawns = levelTiledMap.getObjectsOfLayer(PLAYER_SPAWNS.getLayerName())
                .stream().collect(Collectors.toMap(
                        MapObject::getName, RectangleMapObject::getRectangle));
        MegamanStats megamanStats = gameContext.getBlackboardObject(
                MegamanVals.MEGAMAN_STATS, MegamanStats.class);
        megaman = new Megaman(gameContext, megamanSpawns, megamanStats);
        megaman.setCurrentSpawn("Start");
        // ------------------------------------------------------------------------------
        // TODO: Replace with proper entrance
        Vector2 testSpawnPos = new Vector2();
        megamanSpawns.get("Start").getPosition(testSpawnPos);
        megaman.getComponent(BodyComponent.class).getCollisionBox().setPosition(testSpawnPos);
        // ------------------------------------------------------------------------------
        gameContext.addEntity(megaman);
        // Static blocks
        List<RectangleMapObject> blockRectangleMapObjs = levelTiledMap
                .getObjectsOfLayer(STATIC_BLOCKS.getLayerName());
        StaticBlockFactory staticBlockFactory = new StaticBlockFactory();
        staticBlockFactory.create(blockRectangleMapObjs).forEach(gameContext::addEntity);
        // Get game rooms layer
        Map<Rectangle, String> gameRooms = levelTiledMap.getObjectsOfLayer(GAME_ROOMS.getLayerName())
                .stream().collect(Collectors.toMap(
                        RectangleMapObject::getRectangle, MapObject::getName));
        // Load level camera manager
        levelCameraManager = new LevelCameraManager(
                gameContext.getViewport(PLAYGROUND).getCamera(),
                new Timer(LEVEL_CAM_TRANS_DURATION),
                gameRooms, megaman);
        /*
        // Load health bar UI
        TextureRegion containerRegion = gameContext
                .loadAsset(TextureAssets.DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("Black");
        Rectangle containerBounds = new Rectangle(PPM, 9.875f * PPM, 0.5f * PPM, 2.625f * PPM);
        TextureRegion healthBitRegion = gameContext
                .loadAsset(TextureAssets.HEALTH_WEAPON_STATS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("HealthbarFullBit");
        healthBarUi = new HealthBarUi(uiViewport.getCamera(),
                                      containerRegion, containerBounds,
                                      healthBitRegion, MegamanVals.MAX_HEALTH_BITS);
        TextureRegion blackBoxRegion = gameContext
                .loadAsset(TextureAssets.DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("Black");
        // Load black box sprite for fading in/out
        blackBoxSprite.setRegion(blackBoxRegion);
         */
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        gameContext.viewOfEntities().forEach(entity -> {
            if (entity instanceof CullOnOutOfGameCamBounds cullable) {
                BoundingBox cullBBox = UtilMethods.rectToBBox(cullable.getBoundingBox());
                if (!gameContext.getViewport(PLAYGROUND).getCamera()
                        .frustum.boundsInFrustum(cullBBox)) {
                    cullable.getCullTimer().update(delta);
                    if (cullable.getCullTimer().isFinished()) {
                        entity.setMarkedForRemoval(true);
                    }
                } else {
                    cullable.getCullTimer().reset();
                }
            }
        });
        levelCameraManager.update(delta);
        if (levelCameraManager.getTransitionState() != null) {
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> {
                    gameContext.getSystem(WorldSystem.class).setOn(false);
                    // TODO: turn off other relevant systems
                    gameContext.viewOfEntities().forEach(entity -> {
                        if (entity instanceof CullOnLevelCamTrans) {
                            entity.setMarkedForRemoval(true);
                        }
                    });
                }
                case CONTINUE -> {
                    BodyComponent bodyComponent = megaman.getComponent(BodyComponent.class);
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
                    gameContext.getSystem(WorldSystem.class).setOn(true);
                    // TODO: turn on other relevant systems
                }
            }
        }
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

    private void initFadeIn()
            throws IllegalStateException {
        blackBoxSprite.setBounds(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fadeTimer.reset();
    }

    private void initFadeOut()
            throws IllegalStateException {
        blackBoxSprite.setBounds(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackBoxSprite.setAlpha(0f);
        fadeTimer.reset();
    }

}
