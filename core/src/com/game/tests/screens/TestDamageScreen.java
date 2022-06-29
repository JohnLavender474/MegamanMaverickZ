package com.game.tests.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.*;
import com.game.ConstVals.TextureAssets;
import com.game.ConstVals.VolumeVals;
import com.game.ConstVals.WorldVals;
import com.game.System;
import com.game.animations.AnimationComponent;
import com.game.animations.AnimationSystem;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorSystem;
import com.game.behaviors.BehaviorType;
import com.game.contracts.Damageable;
import com.game.contracts.Damager;
import com.game.contracts.Faceable;
import com.game.contracts.Facing;
import com.game.controllers.*;
import com.game.core.IAssetLoader;
import com.game.core.IController;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IMessageDispatcher;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugSystem;
import com.game.entities.megaman.behaviors.MegamanRun;
import com.game.screens.levels.CullOnLevelCamTrans;
import com.game.screens.levels.CullOnOutOfGameCamBounds;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.sound.SoundComponent;
import com.game.sound.SoundRequest;
import com.game.sound.SoundSystem;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteSystem;
import com.game.tests.screens.TestDamageScreen.TestPlayer.AButtonTask;
import com.game.updatables.UpdatableComponent;
import com.game.updatables.UpdatableSystem;
import com.game.utils.*;
import com.game.utils.Timer;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.MusicAssets.MMZ_NEO_ARCADIA_MUSIC;
import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.SoundAssets.THUMP_SOUND;
import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;
import static com.game.controllers.ControllerButtonStatus.*;
import static com.game.controllers.ControllerButtonStatus.IS_JUST_RELEASED;
import static com.game.controllers.ControllerUtils.*;

public class TestDamageScreen extends ScreenAdapter {


    static class AssetLoader implements IAssetLoader, Disposable {

        private final AssetManager assetManager = new AssetManager();

        public AssetLoader() {
            loadAssets(Music.class,
                       MMZ_NEO_ARCADIA_MUSIC);
            loadAssets(Sound.class,
                       ENEMY_BULLET_SOUND,
                       ENEMY_DAMAGE_SOUND,
                       MEGA_BUSTER_BULLET_SHOT_SOUND,
                       MEGAMAN_LAND_SOUND,
                       MEGAMAN_DEFEAT_SOUND,
                       WHOOSH_SOUND,
                       THUMP_SOUND);
            loadAssets(TextureAtlas.class,
                       OBJECTS_TEXTURE_ATLAS,
                       MEGAMAN_TEXTURE_ATLAS,
                       DECORATIONS_TEXTURE_ATLAS);
            assetManager.finishLoading();
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
        public void dispose() {
            assetManager.dispose();
        }

    }

    static class MessageDispatcher implements IMessageDispatcher {

