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
import com.mygdx.game.controllers.ControllerManager;
import com.mygdx.game.megaman.MegamanAbility;
import com.mygdx.game.megaman.MegamanWeapon;
import com.mygdx.game.sprites.RenderingGround;
import com.mygdx.game.sprites.SpriteHandle;
import com.mygdx.game.sprites.SpriteSystem;
import com.mygdx.game.levels.LevelTiledMapManager;
import com.mygdx.game.utils.Percentage;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * The entry point into the game. Initializes all assets and classes that need to be initialized before gameplay
 * is possible. The render method is responsible only for clearing textures from the screen and calling
 * {@link com.badlogic.gdx.Screen#render(float)} on {@link #getScreen()} every frame.
 */
@Getter
public class MegamanMaverick extends Game {

    /**
     * The constant OBJECTS_TEXTURE_ATLAS.
     */
    public static final String OBJECTS_TEXTURE_ATLAS = "sprites/SpriteSheets/Objects.txt";
    /**
     * The constant MET_TEXTURE_ATLAS.
     */
    public static final String MET_TEXTURE_ATLAS = "sprites/SpriteSheets/Met.txt";
    /**
     * The constant ENEMIES_TEXTURE_ATLAS.
     */
    public static final String ENEMIES_TEXTURE_ATLAS = "sprites/SpriteSheets/Enemies.txt";
    /**
     * The constant ITEMS_TEXTURE_ATLAS.
     */
    public static final String ITEMS_TEXTURE_ATLAS = "sprites/SpriteSheets/Items.txt";
    /**
     * The constant BACKGROUNDS_1_TEXTURE_ATLAS.
     */
    public static final String BACKGROUNDS_1_TEXTURE_ATLAS = "sprites/SpriteSheets/Backgrounds1.txt";
    /**
     * The constant MEGAMAN_TEXTURE_ATLAS.
     */
    public static final String MEGAMAN_TEXTURE_ATLAS = "sprites/SpriteSheets/Megaman.txt";
    /**
     * The constant CHARGE_ORBS_TEXTURE_ATLAS.
     */
    public static final String CHARGE_ORBS_TEXTURE_ATLAS = "sprites/SpriteSheets/ChargeOrbs.txt";
    /**
     * The constant MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS.
     */
    public static final String MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS = "sprites/SpriteSheets/MegamanChargedShot.txt";
    /**
     * The constant ELECTRIC_BALL_TEXTURE_ATLAS.
     */
    public static final String ELECTRIC_BALL_TEXTURE_ATLAS = "sprites/SpriteSheets/ElectricBall.txt";
    /**
     * The constant DECORATIONS_TEXTURE_ATLAS.
     */
    public static final String DECORATIONS_TEXTURE_ATLAS = "sprites/SpriteSheets/Decorations.txt";
    /**
     * The constant HEALTH_WEAPON_STATS_TEXTURE_ATLAS.
     */
    public static final String HEALTH_WEAPON_STATS_TEXTURE_ATLAS = "sprites/SpriteSheets/HealthWeaponStats.txt";

    /**
     * The constant MMX3_INTRO_STAGE_MUSIC.
     */
    public static final String MMX3_INTRO_STAGE_MUSIC = "music/MMX3_IntroStage.ogg";
    /**
     * The constant XENOBLADE_GAUR_PLAINS_MUSIC.
     */
    public static final String XENOBLADE_GAUR_PLAINS_MUSIC = "music/Xenoblade_GaurPlains.ogg";
    /**
     * The constant MMZ_NEO_ARCADIA_MUSIC.
     */
    public static final String MMZ_NEO_ARCADIA_MUSIC = "music/MMZ_NeoArcadia.mp3";
    /**
     * The constant MMX_LEVEL_SELECT_SCREEN_MUSIC.
     */
    public static final String MMX_LEVEL_SELECT_SCREEN_MUSIC = "music/MMX_LevelSelectScreen.ogg";
    /**
     * The constant STAGE_SELECT_MM3_MUSIC.
     */
    public static final String STAGE_SELECT_MM3_MUSIC = "music/StageSelectMM3.mp3";

    /**
     * The constant CURSOR_MOVE_BLOOP_SOUND.
     */
    public static final String CURSOR_MOVE_BLOOP_SOUND = "sounds/CursorMoveBloop.mp3";
    /**
     * The constant MARIO_JUMP_SOUND.
     */
    public static final String MARIO_JUMP_SOUND = "sounds/MarioJump.mp3";
    /**
     * The constant SELECT_PING_SOUND.
     */
    public static final String SELECT_PING_SOUND = "sounds/SelectPing.mp3";
    /**
     * The constant DINK_SOUND.
     */
    public static final String DINK_SOUND = "sounds/Dink.mp3";
    /**
     * The constant EXPLOSION_SOUND.
     */
    public static final String EXPLOSION_SOUND = "sounds/Explosion.mp3";
    /**
     * The constant ENEMY_DAMAGE_SOUND.
     */
    public static final String ENEMY_DAMAGE_SOUND = "sounds/EnemyDamage.mp3";
    /**
     * The constant ENEMY_BULLET_SOUND.
     */
    public static final String ENEMY_BULLET_SOUND = "sounds/EnemyShoot.mp3";
    /**
     * The constant MEGA_BUSTER_BULLET_SHOT_SOUND.
     */
    public static final String MEGA_BUSTER_BULLET_SHOT_SOUND = "sounds/MegaBusterBulletShot.mp3";
    /**
     * The constant MEGA_BUSTER_CHARGING_SOUND.
     */
    public static final String MEGA_BUSTER_CHARGING_SOUND = "sounds/MegaBusterCharging.mp3";
    /**
     * The constant MEGA_BUSTER_CHARGED_SHOT_SOUND.
     */
    public static final String MEGA_BUSTER_CHARGED_SHOT_SOUND = "sounds/MegaBusterChargedShot.mp3";
    /**
     * The constant MEGAMAN_DAMAGE_SOUND.
     */
    public static final String MEGAMAN_DAMAGE_SOUND = "sounds/MegamanDamage.mp3";
    /**
     * The constant MEGAMAN_DEFEAT_SOUND.
     */
    public static final String MEGAMAN_DEFEAT_SOUND = "sounds/MegamanDefeat.mp3";
    /**
     * The constant MEGAMAN_LAND_SOUND.
     */
    public static final String MEGAMAN_LAND_SOUND = "sounds/MegamanLand.mp3";
    /**
     * The constant WHOOSH_SOUND.
     */
    public static final String WHOOSH_SOUND = "sounds/Whoosh.mp3";
    /**
     * The constant THUMP_SOUND.
     */
    public static final String THUMP_SOUND = "sounds/Thump.mp3";
    /**
     * The constant ENERGY_FILL_SOUND.
     */
    public static final String ENERGY_FILL_SOUND = "sounds/EnergyFill.mp3";
    /**
     * The constant PAUSE_SOUND.
     */
    public static final String PAUSE_SOUND = "sounds/PauseMenu.mp3";

    /**
     * The constant PPM.
     */
    public static float PPM = 16f;
    /**
     * The constant P2M.
     */
    public static float P2M = 1f / PPM;
    /**
     * The constant VIEW_WIDTH.
     */
    public static float VIEW_WIDTH = 16f;
    /**
     * The constant VIEW_HEIGHT.
     */
    public static float VIEW_HEIGHT = 14f;

    private final Map<RenderingGround, Queue<SpriteHandle>> renderables = new EnumMap<>(RenderingGround.class);
    private final Map<RenderingGround, Viewport> viewports = new EnumMap<>(RenderingGround.class);
    private final Map<Class<? extends System>, System> systems = new HashMap<>();
    private final Set<MegamanAbility> megamanAbilities = new HashSet<>();
    private final Percentage megamanHealthPercentage = new Percentage();
    private final Set<MegamanWeapon> megamanWeapons = new HashSet<>();
    private final Map<String, Object> blackBoard = new HashMap<>();
    private final Map<String, Screen> screens = new HashMap<>();
    private final Set<Entity> entities = new HashSet<>();
    private LevelTiledMapManager levelTiledMapManager;
    private ControllerManager controllerManager;
    private AssetManager assetManager;
    private SpriteBatch spriteBatch;
    @Setter private GameState gameState;
    @Setter private Integer megamanLives = 0;
    @Setter private Integer megamanCredits = 0;

    @Override
    public void create() {
        for (RenderingGround renderingGround : RenderingGround.values()) {
            renderables.put(renderingGround, new PriorityQueue<>());
            viewports.put(renderingGround, new FitViewport(VIEW_WIDTH, VIEW_HEIGHT));
        }
        levelTiledMapManager = new LevelTiledMapManager();
        controllerManager = new ControllerManager();
        assetManager = new AssetManager();
        spriteBatch = new SpriteBatch();
        addSystem(new SpriteSystem(this));
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

    /**
     * Create new entity entity.
     *
     * @return the entity
     */
    public Entity createNewEntity() {
        Entity entity = new Entity();
        entities.add(entity);
        return entity;
    }

    /**
     * Add system.
     *
     * @param system the system
     */
    public void addSystem(System system) {
        systems.put(system.getClass(), system);
    }

    /**
     * Gets system.
     *
     * @param <S>   the type parameter
     * @param clazz the clazz
     * @return the system
     */
    public <S extends System> S getSystem(Class<S> clazz) {
        return clazz.cast(systems.get(clazz));
    }

    /**
     * The {@link Entity} is filtered through all {@link System} values. If the entity is not a member of a system
     * but qualifies for membership, then it is added to the system. And if the entity is a member of a system but
     * no longer qualifies for membership, then it is removed from the system.
     *
     * @param entity the entity to be filtered through the systems
     */
    public void filterEntityThroughSystems(Entity entity) {
        systems.values().forEach(system -> {
            if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                system.addEntity(entity);
            } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                system.removeEntity(entity);
            }
        });
    }

    /**
     * Removes the {@link Entity} from all {@link System} values that it is a member of.
     *
     * @param entity the entity to be removed from systems it is currently a member of
     */
    public void removeEntityFromSystems(Entity entity) {
        systems.values().forEach(system -> {
            if (system.entityIsMember(entity)) {
                system.removeEntity(entity);
            }
        });
    }

    /**
     * Returns if the {@link Entity} is a member of the {@link System} corresponding to the provided class key.
     *
     * @param clazz  the system clazz
     * @param entity the entity
     * @return if the entity is a member of the system
     */
    public boolean systemContainsEntity(Class<? extends System> clazz, Entity entity) {
        System system = systems.get(clazz);
        return system != null && system.entityIsMember(entity);
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
