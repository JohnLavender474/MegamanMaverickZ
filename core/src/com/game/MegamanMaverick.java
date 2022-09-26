package com.game;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.assets.MusicAsset;
import com.game.assets.SoundAsset;
import com.game.assets.TextureAsset;
import com.game.controllers.ControllerUtils;
import com.game.animations.AnimationSystem;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ButtonStatus;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerSystem;
import com.game.cull.CullOnOutOfCamBoundsSystem;
import com.game.cull.CullOnMessageSystem;
import com.game.entities.Entity;
import com.game.entities.megaman.MegamanInfo;
import com.game.messages.Message;
import com.game.levels.LevelStatus;
import com.game.messages.MessageListener;
import com.game.shapes.LineSystem;
import com.game.shapes.ShapeSystem;
import com.game.graph.GraphSystem;
import com.game.health.HealthSystem;
import com.game.levels.LevelScreen;
import com.game.menus.impl.BossSelectScreen;
import com.game.menus.impl.ExtrasScreen;
import com.game.menus.impl.MainMenuScreen;
import com.game.menus.impl.PauseMenuScreen;
import com.game.movement.PendulumSystem;
import com.game.movement.RotatingLineSystem;
import com.game.pathfinding.PathfindingSystem;
import com.game.levels.BossIntroScreen;
import com.game.sounds.SoundSystem;
import com.game.sprites.RenderingGround;
import com.game.sprites.SpriteSystem;
import com.game.movement.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.DebugLogger;
import com.game.utils.objects.KeyValuePair;
import com.game.world.WorldContactListenerImpl;
import com.game.world.WorldSystem;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.badlogic.gdx.Gdx.*;
import static com.game.entities.megaman.MegamanWeapon.*;
import static com.game.messages.MessageType.*;
import static com.game.GameScreen.*;
import static com.game.levels.LevelStatus.*;
import static com.game.entities.megaman.MegamanVals.*;
import static com.game.assets.MusicAsset.*;
import static com.game.sprites.RenderingGround.PLAYGROUND;
import static com.game.ViewVals.*;
import static com.game.world.WorldVals.*;
import static com.game.controllers.ButtonStatus.*;
import static com.game.utils.DebugLogger.DebugLevel.*;
import static com.game.utils.UtilMethods.*;
import static java.util.Collections.*;

/** Entry point into game. */
@Getter
public class MegamanMaverick extends Game implements GameContext2d, MessageListener {

    private final Map<Class<? extends System>, System> systems = new LinkedHashMap<>();
    private final Set<Entity> entities = new HashSet<>();

    private final Map<ControllerButton, ButtonStatus> controllerButtons = new EnumMap<>(ControllerButton.class);

    private final Map<RenderingGround, Viewport> viewports = new EnumMap<>(RenderingGround.class);
    private final Map<GameScreen, Screen> screens = new EnumMap<>(GameScreen.class);

    private final Set<MessageListener> messageListeners = new HashSet<>();

    private final Map<String, Object> blackBoard = new HashMap<>();

    private final List<Disposable> disposables = new ArrayList<>();
    private final List<Runnable> runOnShutdown = new ArrayList<>();

    private final Queue<KeyValuePair<Rectangle, Color>> debugQueue = new ArrayDeque<>();

    private GameScreen currentScreenKey;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    private SpriteBatch spriteBatch;

    private int soundEffectsVolume = 6;
    private int musicVolume = 6;

    private Screen overlayScreen;
    private GameScreen overlayScreenKey;

    @Setter
    private LevelStatus levelStatus = LevelStatus.NONE;
    @Setter
    private boolean doUpdateController;

