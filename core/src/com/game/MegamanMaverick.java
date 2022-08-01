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
import com.game.animations.AnimationSystem;
import com.game.behaviors.BehaviorSystem;
import com.game.controllers.ButtonStatus;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerSystem;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransSystem;
import com.game.cull.CullOnOutOfCamBoundsSystem;
import com.game.debugging.DebugRectSystem;
import com.game.entities.megaman.MegamanStats;
import com.game.graph.GraphSystem;
import com.game.health.HealthSystem;
import com.game.levels.LevelScreen;
import com.game.menus.impl.MainMenuScreen;
import com.game.pathfinding.PathfindingSystem;
import com.game.sounds.SoundSystem;
import com.game.sprites.SpriteSystem;
import com.game.trajectories.TrajectorySystem;
import com.game.updatables.UpdatableSystem;
import com.game.utils.objects.KeyValuePair;
import com.game.world.WorldContactListenerImpl;
import com.game.world.WorldSystem;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.*;
import static com.game.ConstVals.GameScreen.*;
import static com.game.ConstVals.MusicAsset.XENOBLADE_GAUR_PLAINS_MUSIC;
import static com.game.ConstVals.RenderingGround.PLAYGROUND;
import static com.game.ConstVals.ViewVals.*;
import static com.game.ConstVals.WorldVals.*;
import static com.game.controllers.ButtonStatus.*;
import static com.game.controllers.ControllerUtils.*;
import static com.game.utils.UtilMethods.*;

/**
 * The entry point into the Megaman game. Initializes all assets and classes that need to be initialized before gameplay
 * is possible. The view method is responsible only for clearing textures from the screen and calling
 * {@link com.badlogic.gdx.Screen#render(float)} on {@link #getScreen()} every frame.
 */
@Getter
public class MegamanMaverick extends Game implements GameContext2d {

    private final Map<ControllerButton, ButtonStatus> controllerButtons = new EnumMap<>(ControllerButton.class);
    private final Map<RenderingGround, Viewport> viewports = new EnumMap<>(RenderingGround.class);
    private final Map<GameScreen, Supplier<Screen>> screens = new EnumMap<>(GameScreen.class);
    private final Queue<KeyValuePair<Rectangle, Color>> debugQueue = new ArrayDeque<>();
    private final Map<Class<? extends System>, System> systems = new LinkedHashMap<>();
    private final Set<MessageListener> messageListeners = new HashSet<>();
    private final Queue<Message> messageQueue = new ArrayDeque<>();
    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<String, Object> blackBoard = new HashMap<>();
    private final List<Runnable> runOnShutdown = new ArrayList<>();
    private final Set<IEntity> entities = new HashSet<>();
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    private SpriteBatch spriteBatch;

    @Setter
    private boolean doUpdateController;

    @Override
    public void create() {
        // viewports
        for (RenderingGround renderingGround : RenderingGround.values()) {
            viewports.put(renderingGround, new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM));
        }
        // controller buttons
        for (ControllerButton controllerButton : ControllerButton.values()) {
            controllerButtons.put(controllerButton, IS_RELEASED);
        }
        // rendering
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();
        spriteBatch = new SpriteBatch();
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
        addSystem(new ControllerSystem(this));
        addSystem(new CullOnCamTransSystem());
        addSystem(new CullOnOutOfCamBoundsSystem(getViewport(PLAYGROUND).getCamera()));
        addSystem(new HealthSystem());
        addSystem(new TrajectorySystem());
        addSystem(new WorldSystem(new WorldContactListenerImpl(), AIR_RESISTANCE, FIXED_TIME_STEP));
        addSystem(new PathfindingSystem(runOnShutdown));
        addSystem(new UpdatableSystem());
        addSystem(new BehaviorSystem());
        addSystem(new GraphSystem());
        addSystem(new SpriteSystem((OrthographicCamera) viewports.get(PLAYGROUND).getCamera(), getSpriteBatch()));
        addSystem(new AnimationSystem());
        addSystem(new DebugRectSystem(viewports.get(PLAYGROUND).getCamera(), getShapeRenderer()));
        addSystem(new SoundSystem(this));
        // blackboard
        putBlackboardObject(MegamanVals.MEGAMAN_STATS, new MegamanStats());
        // define screens
        screens.put(MAIN_MENU, () -> new MainMenuScreen(this));
        screens.put(TEST_LEVEL_1, () -> new LevelScreen(
                this, "tiledmaps/tmx/test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc()));
        // set screen
        setScreen(MAIN_MENU);
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
        Screen screen = screens.get(key).get();
        if (screen == null) {
            throw new NoSuchElementException("No screen found associated with key " + key);
        }
        setScreen(screen);
    }

    @Override
    public void setScreen(Screen screen) {
        if (this.screen != null) {
            this.screen.dispose();
        }
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    @Override
    public boolean isJustPressed(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_PRESSED;
    }

    @Override
    public boolean isPressed(ControllerButton controllerButton) {
        ButtonStatus buttonStatus = controllerButtons.get(controllerButton);
        return equalsAny(buttonStatus, IS_JUST_PRESSED, IS_PRESSED);
    }

    @Override
    public boolean isJustReleased(ControllerButton controllerButton) {
        return controllerButtons.get(controllerButton) == IS_JUST_RELEASED;
    }

    @Override
    public void updateController() {
        for (ControllerButton controllerButton : ControllerButton.values()) {
            ButtonStatus status = controllerButtons.get(controllerButton);
            boolean isControllerButtonPressed = isControllerConnected() ?
                    isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                    isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
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
    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    @Override
    public void removeMessageListener(MessageListener messageListener) {
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
            messageListeners.forEach(listener -> listener.listenToMessage(message.owner(), message.contents(), delta));
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (doUpdateController()) {
            updateController();
        }
        updateMessageDispatcher(Gdx.graphics.getDeltaTime());
        super.render();
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
