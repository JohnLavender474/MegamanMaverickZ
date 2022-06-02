package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.animations.AnimationSystem;
import com.mygdx.game.controllers.ControllerManager;
import com.mygdx.game.sprites.RenderingGround;
import com.mygdx.game.sprites.SpriteHandle;
import com.mygdx.game.sprites.SpriteSystem;
import com.mygdx.game.levels.LevelTiledMapManager;
import com.mygdx.game.world.WorldSystem;
import lombok.Getter;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The entry point into the game. Initializes all assets and classes that need to be initialized before gameplay
 * is possible. The render method is responsible only for clearing textures from the screen and calling
 * {@link com.badlogic.gdx.Screen#render(float)} on {@link #getScreen()} every frame. This class implements
 * {@link GameContext} so that all updatable objects and other necessary components are available to the game context.
 * Any other update logic must be performed by the {@link com.badlogic.gdx.Screen} being updated, including the update
 * logic needed for the various objects returned from the {@link GameContext} methods.
 */
@Getter
public class MegamanMaverick extends Game implements GameContext {

    public static final String OBJECTS_TEXTURE_ATLAS = "sprites/SpriteSheets/Objects.txt";
    public static final String MET_TEXTURE_ATLAS = "sprites/SpriteSheets/Met.txt";
    public static final String ENEMIES_TEXTURE_ATLAS = "sprites/SpriteSheets/Enemies.txt";
    public static final String ITEMS_TEXTURE_ATLAS = "sprites/SpriteSheets/Items.txt";
    public static final String BACKGROUNDS_1_TEXTURE_ATLAS = "sprites/SpriteSheets/Backgrounds1.txt";
    public static final String MEGAMAN_TEXTURE_ATLAS = "sprites/SpriteSheets/Megaman.txt";
    public static final String CHARGE_ORBS_TEXTURE_ATLAS = "sprites/SpriteSheets/ChargeOrbs.txt";
    public static final String MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS = "sprites/SpriteSheets/MegamanChargedShot.txt";
    public static final String ELECTRIC_BALL_TEXTURE_ATLAS = "sprites/SpriteSheets/ElectricBall.txt";
    public static final String DECORATIONS_TEXTURE_ATLAS = "sprites/SpriteSheets/Decorations.txt";
    public static final String HEALTH_WEAPON_STATS_TEXTURE_ATLAS = "sprites/SpriteSheets/HealthWeaponStats.txt";

    public static final String MMX3_INTRO_STAGE_MUSIC = "music/MMX3_IntroStage.ogg";
    public static final String XENOBLADE_GAUR_PLAINS_MUSIC = "music/Xenoblade_GaurPlains.ogg";
    public static final String MMZ_NEO_ARCADIA_MUSIC = "music/MMZ_NeoArcadia.mp3";
    public static final String MMX_LEVEL_SELECT_SCREEN_MUSIC = "music/MMX_LevelSelectScreen.ogg";
    public static final String STAGE_SELECT_MM3_MUSIC = "music/StageSelectMM3.mp3";

    public static final String CURSOR_MOVE_BLOOP_SOUND = "sounds/CursorMoveBloop.mp3";
    public static final String MARIO_JUMP_SOUND = "sounds/MarioJump.mp3";
    public static final String SELECT_PING_SOUND = "sounds/SelectPing.mp3";
    public static final String DINK_SOUND = "sounds/Dink.mp3";
    public static final String EXPLOSION_SOUND = "sounds/Explosion.mp3";
    public static final String ENEMY_DAMAGE_SOUND = "sounds/EnemyDamage.mp3";
    public static final String ENEMY_BULLET_SOUND = "sounds/EnemyShoot.mp3";
    public static final String MEGA_BUSTER_BULLET_SHOT_SOUND = "sounds/MegaBusterBulletShot.mp3";
    public static final String MEGA_BUSTER_CHARGING_SOUND = "sounds/MegaBusterCharging.mp3";
    public static final String MEGA_BUSTER_CHARGED_SHOT_SOUND = "sounds/MegaBusterChargedShot.mp3";
    public static final String MEGAMAN_DAMAGE_SOUND = "sounds/MegamanDamage.mp3";
    public static final String MEGAMAN_DEFEAT_SOUND = "sounds/MegamanDefeat.mp3";
    public static final String MEGAMAN_LAND_SOUND = "sounds/MegamanLand.mp3";
    public static final String WHOOSH_SOUND = "sounds/Whoosh.mp3";
    public static final String THUMP_SOUND = "sounds/Thump.mp3";
    public static final String ENERGY_FILL_SOUND = "sounds/EnergyFill.mp3";
    public static final String PAUSE_SOUND = "sounds/PauseMenu.mp3";

    public static float PPM = 16f;
    public static float P2M = 1f / PPM;
    public static float VIEW_WIDTH = 16f;
    public static float VIEW_HEIGHT = 14f;

    private Map<RenderingGround, Queue<SpriteHandle>> renderables;
    private LevelTiledMapManager levelTiledMapManager;
    private Map<RenderingGround, Viewport> viewports;
    private ControllerManager controllerManager;
    private SystemsManager systemsManager;
    private Map<String, Screen> screens;
    private AssetManager assetManager;
    private SpriteBatch spriteBatch;

    @Override
    public void create() {
        for (RenderingGround renderingGround : RenderingGround.values()) {
            renderables.put(renderingGround, new PriorityQueue<>());
            viewports.put(renderingGround, new FitViewport(VIEW_WIDTH, VIEW_HEIGHT));
        }
        levelTiledMapManager = new LevelTiledMapManager();
        controllerManager = new ControllerManager();
        systemsManager = new SystemsManager();
        assetManager = new AssetManager();
        spriteBatch = new SpriteBatch();
        systemsManager.addSystem(new SpriteSystem(this));
        systemsManager.addSystem(new AnimationSystem(this));
        loadAssets(Music.class,
                   MMX3_INTRO_STAGE_MUSIC,
                   MMZ_NEO_ARCADIA_MUSIC,
                   XENOBLADE_GAUR_PLAINS_MUSIC,
                   MMX_LEVEL_SELECT_SCREEN_MUSIC,
                   STAGE_SELECT_MM3_MUSIC);
        loadAssets(Sound.class,
                   SELECT_PING_SOUND,
                   MARIO_JUMP_SOUND,
                   CURSOR_MOVE_BLOOP_SOUND,
                   DINK_SOUND,
                   ENEMY_BULLET_SOUND,
                   ENEMY_DAMAGE_SOUND,
                   MEGA_BUSTER_BULLET_SHOT_SOUND,
                   MEGA_BUSTER_CHARGED_SHOT_SOUND,
                   ENERGY_FILL_SOUND,
                   MEGA_BUSTER_CHARGING_SOUND,
                   MEGAMAN_DAMAGE_SOUND,
                   MEGAMAN_LAND_SOUND,
                   MEGAMAN_DEFEAT_SOUND,
                   WHOOSH_SOUND,
                   THUMP_SOUND,
                   EXPLOSION_SOUND,
                   PAUSE_SOUND);
        loadAssets(TextureAtlas.class,
                   CHARGE_ORBS_TEXTURE_ATLAS,
                   OBJECTS_TEXTURE_ATLAS,
                   MET_TEXTURE_ATLAS,
                   ENEMIES_TEXTURE_ATLAS,
                   ITEMS_TEXTURE_ATLAS,
                   BACKGROUNDS_1_TEXTURE_ATLAS,
                   MEGAMAN_TEXTURE_ATLAS,
                   MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS,
                   ELECTRIC_BALL_TEXTURE_ATLAS,
                   DECORATIONS_TEXTURE_ATLAS,
                   HEALTH_WEAPON_STATS_TEXTURE_ATLAS);
    }

    private <S> void loadAssets(Class<S> sClass, String... sources) {
        for (String source : sources) {
            assetManager.load(source, sClass);
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        assetManager.dispose();
        levelTiledMapManager.dispose();
    }

}
