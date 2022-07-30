package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.debugging.DebugRectComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.health.HealthComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.MET_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.world.BodySense.TOUCHING_HITBOX_LEFT;
import static com.game.world.BodySense.TOUCHING_HITBOX_RIGHT;

@Getter
@Setter
public class TestMet extends Entity implements Faceable, Damager, Damageable {

    private enum MetBehavior {
        SHIELDING, POP_UP, RUNNING, PANIC
    }

    private final Map<MetBehavior, Timer> metBehaviorTimers = new EnumMap<>(MetBehavior.class) {{
        put(MetBehavior.SHIELDING, new Timer(1.15f));
        put(MetBehavior.POP_UP, new Timer(.5f));
        put(MetBehavior.RUNNING, new Timer(.5f));
        put(MetBehavior.PANIC, new Timer(1f));
    }};
    private final IAssetLoader assetLoader;
    private final Supplier<TestPlayer> megamanSupplier;
    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Set<Class<? extends Damager>> damagerMaskSet = Set.of(
            TestBullet.class, TestChargedShot.class, TestFireball.class);
    private final Timer damageTimer = new Timer(.25f);
    private final Timer blinkTimer = new Timer(.05f);

    private MetBehavior metBehavior;
    private Facing facing;

    public TestMet(IEntitiesAndSystemsManager entitiesAndSystemsManager, IAssetLoader assetLoader,
                   Supplier<TestPlayer> megamanSupplier, Vector2 spawn) {
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        this.megamanSupplier = megamanSupplier;
        this.assetLoader = assetLoader;
        addComponent(new CullOnCamTransComponent());
        addComponent(new CullOutOfCamBoundsComponent(
                () -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
        addComponent(new HealthComponent(30, this::disintegrate));
        addComponent(defineUpdatableComponent(megamanSupplier));
        addComponent(defineBodyComponent(spawn));
        addComponent(defineDebugComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(assetLoader.getAsset(MET_TEXTURE_ATLAS, TextureAtlas.class)));
        setMetBehavior(MetBehavior.SHIELDING);
        damageTimer.setToEnd();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        damageTimer.reset();
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
        if (damager instanceof TestBullet) {
            getComponent(HealthComponent.class).sub(10);
        } else if (damager instanceof TestChargedShot) {
            getComponent(HealthComponent.class).sub(30);
        } else if (damager instanceof TestFireball) {
            getComponent(HealthComponent.class).sub(20);
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
        Vector2 trajectory = new Vector2((isFacing(Facing.F_RIGHT) ? 10f : -10f) * PPM, .5f * PPM);
        Vector2 spawn = bodyComponent.getCenter().cpy().add(isFacing(Facing.F_RIGHT) ? .5f : -.5f, -4f);
        TestBullet bullet = new TestBullet(this, trajectory, spawn, assetLoader, entitiesAndSystemsManager);
        entitiesAndSystemsManager.addEntity(bullet);
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyShoot.mp3")).play();
    }

    private void disintegrate() {
        entitiesAndSystemsManager.addEntity(new TestDisintegration(assetLoader,
                getComponent(BodyComponent.class).getCenter(), new Vector2(2f * PPM, 2f * PPM)));
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> megamanSupplier) {
        return new UpdatableComponent(delta -> {
            if (megamanSupplier.get().isDead()) {
                return;
            }
            damageTimer.update(delta);
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            bodyComponent.getFirstMatchingFixture(FixtureType.SHIELD).ifPresentOrElse(
                    shield -> shield.setActive(metBehavior == MetBehavior.SHIELDING),
                    () -> {throw new IllegalStateException();});
            bodyComponent.getFirstMatchingFixture(FixtureType.DAMAGEABLE_BOX).ifPresentOrElse(
                    hitBox -> hitBox.setActive(metBehavior != MetBehavior.SHIELDING),
                    () -> {throw new IllegalStateException();});
            switch (metBehavior) {
                case SHIELDING -> {
                    Timer shieldingTimer = metBehaviorTimers.get(MetBehavior.SHIELDING);
                    if (!playerIsAttacking(megamanSupplier.get())) {
                        shieldingTimer.update(delta);
                    }
                    if (shieldingTimer.isFinished()) {
                        setMetBehavior(MetBehavior.POP_UP);
                    }
                }
                case POP_UP -> {
                    setFacing(Math.round(megamanSupplier.get().getComponent(BodyComponent.class).getPosition().x) <
                            Math.round(bodyComponent.getPosition().x) ? Facing.F_LEFT : Facing.F_RIGHT);
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
                    bodyComponent.setVelocity((isFacing(Facing.F_LEFT) ? -8f : 8f) * PPM, 0f);
                    if (runningTimer.isFinished() ||
                            (isFacing(Facing.F_LEFT) && bodyComponent.is(TOUCHING_HITBOX_LEFT)) ||
                            (isFacing(Facing.F_RIGHT) && bodyComponent.is(TOUCHING_HITBOX_RIGHT))) {
                        setMetBehavior(MetBehavior.SHIELDING);
                    }
                }
                case PANIC -> {
                    Timer panicTimer = metBehaviorTimers.get(MetBehavior.PANIC);
                    metBehaviorTimers.get(MetBehavior.PANIC).update(delta);
                    if (panicTimer.isFinished()) {
                        disintegrate();
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
                ((metBody.getPosition().x < playerBody.getPosition().x && player.isFacing(Facing.F_LEFT)) ||
                        (metBody.getPosition().x > playerBody.getPosition().x && player.isFacing(Facing.F_RIGHT)));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setSize(.75f * PPM, .75f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setGravity(-50f * PPM);
        // left
        Fixture left = new Fixture(this, FixtureType.LEFT);
        left.setSize(3f, .75f * PPM);
        left.setOffset(-.65f * PPM, 0f);
        bodyComponent.addFixture(left);
        // right
        Fixture right = new Fixture(this, FixtureType.RIGHT);
        right.setSize(3f, .75f * PPM);
        right.setOffset(.65f * PPM, 0f);
        bodyComponent.addFixture(right);
        // shield
        Fixture shield = new Fixture(this, FixtureType.SHIELD);
        shield.putUserData("reflectDir", "up");
        shield.setSize(PPM, 1.5f * PPM);
        bodyComponent.addFixture(shield);
        // hit box
        Fixture hitBox = new Fixture(this, FixtureType.DAMAGEABLE_BOX);
        hitBox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(hitBox);
        // damage box
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGER_BOX);
        damageBox.setSize(.75f * PPM, .75f * PPM);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    private DebugRectComponent defineDebugComponent() {
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        debugRectComponent.addDebugHandle(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> Color.GREEN);
        return debugRectComponent;
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
                return isFacing(Facing.F_LEFT);
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
        Map<String, TimedAnimation> timedAnimations = Map.of(
                "Run", new TimedAnimation(textureAtlas.findRegion("Run"), 2, .125f),
                "PopUp", new TimedAnimation(textureAtlas.findRegion("PopUp")),
                "RunNaked", new TimedAnimation(textureAtlas.findRegion("RunNaked"), 2, .1f),
                "LayDown", new TimedAnimation(textureAtlas.findRegion("LayDown")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

}
