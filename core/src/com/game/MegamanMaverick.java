package com.game;

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
import com.badlogic.gdx.utils.Disposable;
import com.game.ConstVals.MegamanVals;
import com.game.controllers.ControllerManager;
import com.game.entities.Entity;
import com.game.entities.megaman.MegamanStats;
import com.game.screens.menu.impl.MainMenuScreen;
import lombok.Getter;

import java.util.*;

import static com.game.ConstVals.MusicAssets.*;
import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;

/**
 * The entry point into the Megaman game. Initializes all assets and classes that need to be initialized before gameplay
 * is possible. The view method is responsible only for clearing textures from the screen and calling
 * {@link com.badlogic.gdx.Screen#render(float)} on {@link #getScreen()} every frame.
 */
@Getter
public class MegamanMaverick extends Game implements GameContext2d {

    private final Map<Class<? extends System>, System> systems = new HashMap<>();
    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<String, Object> blackBoard = new HashMap<>();
    private final Map<String, Screen> screens = new HashMap<>();
    private final Set<Entity> entities = new HashSet<>();
    @Getter private ControllerManager controllerManager;
    @Getter private AssetManager assetManager;
    @Getter private SpriteBatch spriteBatch;

    @Override
    public void create() {
        controllerManager = new ControllerManager();
        assetManager = new AssetManager();
        spriteBatch = new SpriteBatch();
        disposables.addAll(List.of(assetManager, spriteBatch));
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
        assetManager.finishLoading();
        putBlackboardObject(MegamanVals.MEGAMAN_STATS, new MegamanStats());
        setScreen(new MainMenuScreen(this));
    }

    private <S> void loadAssets(Class<S> sClass, String... sources) {
        for (String source : sources) {
            assetManager.load(source, sClass);
        }
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public Collection<Entity> viewOfEntities() {
        return Collections.unmodifiableCollection(entities);
    }

    /**
     * Gets system.
     *
     * @param <S>   the type parameter
     * @param sClass the system class
     * @return the system
     */
    @Override
    public <S extends System> S getSystem(Class<S> sClass) {
        return sClass.cast(systems.get(sClass));
    }

    private void filterEntityThroughSystems(Entity entity) {
        systems.values().forEach(system -> {
            if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                system.addEntity(entity);
            } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                system.removeEntity(entity);
            }
        });
    }

    private void removeEntityFromSystems(Entity entity) {
        systems.values().forEach(system -> {
            if (system.entityIsMember(entity)) {
                system.removeEntity(entity);
            }
        });
    }

    @Override
    public void putBlackboardObject(String key, Object object) {
        blackBoard.put(key, object);
    }

    @Override
    public <T> T getBlackboardObject(String key, Class<T> tClass) {
        return tClass.cast(blackBoard.get(key));
    }

    @Override
    public <T> T loadAsset(String key, Class<T> tClass) {
        return assetManager.get(key, tClass);
    }

    @Override
    public void setScreen(String key)
            throws NoSuchElementException {
        Screen screen = screens.get(key);
        if (screen == null) {
            throw new NoSuchElementException("No screen found associated with key " + key);
        }
        setScreen(screen);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        entities.forEach(entity -> {
            if (entity.isMarkedForRemoval()) {
                removeEntityFromSystems(entity);
            } else {
                filterEntityThroughSystems(entity);
            }
        });
        entities.removeIf(Entity::isMarkedForRemoval);
        controllerManager.updateControllerStatuses();
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        disposables.forEach(Disposable::dispose);
    }

}
