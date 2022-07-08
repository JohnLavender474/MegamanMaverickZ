package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.GameScreen;
import com.game.ConstVals.MegamanVals;
import com.game.ConstVals.RenderingGround;
import com.game.ConstVals.WorldVals;
import com.game.animations.AnimationSystem;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerButtonStatus;
import com.game.controllers.ControllerSystem;
import com.game.core.IEntity;
import com.game.debugging.DebugSystem;
import com.game.entities.megaman.MegamanStats;
import com.game.health.HealthSystem;
import com.game.levels.LevelScreen;
import com.game.menus.impl.MainMenuScreen;
import com.game.sprites.SpriteSystem;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.KeyValuePair;
import com.game.world.WorldContactListenerImpl;
import com.game.world.WorldSystem;
import lombok.Getter;

import java.util.*;

import static com.game.ConstVals.MusicAssets.*;
import static com.game.ConstVals.RenderingGround.PLAYGROUND;
import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.controllers.ControllerUtils.*;

/**
 * The entry point into the Megaman game. Initializes all assets and classes that need to be initialized before gameplay
 * is possible. The view method is responsible only for clearing textures from the screen and calling
 * {@link com.badlogic.gdx.Screen#render(float)} on {@link #getScreen()} every frame.
 */
@Getter
public class MegamanMaverick extends Game implements GameContext2d {

    private final Map<ControllerButton, ControllerButtonStatus> controllerButtons =
            new EnumMap<>(ControllerButton.class);
    private final Map<RenderingGround, Viewport> viewports = new EnumMap<>(RenderingGround.class);
    private final Queue<KeyValuePair<Rectangle, Color>> debugQueue = new ArrayDeque<>();
    private final Map<Class<? extends System>, System> systems = new LinkedHashMap<>();
    private final Map<GameScreen, Screen> screens = new EnumMap<>(GameScreen.class);
    private final Set<MessageListener> messageListeners = new HashSet<>();
    private final Queue<Message> messageQueue = new ArrayDeque<>();
    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<String, Object> blackBoard = new HashMap<>();
    private final Set<IEntity> entities = new HashSet<>();
    @Getter
    private ShapeRenderer shapeRenderer;
    @Getter
    private SpriteBatch spriteBatch;
    private AssetManager assetManager;

    @Override
    public void create() {
        // viewports
        for (RenderingGround renderingGround : RenderingGround.values()) {
            viewports.put(renderingGround, new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM));
        }
        // controller buttons
        for (ControllerButton controllerButton : ControllerButton.values()) {
            controllerButtons.put(controllerButton, ControllerButtonStatus.IS_RELEASED);
        }
        // screens