    @Override
    public void create() {
        DebugLogger.getInstance().setGlobalDebugLevel(DEBUG);
        // viewports
        for (RenderingGround renderingGround : RenderingGround.values()) {
            Viewport viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
            viewport.getCamera().position.set(ConstFuncs.getCamInitPos());
            viewports.put(renderingGround, viewport);
        }
        // controller buttons
        for (ControllerButton controllerButton : ControllerButton.values()) {
            controllerButtons.put(controllerButton, IS_RELEASED);
        }
        // rendering
        spriteBatch = new SpriteBatch();
        assetManager = new AssetManager();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        // disposables
        disposables.addAll(List.of(assetManager, spriteBatch, shapeRenderer));
        // assets
        for (MusicAsset musicAsset : MusicAsset.values()) {
            assetManager.load(musicAsset.getSrc(), Music.class);
        }
        for (SoundAsset soundAsset : SoundAsset.values()) {
            assetManager.load(soundAsset.getSrc(), Sound.class);
        }
        for (TextureAsset textureAsset : TextureAsset.values()) {
            assetManager.load(textureAsset.getSrc(), TextureAtlas.class);
        }
        assetManager.finishLoading();
        // systems
        addSystem(new ControllerSystem(this::isControllerButtonPressed));
        addSystem(new CullOnMessageSystem(this));
        addSystem(new CullOnOutOfCamBoundsSystem(getViewport(PLAYGROUND).getCamera()));
        addSystem(new HealthSystem());
        addSystem(new TrajectorySystem());
        addSystem(new WorldSystem(new WorldContactListenerImpl(), AIR_RESISTANCE, FIXED_TIME_STEP));
        addSystem(new GraphSystem());
        addSystem(new PathfindingSystem(runOnShutdown));
        addSystem(new RotatingLineSystem());
        addSystem(new PendulumSystem());
        addSystem(new UpdatableSystem());
        addSystem(new BehaviorSystem());
        addSystem(new SoundSystem(this));
        addSystem(new AnimationSystem());
        addSystem(new SpriteSystem((OrthographicCamera) viewports.get(PLAYGROUND).getCamera(), getSpriteBatch()));
        addSystem(new LineSystem(viewports.get(PLAYGROUND).getCamera(), getShapeRenderer()));
        addSystem(new ShapeSystem(viewports.get(PLAYGROUND).getCamera(), getShapeRenderer()));
        // blackboard
        MegamanInfo megamanInfo = new MegamanInfo();
        megamanInfo.putWeapon(MEGA_BUSTER);
        putBlackboardObject(MEGAMAN_INFO, megamanInfo);
        // add this as message listener
        addMessageListener(this);
        // define screens
        screens.put(MAIN_MENU, new MainMenuScreen(this));
        screens.put(EXTRAS, new ExtrasScreen(this));
        screens.put(BOSS_SELECT, new BossSelectScreen(this));
        screens.put(PAUSE_MENU, new PauseMenuScreen(this));
        screens.put(LEVEL_INTRO, new BossIntroScreen(this));
        screens.put(TEST_STAGE, new LevelScreen(
                this, "tiledmaps/tmx/Test3.tmx", MMZ_NEO_ARCADIA_MUSIC.getSrc()));
        screens.put(TIMBER_WOMAN, new LevelScreen(
                this, "tiledmaps/tmx/TimberWoman.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc()));
        // set screen
        setScreen(MAIN_MENU);
        // setScreen(TEST_STAGE);
        // setScreen(TIMBER_WOMAN);
        // setScreen(new TestScreen(this));
    }

    @Override
    public Viewport getViewport(RenderingGround renderingGround) {
        return viewports.get(renderingGround);
    }

    @Override
    public void setSpriteBatchProjectionMatrix(RenderingGround renderingGround) {
        getSpriteBatch().setProjectionMatrix(getViewport(renderingGround).getCamera().combined);
    }

    @Override
    public void setShapeRendererProjectionMatrix(RenderingGround renderingGround) {
        getShapeRenderer().setProjectionMatrix(getViewport(renderingGround).getCamera().combined);
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public Collection<Entity> getEntities() {
        return unmodifiableCollection(entities);
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
        Set<Entity> entitiesToRemove = new HashSet<>();
        entities.forEach(entity -> {
            if (entity.isDead()) {
                systems.values().forEach(system -> {
                    if (system.entityIsMember(entity)) {
                        system.removeEntity(entity);
                    }
                });
                entity.onDeath();
                entitiesToRemove.add(entity);
            } else {
                systems.values().forEach(system -> {
                    if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                        system.addEntity(entity);
                    } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                        system.removeEntity(entity);
                    }
                });
            }
        });
        entitiesToRemove.forEach(this::removeEntity);
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
    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    @Override
    public void removeMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    @Override
    public void sendMessage(Message message) {
        messageListeners.forEach(messageListener -> messageListener.listenToMessage(message));
    }

    @Override
    public void putOverlayScreen(GameScreen overlayScreenKey) {
        popOverlayScreen();
        this.overlayScreenKey = overlayScreenKey;
        overlayScreen = screens.get(overlayScreenKey);
    }

    @Override
    public void popOverlayScreen() {
        if (overlayScreen == null) {
            return;
        }
        overlayScreen.dispose();
        currentScreenKey = null;
        overlayScreen = null;
    }

    @Override
    public void setScreen(GameScreen key) throws NoSuchElementException {
        currentScreenKey = key;
        Screen screen = screens.get(currentScreenKey);
        if (screen == null) {
            throw new NoSuchElementException("No screen found associated with key " + key);
        }
        setScreen(screen);
    }

    @Override
    public void setScreen(Screen screen) {
        viewports.values().forEach(viewport -> viewport.getCamera().position.set(ConstFuncs.getCamInitPos()));
        if (this.screen != null) {
            this.screen.dispose();
        }
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(graphics.getWidth(), graphics.getHeight());
        }
    }

    @Override
    public Screen getScreen(GameScreen gameScreen) {
        return screens.get(gameScreen);
    }

    @Override
    public boolean isControllerButtonJustPressed(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_PRESSED;
    }

    @Override
    public boolean isControllerButtonPressed(ControllerButton controllerButton) {
        ButtonStatus buttonStatus = controllerButtons.get(controllerButton);
        return equalsAny(buttonStatus, IS_JUST_PRESSED, IS_PRESSED);
    }

    @Override
    public boolean isControllerButtonJustReleased(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_RELEASED;
    }

    @Override
    public void updateController() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ButtonStatus status = controllerButtons.get(controllerButton);
            boolean isControllerButtonPressed = ControllerUtils.isControllerConnected() ?
                    ControllerUtils.isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                    ControllerUtils.isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
            if (isControllerButtonPressed) {
                if (equalsAny(status, IS_RELEASED, IS_JUST_RELEASED)) {
                    controllerButtons.replace(controllerButton, IS_JUST_PRESSED);
                } else {
                    controllerButtons.replace(controllerButton, IS_PRESSED);
                }
            } else if (equalsAny(status, IS_RELEASED, IS_JUST_RELEASED)) {
                controllerButtons.replace(controllerButton, IS_RELEASED);
            } else {
                controllerButtons.replace(controllerButton, IS_JUST_RELEASED);
            }
        }
    }

    @Override
    public boolean doUpdateController() {
        return doUpdateController;
    }

    @Override
    public void listenToMessage(Message message) {
        switch (message.getMessageType()) {
            case LEVEL_PAUSED -> setLevelStatus(PAUSED);
            case LEVEL_UNPAUSED -> setLevelStatus(UNPAUSED);
        }
    }

    @Override
    public void setSoundEffectsVolume(int soundEffectsVolume) {
        this.soundEffectsVolume = soundEffectsVolume;
        sendMessage(new Message(SOUND_VOLUME_CHANGE));
    }

    @Override
    public void setMusicVolume(int musicVolume) {
        Array<Music> musicArray = new Array<>(MusicAsset.values().length);
        assetManager.getAll(Music.class, musicArray);
        musicArray.forEach(music -> music.setVolume(musicVolume / 10f));
        this.musicVolume = musicVolume;
    }

    @Override
    public void render() {
        float delta = graphics.getDeltaTime();
        gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            app.exit();
        }
        if (doUpdateController()) {
            updateController();
        }
        super.render();
        if (overlayScreen != null) {
            overlayScreen.render(delta);
        }
        viewports.values().forEach(Viewport::apply);
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
        runOnShutdown.forEach(Runnable::run);
        disposables.forEach(Disposable::dispose);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewports.values().forEach(viewport -> viewport.update(width, height));
    }

}