        private final Set<MessageListener> messageListeners = new HashSet<>();
        private final Queue<Message> messageQueue = new ArrayDeque<>();

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
                messageListeners.forEach(listener -> {
                    if (listener.isListeningForMessageFrom(message.getOwner())) {
                        listener.listenToMessage(message.getOwner(), message.getContents(), delta);
                    }
                });
            }
        }

    }

    static class EntitiesAndSystemsManager implements IEntitiesAndSystemsManager {

        private final Set<Entity> entities = new HashSet<>();
        private final Map<Class<? extends System>, System> systems = new LinkedHashMap<>();
        private final Queue<Entity> queuedEntities = new ArrayDeque<>();
        private boolean updating;

        @Override
        public void addEntity(Entity entity) {
            if (updating) {
                queuedEntities.add(entity);
            } else {
                entities.add(entity);
            }
        }

        @Override
        public Collection<Entity> viewOfEntities() {
            return entities;
        }

        @Override
        public void purgeAllEntities() {
            entities.clear();
            systems.values().forEach(System::purgeAllEntities);
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
        public void updateSystems(float delta) {
            updating = true;
            while (!queuedEntities.isEmpty()) {
                entities.add(queuedEntities.poll());
            }
            Iterator<Entity> entityIterator = entities.iterator();
            while (entityIterator.hasNext()) {
                Entity entity = entityIterator.next();
                if (entity.isDead()) {
                    systems.values().forEach(system -> {
                        if (system.entityIsMember(entity)) {
                            system.removeEntity(entity);
                        }
                    });
                    entity.onDeath();
                    entityIterator.remove();
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
            updating = false;
        }

    }

    @Getter
    @Setter
    static class TestDamager implements Entity, Damager {

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        private final Timer timer = new Timer(1f);

        public TestDamager(Rectangle bounds) {
            timer.setToEnd();
            addComponent(defineBodyComponent(bounds));
            addComponent(defineDebugComponent());
            addComponent(defineUpdatableComponent());
        }

        @Override
        public void onDamageInflictedTo(Class<? extends Damageable> damageableClass) {
            timer.reset();
        }

        private BodyComponent defineBodyComponent(Rectangle bounds) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
            bodyComponent.set(bounds);
            bodyComponent.setGravityOn(false);
            bodyComponent.setFriction(0f, 0f);
            bodyComponent.setAffectedByResistance(false);
            Fixture damageBox = new Fixture(this, FixtureType.DAMAGE_BOX);
            damageBox.set(UtilMethods.getScaledRect(bounds, 1.05f));
            bodyComponent.addFixture(damageBox);
            return bodyComponent;
        }

        private DebugComponent defineDebugComponent() {
            DebugComponent debugComponent = new DebugComponent();
            debugComponent.addDebugHandle(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> timer.isFinished() ? Color.PURPLE : Color.RED);
            return debugComponent;
        }

        private UpdatableComponent defineUpdatableComponent() {
            UpdatableComponent updatableComponent = new UpdatableComponent();
            updatableComponent.setUpdatable(delta -> {
                if (!timer.isFinished()) {
                    timer.update(delta);
                }
            });
            return updatableComponent;
        }

    }

    @Getter
    @Setter
    static class TestPlayer implements Entity, Damageable, Faceable, LevelCameraFocusable {

        enum AButtonTask {
            JUMP,
            AIR_DASH
        }

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        private final IController controller;
        private final IAssetLoader assetLoader;
        private final IEntitiesAndSystemsManager entitiesAndSystemsManager;

        private boolean isCharging;
        private Facing facing = Facing.RIGHT;
        private AButtonTask aButtonTask = AButtonTask.JUMP;

        private final Rectangle priorFocusBox = new Rectangle(0f, 0f, .8f * PPM, .95f * PPM);
        private final Rectangle currentFocusBox = new Rectangle(0f, 0f, .8f * PPM, .95f * PPM);
        
        private final Timer airDashTimer = new Timer(.25f);
        private final Timer groundSlideTimer = new Timer(.35f);
        private final Timer wallJumpImpetusTimer = new Timer(.2f);
        private final Timer megaBusterChargingTimer = new Timer(.5f);
        private final Timer shootCoolDownTimer = new Timer(.1f);
        private final Timer shootAnimationTimer = new Timer(.5f);

        private final Timer damageTimer = new Timer(.75f);
        private final Timer damageRecoveryTimer = new Timer(1.5f);
        private final Timer damageRecoveryBlinkTimer = new Timer(.05f);
        private boolean recoveryBlink;

        public TestPlayer(IController controller, IAssetLoader assetLoader, IEntitiesAndSystemsManager entitiesAndSystemsManager) {
            this.controller = controller;
            this.assetLoader = assetLoader;
            this.entitiesAndSystemsManager = entitiesAndSystemsManager;
            addComponent(defineUpdatableComponent());
            addComponent(defineControllerComponent());
            addComponent(defineBehaviorComponent());
            addComponent(defineBodyComponent());
            addComponent(defineDebugComponent());
            addComponent(defineSpriteComponent());
            addComponent(defineAnimationComponent(assetLoader.getAsset(TextureAssets.MEGAMAN_TEXTURE_ATLAS, TextureAtlas.class)));
            shootCoolDownTimer.setToEnd();
            shootAnimationTimer.setToEnd();
            wallJumpImpetusTimer.setToEnd();
            damageTimer.setToEnd();
            damageRecoveryTimer.setToEnd();
        }

        @Override
        public void onDeath() {

        }

        @Override
        public Set<Class<? extends Damager>> getDamagerMaskSet() {
            return Set.of(TestDamager.class);
        }

        @Override
        public void takeDamageFrom(Class<? extends Damager> damagerClass) {
            if (damagerClass.equals(TestDamager.class)) {
                damageTimer.reset();
            }
        }

        @Override
        public boolean isInvincible() {
            return !damageTimer.isFinished() || !damageRecoveryTimer.isFinished();
        }

        public boolean isDamaged() {
            return !damageTimer.isFinished();
        }

        public boolean isShooting() {
            return !shootAnimationTimer.isFinished();
        }

        public void shoot() {
            if (isDamaged()) {
                return;
            }
            Vector2 trajectory = new Vector2(15f * (facing == Facing.LEFT ? -PPM : PPM), 0f);
            Vector2 spawn = getComponent(BodyComponent.class).getCenter().add(facing == Facing.LEFT ? -15f : 15f, 1f);
            if (getComponent(BehaviorComponent.class).is(WALL_SLIDING)) {
                spawn.y += 3.5f;
            } else if (!getComponent(BodyComponent.class).is(BodySense.FEET_ON_GROUND)) {
                spawn.y += 4.5f;
            }
            TextureRegion yellowBullet = assetLoader.getAsset(TextureAssets.OBJECTS_TEXTURE_ATLAS, TextureAtlas.class).findRegion("YellowBullet");
            TestBullet bullet = new TestBullet(this, trajectory, spawn, yellowBullet, assetLoader, entitiesAndSystemsManager);
            entitiesAndSystemsManager.addEntity(bullet);
            shootCoolDownTimer.reset();
            shootAnimationTimer.reset();
        }

        private UpdatableComponent defineUpdatableComponent() {
            UpdatableComponent updatableComponent = new UpdatableComponent();
            updatableComponent.setUpdatable(delta -> {
                priorFocusBox.set(currentFocusBox);
                UtilMethods.setBottomCenterToPoint(currentFocusBox, UtilMethods.bottomCenterPoint(getComponent(BodyComponent.class).getCollisionBox()));
                damageTimer.update(delta);
                if (damageTimer.isJustFinished()) {
                    damageRecoveryTimer.reset();
                }
                wallJumpImpetusTimer.update(delta);
                shootCoolDownTimer.update(delta);
                shootAnimationTimer.update(delta);
                setCharging(megaBusterChargingTimer.isFinished());
            });
            return updatableComponent;
        }

        private ControllerComponent defineControllerComponent() {
            ControllerComponent controllerComponent = new ControllerComponent();
            controllerComponent.addControllerAdapter(ControllerButton.LEFT, new ControllerAdapter() {

                @Override
                public void onPressContinued(float delta) {
                    if (isDamaged()) {
                        return;
                    }
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                    if (wallJumpImpetusTimer.isFinished()) {
                        setFacing(behaviorComponent.is(WALL_SLIDING) ? Facing.RIGHT : Facing.LEFT);
                    }
                    behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                    if (bodyComponent.getVelocity().x > -MegamanRun.RUN_SPEED * PPM) {
                        bodyComponent.applyImpulse(-PPM * 50f * delta, 0f);
                    }
                }

                @Override
                public void onJustReleased(float delta) {
                    getComponent(BehaviorComponent.class).setIsNot(RUNNING);
                }

            });
            controllerComponent.addControllerAdapter(ControllerButton.RIGHT, new ControllerAdapter() {

                @Override
                public void onPressContinued(float delta) {
                    if (isDamaged()) {
                        return;
                    }
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                    if (wallJumpImpetusTimer.isFinished()) {
                        setFacing(behaviorComponent.is(WALL_SLIDING) ? Facing.LEFT : Facing.RIGHT);
                    }
                    behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                    if (bodyComponent.getVelocity().x < MegamanRun.RUN_SPEED * PPM) {
                        bodyComponent.applyImpulse(PPM * 50f * delta, 0f);
                    }
                }

                @Override
                public void onJustReleased(float delta) {
                    getComponent(BehaviorComponent.class).setIsNot(RUNNING);
                }

            });
            controllerComponent.addControllerAdapter(ControllerButton.X, new ControllerAdapter() {

                @Override
                public void onPressContinued(float delta) {
                    if (isDamaged()) {
                        megaBusterChargingTimer.reset();
                        return;
                    }
                    megaBusterChargingTimer.update(delta);
                }

                @Override
                public void onJustReleased(float delta) {
                    BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                    if (shootCoolDownTimer.isFinished() && !behaviorComponent.is(GROUND_SLIDING) && !behaviorComponent.is(AIR_DASHING)) {
                        shoot();
                    }
                    megaBusterChargingTimer.reset();
                }

            });
            return controllerComponent;
        }

        private BehaviorComponent defineBehaviorComponent() {
            BehaviorComponent behaviorComponent = new BehaviorComponent();
            Behavior wallSlide = new Behavior() {

                @Override
                protected boolean evaluate(float delta) {
                    if (isDamaged()) {
                        return false;
                    }
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    return wallJumpImpetusTimer.isFinished() && !bodyComponent.is(BodySense.FEET_ON_GROUND) &&
                            ((bodyComponent.is(BodySense.TOUCHING_WALL_SLIDE_LEFT) && controller.isPressed(ControllerButton.LEFT)) ||
                                    (bodyComponent.is(BodySense.TOUCHING_WALL_SLIDE_RIGHT) && controller.isPressed(ControllerButton.RIGHT)));
                }

                @Override
                protected void init() {
                    behaviorComponent.setIs(WALL_SLIDING);
                    setAButtonTask(TestPlayer.AButtonTask.JUMP);
                }

                @Override
                protected void act(float delta) {
                    getComponent(BodyComponent.class).applyResistanceY(1.25f);
                }

                @Override
                protected void end() {
                    behaviorComponent.setIsNot(WALL_SLIDING);
                    setAButtonTask(TestPlayer.AButtonTask.AIR_DASH);
                }

            };
            behaviorComponent.addBehavior(wallSlide);
            // Jump
            Behavior jump = new Behavior() {

                @Override
                protected boolean evaluate(float delta) {
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    if (isDamaged() || controller.isPressed(ControllerButton.DOWN) || bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                        return false;
                    }
                    return behaviorComponent.is(JUMPING) ?
                            // case 1
                            bodyComponent.getVelocity().y >= 0f && controller.isPressed(ControllerButton.A) :
                            // case 2
                            aButtonTask == TestPlayer.AButtonTask.JUMP && controller.isJustPressed(ControllerButton.A) &&
                                    (bodyComponent.is(BodySense.FEET_ON_GROUND) || behaviorComponent.is(WALL_SLIDING));
                }

                @Override
                protected void init() {
                    behaviorComponent.setIs(JUMPING);
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    if (behaviorComponent.is(WALL_SLIDING)) {
                        bodyComponent.applyImpulse((isFacing(Facing.LEFT) ? -1f : 1f) * 15f * PPM, 32f * PPM);
                        wallJumpImpetusTimer.reset();
                    } else {
                        bodyComponent.applyImpulse(0f, 18f * PPM);
                    }
                }

                @Override
                protected void act(float delta) {}

                @Override
                protected void end() {
                    behaviorComponent.setIsNot(JUMPING);
                    getComponent(BodyComponent.class).getVelocity().y = 0f;
                }

            };
            behaviorComponent.addBehavior(jump);
            // Air dash
            Behavior airDash = new Behavior() {

                @Override
                protected boolean evaluate(float delta) {
                    if (isDamaged() || behaviorComponent.is(WALL_SLIDING) || getComponent(BodyComponent.class).is(BodySense.FEET_ON_GROUND) || airDashTimer.isFinished()) {
                        return false;
                    }
                    return behaviorComponent.is(AIR_DASHING) ? controller.isPressed(ControllerButton.A) :
                            controller.isJustPressed(ControllerButton.A) && getAButtonTask() == TestPlayer.AButtonTask.AIR_DASH;
                }

                @Override
                protected void init() {
                    getComponent(BodyComponent.class).setGravityOn(false);
                    behaviorComponent.setIs(BehaviorType.AIR_DASHING);
                    setAButtonTask(TestPlayer.AButtonTask.JUMP);
                }

                @Override
                protected void act(float delta) {
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    airDashTimer.update(delta);
                    bodyComponent.setVelocityY(0f);
                    if ((isFacing(Facing.LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                            (isFacing(Facing.RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT))) {
                        return;
                    }
                    float x = 12f * PPM;
                    if (isFacing(Facing.LEFT)) {
                        x *= -1f;
                    }
                    bodyComponent.setVelocityX(x);
                }

                @Override
                protected void end() {
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    airDashTimer.reset();
                    bodyComponent.setGravityOn(true);
                    behaviorComponent.setIsNot(BehaviorType.AIR_DASHING);
                    if (isFacing(Facing.LEFT)) {
                        bodyComponent.applyImpulse(-5f * PPM, 0f);
                    } else {
                        bodyComponent.applyImpulse(5f * PPM, 0f);
                    }
                }
            };
            behaviorComponent.addBehavior(airDash);
            // Ground slide
            Behavior groundSlide = new Behavior() {

                @Override
                protected boolean evaluate(float delta) {
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) && bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                        return true;
                    }
                    if (isDamaged() || !bodyComponent.is(BodySense.FEET_ON_GROUND) || groundSlideTimer.isFinished()) {
                        return false;
                    }
                    if (!behaviorComponent.is(BehaviorType.GROUND_SLIDING)) {
                        return controller.isPressed(ControllerButton.DOWN) && controller.isJustPressed(ControllerButton.A);
                    } else {
                        return controller.isPressed(ControllerButton.DOWN) && controller.isPressed(ControllerButton.A);
                    }
                }

                @Override
                protected void init() {
                    behaviorComponent.setIs(BehaviorType.GROUND_SLIDING);
                }

                @Override
                protected void act(float delta) {
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    groundSlideTimer.update(delta);
                    if (isDamaged() ||
                            (isFacing(Facing.LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                            (isFacing(Facing.RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT))) {
                        return;
                    }
                    float x = 12f * PPM;
                    if (isFacing(Facing.LEFT)) {
                        x *= -1f;
                    }
                    bodyComponent.setVelocityX(x);
                }

                @Override
                protected void end() {
                    BodyComponent bodyComponent = getComponent(BodyComponent.class);
                    groundSlideTimer.reset();
                    behaviorComponent.setIsNot(BehaviorType.GROUND_SLIDING);
                    if (isFacing(Facing.LEFT)) {
                        bodyComponent.applyImpulse(-5f * PPM, 0f);
                    } else {
                        bodyComponent.applyImpulse(5f * PPM, 0f);
                    }
                }
            };
            behaviorComponent.addBehavior(groundSlide);
            return behaviorComponent;
        }

        private BodyComponent defineBodyComponent() {
            BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
            bodyComponent.setPosition(3f * PPM, 3f * PPM);
            bodyComponent.setWidth(.8f * PPM);
            bodyComponent.setPreProcess(delta -> {
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (behaviorComponent.is(GROUND_SLIDING)) {
                    bodyComponent.setHeight(.45f * PPM);
                } else {
                    bodyComponent.setHeight(.95f * PPM);
                }
                if (bodyComponent.getVelocity().y < 0f && !bodyComponent.is(BodySense.FEET_ON_GROUND)) {
                    bodyComponent.setGravity(-60f * PPM);
                } else {
                    bodyComponent.setGravity(-20f * PPM);
                }
            });
            Fixture feet = new Fixture(this, FixtureType.FEET);
            feet.setSize(10f, 1f);
            feet.setOffset(0f, -PPM / 2f);
            bodyComponent.addFixture(feet);
            Fixture head = new Fixture(this, FixtureType.HEAD);
            head.setSize(7f, 2f);
            head.setOffset(0f, PPM / 2f);
            bodyComponent.addFixture(head);
            Fixture left = new Fixture(this, FixtureType.LEFT);
            left.setSize(1f, .25f * PPM);
            left.setOffset(-.45f * PPM, 0f);
            bodyComponent.addFixture(left);
            Fixture right = new Fixture(this, FixtureType.RIGHT);
            right.setSize(1f, .25f * PPM);
            right.setOffset(.45f * PPM, 0f);
            bodyComponent.addFixture(right);
            Fixture hitBox = new Fixture(this, FixtureType.HIT_BOX);
            hitBox.setSize(.8f * PPM, .5f * PPM);
            bodyComponent.addFixture(hitBox);
            return bodyComponent;
        }

        private DebugComponent defineDebugComponent() {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            DebugComponent debugComponent = new DebugComponent();
            debugComponent.addDebugHandle(bodyComponent::getCollisionBox, () -> Color.GREEN);
            bodyComponent.getFixtures().forEach(fixture -> debugComponent.addDebugHandle(fixture::getFixtureBox, () -> Color.GOLD));
            return debugComponent;
        }

        private SpriteComponent defineSpriteComponent() {
            SpriteComponent spriteComponent = new SpriteComponent();
            spriteComponent.getSprite().setSize(1.65f * PPM, 1.35f * PPM);
            spriteComponent.setSpriteUpdater(delta -> {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                Sprite sprite = spriteComponent.getSprite();
                if (damageTimer.isFinished() && !damageRecoveryTimer.isFinished()) {
                    damageRecoveryTimer.update(delta);
                    damageRecoveryBlinkTimer.update(delta);
                    sprite.setAlpha(recoveryBlink ? 0f : 1f);
                    if (damageRecoveryBlinkTimer.isFinished()) {
                        recoveryBlink = !recoveryBlink;
                        damageRecoveryBlinkTimer.reset();
                    }
                } 
                if (damageRecoveryTimer.isJustFinished()) {
                    sprite.setAlpha(1f);
                }
                if (behaviorComponent.is(WALL_SLIDING)) {
                    sprite.setFlip(isFacing(Facing.RIGHT), false);
                } else {
                    sprite.setFlip(isFacing(Facing.LEFT), false);
                }
                Vector2 bottomCenter = UtilMethods.bottomCenterPoint(bodyComponent.getCollisionBox());
                sprite.setCenterX(bottomCenter.x);
                sprite.setY(bottomCenter.y);
                if (behaviorComponent.is(GROUND_SLIDING)) {
                    sprite.translateY(-.035f * PPM);
                }
            });
            return spriteComponent;
        }

        private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
            Supplier<String> keySupplier = () -> {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (isDamaged()) {
                    return behaviorComponent.is(GROUND_SLIDING) ? "LayDownDamaged" : "Damaged";
                } else if (behaviorComponent.is(AIR_DASHING)) {
                    return "AirDash";
                } else if (behaviorComponent.is(GROUND_SLIDING)) {
                    return "GroundSlide";
                } else if (behaviorComponent.is(WALL_SLIDING)) {
                    return isShooting() ? "WallSlideShoot" : "WallSlide";
                } else if (behaviorComponent.is(JUMPING) || !bodyComponent.is(BodySense.FEET_ON_GROUND)) {
                    return isShooting() ? "JumpShoot" : "Jump";
                } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && behaviorComponent.is(RUNNING)) {
                    return isShooting() ? "RunShoot" : "Run";
                } else if (behaviorComponent.is(CLIMBING)) {
                    return isShooting() ? "ClimbShoot" : "Climb";
                } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && Math.abs(bodyComponent.getVelocity().x) > 3f) {
                    return isShooting() ? "SlipSlideShoot" : "SlipSlide";
                } else {
                    return isShooting() ? "StandShoot" : "Stand";
                }
            };
            Map<String, TimedAnimation> animations = new HashMap<>();
            animations.put("Climb", new TimedAnimation(textureAtlas.findRegion("Climb"), 2, .125f));
            animations.put("ClimbShoot", new TimedAnimation(textureAtlas.findRegion("ClimbShoot")));
            animations.put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
            animations.put("StandShoot", new TimedAnimation(textureAtlas.findRegion("StandShoot")));
            animations.put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 3, .05f));
            animations.put("LayDownDamaged", new TimedAnimation(textureAtlas.findRegion("LayDownDamaged"), 3, .05f));
            animations.put("Run", new TimedAnimation(textureAtlas.findRegion("Run"), 4, .125f));
            animations.put("RunShoot", new TimedAnimation(textureAtlas.findRegion("RunShoot"), 4, .125f));
            animations.put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
            animations.put("JumpShoot", new TimedAnimation(textureAtlas.findRegion("JumpShoot")));
            animations.put("WallSlide", new TimedAnimation(textureAtlas.findRegion("WallSlide")));
            animations.put("WallSlideShoot", new TimedAnimation(textureAtlas.findRegion("WallSlideShoot")));
            animations.put("GroundSlide", new TimedAnimation(textureAtlas.findRegion("GroundSlide")));
            animations.put("AirDash", new TimedAnimation(textureAtlas.findRegion("AirDash")));
            animations.put("SlipSlide", new TimedAnimation(textureAtlas.findRegion("SlipSlide")));
            animations.put("SlipSlideShoot", new TimedAnimation(textureAtlas.findRegion("SlipSlideShoot")));
            Animator animator = new Animator(keySupplier, animations);
            AnimationComponent animationComponent = new AnimationComponent(animator);
            return animationComponent;
        }

    }

    @Getter
    @Setter
    static class TestBullet implements Entity, CullOnOutOfGameCamBounds, CullOnLevelCamTrans {

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        private int damage;
        private Entity owner;
        private final Vector2 trajectory = new Vector2();
        private final Timer cullTimer = new Timer(.15f);

        private final IAssetLoader assetLoader;
        private final IEntitiesAndSystemsManager entitiesAndSystemsManager;

        public TestBullet(Entity owner, Vector2 trajectory, Vector2 spawn, TextureRegion textureRegion,
                          IAssetLoader assetLoader, IEntitiesAndSystemsManager entitiesAndSystemsManager) {
            this.owner = owner;
            this.trajectory.set(trajectory);
            this.assetLoader = assetLoader;
            this.entitiesAndSystemsManager = entitiesAndSystemsManager;
            addComponent(defineSpriteComponent(textureRegion));
            addComponent(defineBodyComponent(spawn));
            addComponent(new SoundComponent());
        }

        @Override
        public Rectangle getBoundingBox() {
            return getComponent(BodyComponent.class).getCollisionBox();
        }

        @Override
        public void onDeath() {
            SoundComponent soundComponent = getComponent(SoundComponent.class);
            soundComponent.request(new SoundRequest(THUMP_SOUND, false, Percentage.of(VolumeVals.HIGH_VOLUME)));
            TestDisintegration disintegration = new TestDisintegration(assetLoader, getComponent(BodyComponent.class).getCenter());
            entitiesAndSystemsManager.addEntity(disintegration);
        }

        private SpriteComponent defineSpriteComponent(TextureRegion textureRegion) {
            SpriteComponent spriteComponent = new SpriteComponent();
            Sprite sprite = spriteComponent.getSprite();
            sprite.setRegion(textureRegion);
            sprite.setSize(PPM * 1.25f, PPM * 1.25f);
            spriteComponent.setSpriteUpdater(delta -> {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                sprite.setCenter(bodyComponent.getCenter().x, bodyComponent.getCenter().y);
            });
            return spriteComponent;
        }

        private BodyComponent defineBodyComponent(Vector2 spawn) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
            bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
            bodyComponent.setSize(.1f * PPM, .1f * PPM);
            bodyComponent.setCenter(spawn.x, spawn.y);
            Fixture projectile = new Fixture(this, FixtureType.PROJECTILE);
            projectile.setSize(.1f * PPM, .1f * PPM);
            projectile.setCenter(spawn.x, spawn.y);
            bodyComponent.addFixture(projectile);
            Fixture damageBox = new Fixture(this, FixtureType.DAMAGE_BOX);
            damageBox.setSize(.1f * PPM, .1f * PPM);
            damageBox.setCenter(spawn.x, spawn.y);
            bodyComponent.addFixture(damageBox);
            return bodyComponent;
        }

    }

    @Getter
    @Setter
    static class TestDisintegration implements Entity {

        public static final float DISINTEGRATION_DURATION = .15f;

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        private final Timer timer = new Timer(DISINTEGRATION_DURATION);

        public TestDisintegration(IAssetLoader assetLoader, Vector2 center) {
            addComponent(defineBodyComponent(center));
            addComponent(defineAnimationComponent(assetLoader));
            addComponent(defineSpriteComponent(center));
            addComponent(defineUpdatableComponent());
        }

        @Override
        public void onDeath() {

        }

        private BodyComponent defineBodyComponent(Vector2 center) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
            bodyComponent.setSize(PPM, PPM);
            bodyComponent.setCenter(center);
            bodyComponent.setGravityOn(false);
            bodyComponent.setFriction(0f, 0f);
            return bodyComponent;
        }

        private SpriteComponent defineSpriteComponent(Vector2 center) {
            SpriteComponent spriteComponent = new SpriteComponent();
            spriteComponent.getSprite().setSize(PPM, PPM);
            spriteComponent.getSprite().setCenter(center.x, center.y);
            return spriteComponent;
        }

        private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
            Map<String, TimedAnimation> animations = Map.of("Disintegration", new TimedAnimation(
                    assetLoader.getAsset(TextureAssets.DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class).findRegion("Disintegration"), 3, .1f));
            Animator animator = new Animator(() -> "Disintegration", animations);
            return new AnimationComponent(animator);
        }

        private UpdatableComponent defineUpdatableComponent() {
            return new UpdatableComponent(delta -> {
                timer.update(delta);
                if (timer.isFinished()) {
                    setDead(true);
                }
            });
        }

    }

    @Getter
    @Setter
    static class TestBlock implements Entity {

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        public TestBlock(Rectangle bounds, boolean affectedByResistance, boolean gravityOn, Vector2 friction) {
            addComponent(defineBodyComponent(bounds, affectedByResistance, gravityOn, friction));
            addComponent(defineDebugComponent());
        }

        private BodyComponent defineBodyComponent(Rectangle bounds, boolean affectedByResistance, boolean gravityOn, Vector2 friction) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
            bodyComponent.set(bounds);
            bodyComponent.setFriction(friction);
            bodyComponent.setGravityOn(gravityOn);
            bodyComponent.setAffectedByResistance(affectedByResistance);
            Fixture block = new Fixture(this, FixtureType.BLOCK);
            block.set(bodyComponent.getCollisionBox());
            bodyComponent.addFixture(block);
            Fixture wallSlideLeft = new Fixture(this, FixtureType.WALL_SLIDE_SENSOR);
            wallSlideLeft.setSize(3f, bounds.height - PPM / 3f);
            wallSlideLeft.setOffset(-bounds.width / 2f, 0f);
            bodyComponent.addFixture(wallSlideLeft);
            Fixture wallSlideRight = new Fixture(this,FixtureType.WALL_SLIDE_SENSOR);
            wallSlideRight.setSize(3f, bounds.height - PPM / 3f);
            wallSlideRight.setOffset(bounds.width / 2f, 0f);
            bodyComponent.addFixture(wallSlideRight);
            return bodyComponent;
        }

        private DebugComponent defineDebugComponent() {
            DebugComponent debugComponent = new DebugComponent();
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            debugComponent.addDebugHandle(bodyComponent::getCollisionBox, () -> Color.BLUE);
            bodyComponent.getFixtures().forEach(fixture -> debugComponent.addDebugHandle(fixture::getFixtureBox, () -> Color.GOLD));
            return debugComponent;
        }

    }

    static class TestWorldContactListener implements WorldContactListener {

        @Override
        public void beginContact(Contact contact, float delta) {
            if (contact.acceptMask(FixtureType.LEFT, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_LEFT);
            } else if (contact.acceptMask(FixtureType.RIGHT, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_RIGHT);
            } else if (contact.acceptMask(FixtureType.LEFT, FixtureType.WALL_SLIDE_SENSOR)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_LEFT);
            } else if (contact.acceptMask(FixtureType.RIGHT, FixtureType.WALL_SLIDE_SENSOR)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_RIGHT);
            } else if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
                Entity entity = contact.maskFirstEntity();
                entity.getComponent(BodyComponent.class).setIs(BodySense.FEET_ON_GROUND);
                if (entity instanceof TestPlayer testPlayer) {
                    testPlayer.setAButtonTask(AButtonTask.JUMP);
                }
            } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIs(BodySense.HEAD_TOUCHING_BLOCK);
            } else if (contact.acceptMask(FixtureType.DAMAGE_BOX, FixtureType.HIT_BOX) &&
                    contact.maskFirstEntity() instanceof Damager damager &&
                    contact.maskSecondEntity() instanceof Damageable damageable &&
                    damageable.canBeDamagedBy(damager)) {
                damageable.takeDamageFrom(damager.getClass());
                damager.onDamageInflictedTo(damageable.getClass());
            } else if (contact.acceptMask(FixtureType.PROJECTILE, FixtureType.BLOCK) &&
                    contact.maskFirstEntity() instanceof TestBullet testBullet) {
                testBullet.setDead(true);
            }
        }

        @Override
        public void continueContact(Contact contact, float delta) {
            if (contact.acceptMask(FixtureType.LEFT, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_LEFT);
            } else if (contact.acceptMask(FixtureType.RIGHT, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_RIGHT);
            } else if (contact.acceptMask(FixtureType.LEFT, FixtureType.WALL_SLIDE_SENSOR)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_LEFT);
            } else if (contact.acceptMask(FixtureType.RIGHT, FixtureType.WALL_SLIDE_SENSOR)) {
                contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_RIGHT);
            } else if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
                Entity entity = contact.maskFirstEntity();
                entity.getComponent(BodyComponent.class).setIs(BodySense.FEET_ON_GROUND);
                if (entity instanceof TestPlayer testPlayer) {
                    testPlayer.setAButtonTask(AButtonTask.JUMP);
                }
            } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
                contact.maskFirstEntity().getComponent(BodyComponent.class).setIs(BodySense.HEAD_TOUCHING_BLOCK);
            } else if (contact.acceptMask(FixtureType.DAMAGE_BOX, FixtureType.HIT_BOX) &&
                    contact.maskFirstEntity() instanceof Damager damager &&
                    contact.maskSecondEntity() instanceof Damageable damageable &&
                    damageable.canBeDamagedBy(damager)) {
                damageable.takeDamageFrom(damager.getClass());
                damager.onDamageInflictedTo(damageable.getClass());
            }
        }

        @Override
        public void endContact(Contact contact, float delta) {
            if (contact.acceptMask(FixtureType.LEFT, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIsNot(BodySense.TOUCHING_BLOCK_LEFT);
            } else if (contact.acceptMask(FixtureType.RIGHT, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIsNot(BodySense.TOUCHING_BLOCK_RIGHT);
            } else if (contact.acceptMask(FixtureType.LEFT, FixtureType.WALL_SLIDE_SENSOR)) {
                contact.maskFirstBody().setIsNot(BodySense.TOUCHING_WALL_SLIDE_LEFT);
            } else if (contact.acceptMask(FixtureType.RIGHT, FixtureType.WALL_SLIDE_SENSOR)) {
                contact.maskFirstBody().setIsNot(BodySense.TOUCHING_WALL_SLIDE_RIGHT);
            } else if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIsNot(BodySense.FEET_ON_GROUND);
                if (contact.maskFirstEntity() instanceof TestPlayer testPlayer) {
                    testPlayer.setAButtonTask(AButtonTask.AIR_DASH);
                }
            } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
                contact.maskFirstBody().setIsNot(BodySense.HEAD_TOUCHING_BLOCK);
            }
        }

    }

    static class TestController implements IController {

        private final Map<ControllerButton, ControllerButtonStatus> controllerButtons = new HashMap<>() {{
            for (ControllerButton controllerButton : ControllerButton.values()) {
                put(controllerButton, IS_RELEASED);
            }
        }};

        @Override
        public boolean isJustPressed(ControllerButton controllerButton) {
            return controllerButtons.get(controllerButton) == IS_JUST_PRESSED;
        }

        @Override
        public boolean isPressed(ControllerButton controllerButton) {
            return controllerButtons.get(controllerButton) == IS_JUST_PRESSED || controllerButtons.get(controllerButton) == IS_PRESSED;
        }

        @Override
        public boolean isJustReleased(ControllerButton controllerButton) {
            return controllerButtons.get(controllerButton) == IS_JUST_RELEASED;
        }

        @Override
        public void updateController() {
            for (ControllerButton controllerButton : ControllerButton.values()) {
                ControllerButtonStatus status = controllerButtons.get(controllerButton);
                boolean isControllerButtonPressed = isControllerConnected() ?
                        isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                        isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
                if (isControllerButtonPressed) {
                    if (status == IS_RELEASED || status == IS_JUST_RELEASED) {
                        controllerButtons.replace(controllerButton, IS_JUST_PRESSED);
                    } else {
                        controllerButtons.replace(controllerButton, IS_PRESSED);
                    }
                } else if (status == IS_RELEASED || status == IS_JUST_RELEASED) {
                    controllerButtons.replace(controllerButton, IS_RELEASED);
                } else {
                    controllerButtons.replace(controllerButton, IS_JUST_RELEASED);
                }
            }
        }

    }

    private AssetLoader assetLoader;
    private TestController testController;
    private MessageDispatcher messageDispatcher;
    private EntitiesAndSystemsManager entitiesAndSystemsManager;

    private Viewport uiViewport;
    private Viewport playgroundViewport;
    private LevelCameraFocusable focusable;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private FontHandle message;

    @Override
    public void show() {
        testController = new TestController();
        messageDispatcher = new MessageDispatcher();
        entitiesAndSystemsManager = new EntitiesAndSystemsManager();
        spriteBatch = new SpriteBatch();
        message = new FontHandle("Megaman10Font.ttf", 6);
        message.getFont().setColor(Color.WHITE);
        message.setText("NULL");
        message.getPosition().set(-VIEW_WIDTH * PPM / 3f, -VIEW_HEIGHT * PPM / 4f);
        shapeRenderer = new ShapeRenderer();
        uiViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        assetLoader = new AssetLoader();
        entitiesAndSystemsManager.addSystem(new WorldSystem(new TestWorldContactListener(), WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP));
        entitiesAndSystemsManager.addSystem(new UpdatableSystem());
        entitiesAndSystemsManager.addSystem(new ControllerSystem(testController));
        entitiesAndSystemsManager.addSystem(new BehaviorSystem());
        entitiesAndSystemsManager.addSystem(new SpriteSystem((OrthographicCamera) playgroundViewport.getCamera(), spriteBatch));
        entitiesAndSystemsManager.addSystem(new AnimationSystem());
        entitiesAndSystemsManager.addSystem(new SoundSystem(assetLoader));
        entitiesAndSystemsManager.addSystem(new DebugSystem(shapeRenderer, (OrthographicCamera) playgroundViewport.getCamera()));
        TestPlayer player = new TestPlayer(testController, assetLoader, entitiesAndSystemsManager);
        focusable = player;
        entitiesAndSystemsManager.addEntity(player);
        TestDamager damager1 = new TestDamager(new Rectangle(3f * PPM, 3f * PPM, 3f * PPM, PPM));
        entitiesAndSystemsManager.addEntity(damager1);
        TestDamager damager2 = new TestDamager(new Rectangle(5f * PPM, 0f, PPM, 5f * PPM));
        entitiesAndSystemsManager.addEntity(damager2);
        TestBlock testBlock1 = new TestBlock(new Rectangle(0f, 0f, 20f * PPM, PPM), false, false, new Vector2(.035f, 0f));
        entitiesAndSystemsManager.addEntity(testBlock1);
        TestBlock testBlock2 = new TestBlock(new Rectangle(23f * PPM, 0f, 20f * PPM, PPM), false, false, new Vector2(.035f, 0f));
        entitiesAndSystemsManager.addEntity(testBlock2);
        TestBlock testBlock3 = new TestBlock(new Rectangle(10f * PPM, 2f * PPM, PPM, 30f * PPM), false, false, new Vector2(.035f, 0f));
        entitiesAndSystemsManager.addEntity(testBlock3);
        TestBlock testBlock4 = new TestBlock(new Rectangle(4f * PPM, 1.65f * PPM, 10f * PPM, PPM), false, false, new Vector2(.035f, 0f));
        entitiesAndSystemsManager.addEntity(testBlock4);
    }

    @Override
    public void render(float delta) {
        testController.updateController();
        entitiesAndSystemsManager.updateSystems(delta);
        messageDispatcher.updateMessageDispatcher(delta);
        Vector2 interpolatedCenter = UtilMethods.interpolate(focusable.getPriorFocus(), focusable.getCurrentFocus(), delta);
        playgroundViewport.getCamera().position.x = interpolatedCenter.x;
        playgroundViewport.getCamera().position.y = interpolatedCenter.y;
        playgroundViewport.apply();
        uiViewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }
    
}
