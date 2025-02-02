package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.blocks.Block;
import com.game.entities.contracts.Hitter;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.pathfinding.PathfindingComponent;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.*;
import static com.game.health.HealthVals.MAX_HEALTH;
import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.PPM;
import static com.game.entities.enemies.Bat.BatStatus.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.enums.Position.CENTER;
import static com.game.world.BodySense.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class Bat extends AbstractEnemy implements Hitter {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum BatStatus {
        
        HANGING("Hang"), OPEN_EYES("OpenEyes"), OPEN_WINGS("OpenWings"),
        FLYING_TO_ATTACK("Fly"), FLYING_TO_RETREAT("Fly");
        
        private final String regionName;
        
    }

    private static final float FLY_TO_ATTACK_SPEED = 3f;
    private static final float FLY_TO_RETREAT_SPEED = 8f;

    private final Timer hangTimer = new Timer(1.75f);
    private final Vector2 trajectory = new Vector2();
    private final Timer releaseFromPerchTimer = new Timer(.25f);

    private BatStatus currentStatus = HANGING;

    public Bat(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, .05f);
        addComponent(spriteComponent());
        addComponent(bodyComponent(spawn));
        addComponent(animationComponent());
        addComponent(updatableComponent());
        addComponent(pathfindingComponent());
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return new HashMap<>() {{
            put(Bullet.class, new DamageNegotiation(10));
            put(Fireball.class, new DamageNegotiation(MAX_HEALTH));
            put(ChargedShot.class, new DamageNegotiation(MAX_HEALTH));
            put(ChargedShotDisintegration.class, new DamageNegotiation(MAX_HEALTH));
        }};
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.isFixtureType(DAMAGEABLE) && fixture.getEntity().equals(getMegaman())) {
            setCurrentStatus(FLYING_TO_RETREAT);
        }
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (getCurrentStatus().equals(HANGING)) {
                    hangTimer.update(delta);
                    if (hangTimer.isFinished()) {
                        setCurrentStatus(OPEN_EYES);
                        hangTimer.reset();
                    }
                }
                if (equalsAny(getCurrentStatus(), OPEN_EYES, OPEN_WINGS)) {
                    releaseFromPerchTimer.update(delta);
                    if (releaseFromPerchTimer.isFinished()) {
                        if (getCurrentStatus().equals(OPEN_EYES)) {
                            setCurrentStatus(OPEN_WINGS);
                            releaseFromPerchTimer.reset();
                        } else {
                            setCurrentStatus(FLYING_TO_ATTACK);
                        }
                    }
                }
                if (getCurrentStatus().equals(FLYING_TO_RETREAT) && bodyComponent.is(HEAD_TOUCHING_BLOCK)) {
                    setCurrentStatus(HANGING);
                }
            }
        });
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteProcessor() {
            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(CENTER);
                return true;
            }
        });
    }

    private AnimationComponent animationComponent() {
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_1.getSrc(), TextureAtlas.class);
        Supplier<String> keySupplier = () -> currentStatus.getRegionName();
        Map<String, TimedAnimation> timedAnimations = Map.of(
                "Hang", new TimedAnimation(textureAtlas.findRegion("Bat/Hang")),
                "Fly", new TimedAnimation(textureAtlas.findRegion("Bat/Fly"), 2, .1f),
                "OpenEyes", new TimedAnimation(textureAtlas.findRegion("Bat/OpenEyes")),
                "OpenWings", new TimedAnimation(textureAtlas.findRegion("Bat/OpenWings")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(.5f * PPM, .75f * PPM);
        setTopCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        // head
        Fixture head = new Fixture(this, new Rectangle(0f, 0f, .5f * PPM, .175f * PPM), HEAD);
        head.setOffset(0f, .75f * PPM / 2f);
        bodyComponent.addFixture(head);
        // model
        Rectangle model = new Rectangle(0f, 0f, .75f * PPM, .75f * PPM);
        // hitter box
        Fixture hitterBox = new Fixture(this, new Rectangle(model), HITTER);
        bodyComponent.addFixture(hitterBox);
        // damageable box
        Fixture damageableBox = new Fixture(this, new Rectangle(model), DAMAGEABLE);
        bodyComponent.addFixture(damageableBox);
        // damager box
        Fixture damagerBox = new Fixture(this, new Rectangle(model), DAMAGER);
        bodyComponent.addFixture(damagerBox);
        // shield
        Fixture shield = new Fixture(this, new Rectangle(model), SHIELD);
        bodyComponent.addFixture(shield);
        // pre-process
        bodyComponent.setPreProcess(delta -> {
            shield.setActive(getCurrentStatus().equals(HANGING));
            damageableBox.setActive(!getCurrentStatus().equals(HANGING));
            if (getCurrentStatus().equals(FLYING_TO_ATTACK)) {
                bodyComponent.setVelocity(trajectory);
            } else if (getCurrentStatus().equals(FLYING_TO_RETREAT)) {
                bodyComponent.setVelocity(0f, FLY_TO_RETREAT_SPEED * PPM);
            } else {
                bodyComponent.setVelocity(Vector2.Zero);
            }
        });
        return bodyComponent;
    }

    private PathfindingComponent pathfindingComponent() {
        PathfindingComponent pathfindingComponent = new PathfindingComponent(
                () -> getComponent(BodyComponent.class).getCenter(),
                () -> topCenterPoint(getMegaman().getComponent(BodyComponent.class).getCollisionBox()),
                target -> {
                    Vector2 targetCenter = centerPoint(target);
                    Vector2 thisCenter = getComponent(BodyComponent.class).getCenter();
                    float angle = atan2(targetCenter.y - thisCenter.y, targetCenter.x - thisCenter.x);
                    trajectory.set(cos(angle), sin(angle)).scl(FLY_TO_ATTACK_SPEED * PPM);
                },
                target -> getComponent(BodyComponent.class).getCollisionBox().overlaps(target));
        pathfindingComponent.setDoAcceptPredicate(node ->
                node.getObjects().stream().noneMatch(o -> o instanceof Block || (o instanceof Bat && !o.equals(this))));
        Timer updateTimer = new Timer(.05f);
        pathfindingComponent.setDoRefreshPredicate(delta -> {
            updateTimer.update(delta);
            boolean isFinished = updateTimer.isFinished();
            if (updateTimer.isFinished()) {
                updateTimer.reset();
            }
            return isFinished;
        });
        return pathfindingComponent;
    }

}
