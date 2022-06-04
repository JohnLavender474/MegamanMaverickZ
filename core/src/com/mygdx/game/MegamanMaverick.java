package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.ConstVals.GameState;
import com.mygdx.game.controllers.ControllerManager;
import com.mygdx.game.core.SystemsManager;
import com.mygdx.game.screens.menus.impl.MainMenuScreen;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.mygdx.game.ConstVals.MusicAssets.*;
import static com.mygdx.game.ConstVals.SoundAssets.*;
import static com.mygdx.game.ConstVals.TextureAssets.*;

/**
 * The entry point into the Megaman game. Initializes all assets and classes that need to be initialized before gameplay
 * is possible. The view method is responsible only for clearing textures from the screen and calling
 * {@link com.badlogic.gdx.Screen#render(float)} on {@link #getScreen()} every frame.
 */
@Getter
public class MegamanMaverick extends Game implements GameContext2d {

    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<String, Object> blackBoard = new HashMap<>();
    private final Map<String, Screen> screens = new HashMap<>();
    @Getter private ControllerManager controllerManager;
    @Getter private SystemsManager systemsManager;
    @Getter @Setter private GameState gameState;
    @Getter private AssetManager assetManager;
    @Getter private SpriteBatch spriteBatch;

    @Override
    public void create() {
        gameState = ConstVals.GameState.IN_MENU;
        controllerManager = new ControllerManager();
        systemsManager = new SystemsManager();
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
        setScreen(new MainMenuScreen(this));
    }

    private <S> void loadAssets(Class<S> sClass, String... sources) {
        for (String source : sources) {
            assetManager.load(source, sClass);
        }
    }

    @Override
    public <T> T getAsset(String key, Class<T> tClass) {
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
        controllerManager.updateControllerStatuses();
        controllerManager.updateControllerListeners();
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        disposables.forEach(Disposable::dispose);
    }

}
