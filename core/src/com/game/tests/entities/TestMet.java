package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import com.game.debugging.DebugComponent;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.health.HealthComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Position;
import com.game.utils.Timer;
import com.game.utils.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.TextureAssets.OBJECTS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class TestMet implements IEntity, Faceable, Damager, Damageable, CullOnLevelCamTrans, CullOnOutOfCamBounds {

    enum MetBehavior {
        SHIELDING, POP_UP, RUNNING, PANIC
    }

    private MetBehavior metBehavior;
    private final Map<MetBehavior, Timer> metBehaviorTimers = new EnumMap<>(MetBehavior.class) {{
        put(MetBehavior.SHIELDING, new Timer(1.15f));
        put(MetBehavior.POP_UP, new Timer(.5f));
        put(MetBehavior.RUNNING, new Timer(.5f));
        put(MetBehavior.PANIC, new Timer(1f));
    }};
    private final IAssetLoader assetLoader;
    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Set<Class<? extends Damager>> damagerMaskSet = Set.of(TestBullet.class);

    private final Timer damageTimer = new Timer(.25f);
    private final Timer blinkTimer = new Timer(.05f);
    private final Timer cullTimer = new Timer(4f);

    private boolean dead;
    private Facing facing;

    public TestMet(IEntitiesAndSystemsManager entitiesAndSystemsManager, IAssetLoader assetLoader,
                   Supplier<TestPlayer> megamanSupplier, Vector2 spawn) {
        System.out.println("Met spawn: " + spawn);
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        this.assetLoader = assetLoader;
        addComponent(new HealthComponent(100));
        addComponent(defineUpdatableComponent(megamanSupplier));
        addComponent(defineBodyComponent(spawn));
        addComponent(defineDebugComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(assetLoader.getAsset(MET_TEXTURE_ATLAS, TextureAtlas.class)));
        setMetBehavior(MetBehavior.SHIELDING);
        damageTimer.setToEnd();
    }

    @Override
    public Rectangle getCullBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    @Override
    public void takeDamageFrom(Class<? extends Damager> damagerClass) {
        damageTimer.reset();
        if (damagerClass.equals(TestBullet.class)) {
            getComponent(HealthComponent.class).translateHealth(-10);
            Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
        }
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished();
    }

    public void setMetBehavior(MetBehavior metBehavior) {
        this.metBehavior = metBehavior;
        metBehaviorTimers.values().forEach(Timer::reset);
        getComponent(BodyComponent.class).setVelocity(0f, 0f);
    }

    private void shoot() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        Vector2 trajectory = new Vector2((isFacing(Facing.RIGHT) ? 10f : -10f) * PPM, .5f * PPM);
        Vector2 spawn = bodyComponent.getCenter().cpy().add(isFacing(Facing.RIGHT) ? .5f : -.5f, -4f);
        TextureRegion yellowBullet = assetLoader.getAsset(OBJECTS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("YellowBullet");
        TestBullet bullet = new TestBullet(this, trajectory, spawn, yellowBullet,
                assetLoader, entitiesAndSystemsManager);
        entitiesAndSystemsManager.addEntity(bullet);
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyShoot.mp3")).play();
    }

    private void explode() {
        Vector2 explosion1Pos = getComponent(BodyComponent.class).getCenter();
        entitiesAndSystemsManager.addEntity(new TestExplosion(assetLoader, explosion1Pos));
        Vector2 explosion2Pos = getComponent(BodyComponent.class).getCenter().cpy().add(-.5f, .25f);
        entitiesAndSystemsManager.addEntity(new TestExplosion(assetLoader, explosion2Pos));
        Vector2 explosionPos3 = getComponent(BodyComponent.class).getCenter().cpy().add(.75f, -.75f);
        entitiesAndSystemsManager.addEntity(new TestExplosion(assetLoader, explosionPos3));
    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> megamanSupplier) {
        return new UpdatableComponent(delta -> {
            if (!damageTimer.isFinished()) {
                damageTimer.update(delta);
            }
            if (playerIsAttacking(megamanSupplier.get())) {
                setMetBehavior(MetBehavior.SHIELDING);
            }
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            bodyComponent.getFirstMatchingFixture(FixtureType.SHIELD).ifPresentOrElse(
                    shield -> shield.setActive(metBehavior == MetBehavior.SHIELDING),
                    () -> { throw new IllegalStateException(); });
            bodyComponent.getFirstMatchingFixture(FixtureType.HIT_BOX).ifPresentOrElse(
                    hitBox -> hitBox.setActive(metBehavior != MetBehavior.SHIELDING),
                    () -> { throw new IllegalStateException(); });
            switch (metBehavior) {
                case SHIELDING -> {
                    Timer shieldingTimer = metBehaviorTimers.get(MetBehavior.SHIELDING);
                    shieldingTimer.update(delta);
                    if (shieldingTimer.isFinished()) {
                        setMetBehavior(MetBehavior.POP_UP);
                    }
                }
                case POP_UP -> {
                    setFacing(Math.round(megamanSupplier.get().getComponent(BodyComponent.class).getPosition().x) <
                            Math.round(bodyComponent.getPosition().x) ? Facing.LEFT : Facing.RIGHT);
                    Timer popUpTimer = metBehaviorTimers.get(MetBehavior.POP_UP);
                    if (popUpTimer.isAtBeginning()) {
                        shoot();
                    }
                    popUpTimer.update(delta);
                    if (popUpTimer.isFinished()) {
                        setMetBehavior(MetBehavior.RUNNING);
                    }
                }
                case RUNNING -> {
                    Timer runningTimer = metBehaviorTimers.get(MetBehavior.RUNNING);
                    runningTimer.update(delta);
                    bodyComponent.setVelocity((isFacing(Facing.LEFT) ? -8f : 8f) * PPM, 0f);
                    if (runningTimer.isFinished()) {
                        setMetBehavior(MetBehavior.SHIELDING);
                    }
                }
                case PANIC -> {
                    Timer panicTimer = metBehaviorTimers.get(MetBehavior.PANIC);
                    metBehaviorTimers.get(MetBehavior.PANIC).update(delta);
                    if (panicTimer.isFinished()) {
                        explode();
                        setDead(true);
                    }
                }
            }
        });
    }

    private boolean playerIsAttacking(TestPlayer player) {
        BodyComponent metBody = getComponent(BodyComponent.class);
        BodyComponent playerBody = player.getComponent(BodyComponent.class);
        return player.isShooting() &&
                ((metBody.getPosition().x < playerBody.getPosition().x && player.isFacing(Facing.LEFT)) ||
                (metBody.getPosition().x > playerBody.getPosition().x && player.isFacing(Facing.RIGHT)));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.set(spawn.x, spawn.y, .75f * PPM, .75f * PPM);
        bodyComponent.setGravity(-50f * PPM);
        // shield
        Fixture shield = new Fixture(this, FixtureType.SHIELD);
        shield.setSize(PPM, 1.5f * PPM);
        bodyComponent.addFixture(shield);
        // hit box
        Fixture hitBox = new Fixture(this, FixtureType.HIT_BOX);
        hitBox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(hitBox);
        // damage box
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGE_BOX);
        damageBox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    private DebugComponent defineDebugComponent() {
        DebugComponent debugComponent = new DebugComponent();
        debugComponent.addDebugHandle(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> Color.GREEN);
        return debugComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(Facing.LEFT);
            }

        });
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        Supplier<String> keySupplier = () -> switch (metBehavior) {
            case RUNNING -> "Run";
            case POP_UP -> "PopUp";
            case PANIC -> "RunNaked";
            case SHIELDING -> "LayDown";
        };
        Map<String, TimedAnimation> timedAnimations = new HashMap<>() {{
            put("Run", new TimedAnimation(textureAtlas.findRegion("Run"), 2, .125f));
            put("PopUp", new TimedAnimation(textureAtlas.findRegion("PopUp")));
            put("RunNaked", new TimedAnimation(textureAtlas.findRegion("RunNaked"), 2, .1f));
            put("LayDown", new TimedAnimation(textureAtlas.findRegion("LayDown")));
        }};
        return new AnimationComponent(new Animator(keySupplier, timedAnimations));
    }

}