        // rendering
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();
        spriteBatch = new SpriteBatch();
        disposables.addAll(List.of(assetManager, spriteBatch, shapeRenderer));
        // assets
        loadAssets(Music.class, MMX3_INTRO_STAGE_MUSIC, MMZ_NEO_ARCADIA_MUSIC, XENOBLADE_GAUR_PLAINS_MUSIC,
                MMX_LEVEL_SELECT_SCREEN_MUSIC, STAGE_SELECT_MM3_MUSIC);
        loadAssets(Sound.class, SELECT_PING_SOUND, MARIO_JUMP_SOUND, CURSOR_MOVE_BLOOP_SOUND, DINK_SOUND,
                ENEMY_BULLET_SOUND, ENEMY_DAMAGE_SOUND, MEGA_BUSTER_BULLET_SHOT_SOUND, MEGA_BUSTER_CHARGED_SHOT_SOUND
                , ENERGY_FILL_SOUND, MEGA_BUSTER_CHARGING_SOUND, MEGAMAN_DAMAGE_SOUND, MEGAMAN_LAND_SOUND,
                MEGAMAN_DEFEAT_SOUND, WHOOSH_SOUND, THUMP_SOUND, EXPLOSION_SOUND, PAUSE_SOUND);
        loadAssets(TextureAtlas.class, CHARGE_ORBS_TEXTURE_ATLAS, OBJECTS_TEXTURE_ATLAS, MET_TEXTURE_ATLAS,
                ENEMIES_TEXTURE_ATLAS, ITEMS_TEXTURE_ATLAS, BACKGROUNDS_1_TEXTURE_ATLAS, MEGAMAN_TEXTURE_ATLAS,
                MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS, ELECTRIC_BALL_TEXTURE_ATLAS, DECORATIONS_TEXTURE_ATLAS,
                BITS_ATLAS);
        assetManager.finishLoading();
        // systems
        addSystem(new HealthSystem());
        addSystem(new UpdatableSystem());
        addSystem(new ControllerSystem(this));
        addSystem(new WorldSystem(new WorldContactListenerImpl(),
                WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP));
        addSystem(new BehaviorSystem());
        addSystem(new TrajectorySystem());
        addSystem(new AnimationSystem());
        addSystem(new SpriteSystem((OrthographicCamera) viewports.get(PLAYGROUND).getCamera(), getSpriteBatch()));
        addSystem(new DebugSystem(getShapeRenderer(), (OrthographicCamera) viewports.get(PLAYGROUND).getCamera()));
        // blackboard
        putBlackboardObject(MegamanVals.MEGAMAN_STATS, new MegamanStats());
        // set screen
        putScreen(GameScreen.MAIN_MENU, new MainMenuScreen(this));
        putScreen(GameScreen.TEST_LEVEL_1, new LevelScreen(this, "tiledmaps/tmx/test1.tmx", MMX3_INTRO_STAGE_MUSIC));
        setScreen(GameScreen.TEST_LEVEL_1);
    }

    private <S> void loadAssets(Class<S> sClass, String... sources) {
        for (String source : sources) {
            assetManager.load(source, sClass);
        }
    }

    @Override
    public Viewport getViewport(RenderingGround renderingGround) {
        return viewports.get(renderingGround);
    }

    @Override
    public void addEntity(IEntity entity) {
        entities.add(entity);
    }

    @Override
    public Collection<IEntity> getEntities() {
        return Collections.unmodifiableCollection(entities);
    }

    @Override
    public void purgeAllEntities() {
        systems.values().forEach(System::purgeAllEntities);
        entities.clear();
    }

    @Override
    public void addSystem(System system) {
        systems.put(system.getClass(), system);
    }

    @Override
    public <S extends System> S getSystem(Class<S> sClass) {
        return sClass.cast(systems.get(sClass));
    }

    @Override
    public Collection<System> getSystems() {
        return systems.values();
    }

    @Override
    public void updateSystems(float delta) {
        Iterator<IEntity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            IEntity entity = entityIterator.next();
            if (entity.isDead()) {
                systems.values().forEach(system -> {
                    if (system.entityIsMember(entity)) {
                        system.removeEntity(entity);
                    }
                });
                entityIterator.remove();
                entity.onDeath();
            } else {
                systems.values().forEach(system -> {
                    if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                        system.addEntity(entity);
                    } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                        system.removeEntity(entity);
                    }
                });
            }
        }
        systems.values().forEach(system -> system.update(delta));
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
    public <T> T getAsset(String key, Class<T> tClass) {
        return assetManager.get(key, tClass);
    }

    @Override
    public void setScreen(GameScreen key) throws NoSuchElementException {
        Screen screen = screens.get(key);
        if (screen == null) {
            throw new NoSuchElementException("No screen found associated with key " + key);
        }
        setScreen(screen);
    }

    @Override
    public void putScreen(GameScreen key, Screen screen) {
        screens.put(key, screen);
    }

    @Override
    public boolean isJustPressed(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == ControllerButtonStatus.IS_JUST_PRESSED;
    }

    @Override
    public boolean isPressed(ControllerButton controllerButton) {
        ControllerButtonStatus controllerButtonStatus = controllerButtons.get(controllerButton);
        return controllerButtonStatus == ControllerButtonStatus.IS_JUST_PRESSED ||
                controllerButtonStatus == ControllerButtonStatus.IS_PRESSED;
    }

    @Override
    public boolean isJustReleased(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == ControllerButtonStatus.IS_JUST_RELEASED;
    }

    @Override
    public void updateController() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ControllerButtonStatus status = controllerButtons.get(controllerButton);
            boolean isControllerButtonPressed = isControllerConnected() ?
                    isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                    isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
            if (isControllerButtonPressed) {
                if (status == ControllerButtonStatus.IS_RELEASED ||
                        status == ControllerButtonStatus.IS_JUST_RELEASED) {
                    controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_JUST_PRESSED);
                } else {
                    controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_PRESSED);
                }
            } else if (status == ControllerButtonStatus.IS_JUST_RELEASED ||
                    status == ControllerButtonStatus.IS_RELEASED) {
                controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_RELEASED);
            } else {
                controllerButtons.replace(controllerButton, ControllerButtonStatus.IS_JUST_RELEASED);
            }
        }
    }

    @Override
    public void addListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    @Override
    public void removeListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    @Override
    public void addMessage(Message message) {
        messageQueue.add(message);
    }

    @Override
    public void updateMessageDispatcher(float delta) {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            messageListeners.forEach(listener -> listener.listenToMessage(
                    message.owner(), message.contents(), delta));
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        updateController();
        updateMessageDispatcher(Gdx.graphics.getDeltaTime());
        super.render();
        viewports.values().forEach(Viewport::apply);
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
        disposables.forEach(Disposable::dispose);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewports.values().forEach(viewport -> viewport.update(width, height));
    }

}
