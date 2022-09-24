package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.utils.enums.Position;
import com.game.utils.objects.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.game.constants.TextureAsset.ENEMIES_1;
import static com.game.constants.ViewVals.PPM;
import static com.game.constants.SoundAsset.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static java.lang.Math.*;

public class SniperJoe extends AbstractEnemy implements Faceable {

    private static final float DAMAGE_DURATION = .15f;
    private static final float BULLET_SPEED = 7.5f;

    private final Timer shieldedTimer = new Timer(1.75f);
    private final Timer shootingTimer = new Timer(1.5f, new TimeMarkedRunnable(.15f, this::shoot),
            new TimeMarkedRunnable(.75f, this::shoot), new TimeMarkedRunnable(1.35f, this::shoot));

    @Setter
    @Getter
    private Facing facing;
    private boolean isShielded = true;

    public SniperJoe(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, DAMAGE_DURATION);
        addComponent(spriteComponent());
        addComponent(animationComponent());
        addComponent(updatableComponent());
        addComponent(bodyComponent(spawn));
        addComponent(shapeComponent());
        shieldedTimer.setToEnd();
        shootingTimer.setToEnd();
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return new HashMap<>() {{
            put(Bullet.class, new DamageNegotiation(5));
            put(Fireball.class, new DamageNegotiation(15));
            put(ChargedShot.class, new DamageNegotiation(damager ->
                    ((ChargedShot) damager).isFullyCharged() ? 15 : 10));
            put(ChargedShotDisintegration.class, new DamageNegotiation(damager ->
                    ((ChargedShotDisintegration) damager).isFullyCharged() ? 15 : 10));
        }};
    }

    private void shoot() {
        Vector2 trajectory = new Vector2(PPM * (isFacing(F_LEFT) ? -BULLET_SPEED : BULLET_SPEED), 0f);
        Vector2 spawn = getComponent(BodyComponent.class).getCenter().cpy().add(
                (isFacing(F_LEFT) ? -.2f : .2f) * PPM, -.15f * PPM);
        gameContext.addEntity(new Bullet(gameContext, this, trajectory, spawn));
        getComponent(SoundComponent.class).requestSound(ENEMY_BULLET_SOUND);
    }

    private void setShielded(boolean isShielded) {
        this.isShielded = isShielded;
        (isShielded ? shieldedTimer : shootingTimer).reset();
    }

    private ShapeComponent shapeComponent() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        ShapeHandle shapeHandle1 = new ShapeHandle();
        shapeHandle1.setShapeSupplier(bodyComponent::getCollisionBox);
        shapeHandle1.setColorSupplier(() -> RED);
        ShapeHandle shapeHandle2 = new ShapeHandle();
        Fixture shield = bodyComponent.getFirstMatchingFixture(SHIELD).orElseThrow();
        shapeHandle2.setShapeSupplier(shield::getFixtureShape);
        shapeHandle2.setColorSupplier(() -> shield.isActive() ? BLUE : ORANGE);
        return new ShapeComponent(shapeHandle1, shapeHandle2);
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                setFacing(round(getMegaman().getComponent(BodyComponent.class).getPosition().x) <
                        round(getComponent(BodyComponent.class).getPosition().x) ? F_LEFT : F_RIGHT);
                Timer behaviorTimer = isShielded ? shieldedTimer : shootingTimer;
                behaviorTimer.update(delta);
                if (behaviorTimer.isFinished()) {
                    setShielded(!isShielded);
                }
            }
        });
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.35f * PPM, 1.35f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(F_LEFT);
            }

        });
    }

    private AnimationComponent animationComponent() {
        Supplier<String> keySupplier = () -> isShielded ? "Shielded" : "Shooting";
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_1.getSrc(), TextureAtlas.class);
        Map<String, TimedAnimation> timedAnimations = Map.of(
            "Shooting", new TimedAnimation(textureAtlas.findRegion("SniperJoe/Shooting")),
            "Shielded", new TimedAnimation(textureAtlas.findRegion("SniperJoe/Shielded")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setGravity(-PPM * .5f);
        bodyComponent.setSize(PPM, 1.25f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        // hit box
        Fixture hitBox = new Fixture(this, new Rectangle(0f, 0f, .75f * PPM, 1.15f * PPM), DAMAGEABLE);
        bodyComponent.addFixture(hitBox);
        // damage Box
        Fixture damageBox = new Fixture(this, new Rectangle(0f, 0f, .75f * PPM, 1.25f * PPM), DAMAGER);
        bodyComponent.addFixture(damageBox);
        // shield
        Fixture shield = new Fixture(this, new Rectangle(0f, 0f, .4f * PPM, .9f * PPM), SHIELD);
        shield.putUserData("reflectDir", "straight");
        bodyComponent.addFixture(shield);
        // body pre-process
        bodyComponent.setPreProcess(delta -> {
            if (isShielded) {
                hitBox.setOffset(isFacing(F_LEFT) ? .2f * PPM : -2f * PPM, 0f);
                shield.setOffset(isFacing(F_LEFT) ? -PPM / 3f: PPM / 3f, 0f);
            } else {
                hitBox.setOffset(0f, 0f);
            }
            shield.setActive(isShielded);
        });
        return bodyComponent;
    }

}
