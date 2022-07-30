package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.DamageNegotiation;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.ENEMIES_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;

@Setter
@Getter
public class TestSniperJoe extends Entity implements Faceable, Damager, Damageable {

    private static final float BULLET_SPEED = 15f;

    private final IAssetLoader assetLoader;
    private final Supplier<TestPlayer> playerSupplier;
    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations = new HashMap<>();

    private final Timer damageTimer = new Timer(1f);
    private final Timer shieldedTimer = new Timer(1.75f);
    private final Timer shootingTimer = new Timer(1.5f, new TimeMarkedRunnable(.15f, this::shoot),
            new TimeMarkedRunnable(.75f, this::shoot), new TimeMarkedRunnable(1.35f, this::shoot));

    private boolean isShielded = true;
    private Facing facing;

    public TestSniperJoe(IEntitiesAndSystemsManager entitiesAndSystemsManager, IAssetLoader assetLoader,
                         Supplier<TestPlayer> playerSupplier, Vector2 spawn) {
        this.assetLoader = assetLoader;
        this.playerSupplier = playerSupplier;
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        defineDamageNegotiations();
        addComponent(new CullOnCamTransComponent());
        addComponent(new CullOutOfCamBoundsComponent(
                () -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
        addComponent(defineUpdatableComponent(playerSupplier));
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(assetLoader.getAsset(ENEMIES_TEXTURE_ATLAS, TextureAtlas.class)));
        addComponent(defineBodyComponent(spawn));
        addComponent(defineHealthComponent());
        addComponent(defineDebugComponent());
        damageTimer.setToEnd();
        shieldedTimer.setToEnd();
        shootingTimer.setToEnd();
    }

    private void shoot() {
        Vector2 trajectory = new Vector2(PPM * (isFacing(Facing.F_LEFT) ? -BULLET_SPEED : BULLET_SPEED), 0f);
        Vector2 spawn = getComponent(BodyComponent.class).getCenter().cpy().add(
                (isFacing(Facing.F_LEFT) ? -5f : 5f), -3.25f);
        TestBullet bullet = new TestBullet(this, trajectory, spawn, assetLoader, entitiesAndSystemsManager);
        entitiesAndSystemsManager.addEntity(bullet);
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyShoot.mp3")).play();
    }

    private void explode() {
        entitiesAndSystemsManager.addEntity(new TestExplosion(
                assetLoader, getComponent(BodyComponent.class).getCenter()));
        Gdx.audio.newSound(Gdx.files.internal("sounds/Explosion.mp3")).play();
    }

    private void setShielded(boolean isShielded) {
        this.isShielded = isShielded;
        Timer behaviorTimer = isShielded ? shieldedTimer : shootingTimer;
        behaviorTimer.reset();
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return damageNegotiations.keySet();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        DamageNegotiation damageNegotiation = damageNegotiations.get(damager.getClass());
        damageTimer.reset();
        damageNegotiation.runOnDamage();
        getComponent(HealthComponent.class).sub(damageNegotiation.damage());
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished();
    }

    private void defineDamageNegotiations() {
        damageNegotiations.put(TestBullet.class, new DamageNegotiation(5));
        damageNegotiations.put(TestFireball.class, new DamageNegotiation(15));
    }

    private HealthComponent defineHealthComponent() {
        return new HealthComponent(30, this::explode);
    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> playerSupplier) {
        return new UpdatableComponent(delta -> {
            // recovery
            damageTimer.update(delta);
            // facing
            setFacing(Math.round(playerSupplier.get().getComponent(BodyComponent.class).getPosition().x) <
                    Math.round(getComponent(BodyComponent.class).getPosition().x) ? Facing.F_LEFT : Facing.F_RIGHT);
            // behavior timer
            Timer behaviorTimer = isShielded ? shieldedTimer : shootingTimer;
            behaviorTimer.update(delta);
            if (behaviorTimer.isFinished()) {
                setShielded(!isShielded);
            }
        });
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.25f * PPM, 1.25f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(Facing.F_RIGHT);
            }

        });
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        Supplier<String> keySupplier = () -> isShielded ? "Shielded" : "Shooting";
        Map<String, TimedAnimation> timedAnimations = new HashMap<>() {{
            put("Shooting", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShooting")));
            put("Shielded", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShielded")));
        }};
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setSize(PPM, 1.5f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setGravity(-50f * PPM);
        // hit box
        Fixture hitBox = new Fixture(this, FixtureType.DAMAGEABLE_BOX);
        hitBox.setSize(.75f * PPM, 1.15f * PPM);
        bodyComponent.addFixture(hitBox);
        // damage Box
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGER_BOX);
        damageBox.setCenter(.75f * PPM, 1.25f * PPM);
        bodyComponent.addFixture(damageBox);
        // shield
        Fixture shield = new Fixture(this, FixtureType.SHIELD);
        shield.putUserData("reflectDir", "straight");
        shield.setSize(.15f * PPM, .85f * PPM);
        bodyComponent.addFixture(shield);
        // body pre-process
        bodyComponent.setPreProcess(delta -> {
            if (isShielded) {
                hitBox.setOffset(isFacing(Facing.F_LEFT) ? 3f : -3f, 0f);
                shield.setOffset(isFacing(Facing.F_LEFT) ? -5f : 5f, 0f);
            } else {
                hitBox.setOffset(0f, 0f);
            }
            shield.setActive(isShielded);
        });
        return bodyComponent;
    }

    private DebugRectComponent defineDebugComponent() {
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
                debugRectComponent.addDebugHandle(fixture::getFixtureBox, () -> {
                    if (fixture.getFixtureType() == FixtureType.SHIELD) {
                        return fixture.isActive() ? Color.GREEN : Color.GRAY;
                    }
                    return Color.BLUE;
                }));
        return debugRectComponent;
    }

}
