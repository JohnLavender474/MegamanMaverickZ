package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.game.GlobalKeys.COLLECTION;
import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.ENEMIES_1;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.entities.contracts.Facing.F_RIGHT;
import static com.game.health.HealthVals.MAX_HEALTH;
import static com.game.utils.enums.Position.CENTER;
import static com.game.world.BodyType.DYNAMIC;
import static com.game.world.FixtureType.*;

public class GapingFish extends AbstractEnemy implements Faceable {

    // private static final float SPEED = 2f;
    private static final float HORIZ_SPEED = 2f;
    private static final float VERT_SPEED = 1.25f;

    // private final Vector2 trajectory = new Vector2();
    private final Timer chompTimer = new Timer(1.25f, true);
    private final Set<Fixture> scannerSet = new HashSet<>();

    @Getter
    @Setter
    private Facing facing;

    public GapingFish(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, .35f);
        addComponent(bodyComponent(spawn));
        addComponent(updatableComponent());
        addComponent(spriteComponent());
        addComponent(animationComponent());
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return new HashMap<>() {{
            put(Bullet.class, new DamageNegotiation(10));
            put(Fireball.class, new DamageNegotiation(15));
            put(ChargedShot.class, new DamageNegotiation(MAX_HEALTH));
            put(ChargedShotDisintegration.class, new DamageNegotiation(15));
        }};
    }

    @Override
    public void onDamageInflictedTo(Damageable damageable) {
        if (damageable instanceof Megaman) {
            chompTimer.reset();
        }
    }

    public boolean isChomping() {
        return !chompTimer.isFinished();
    }

    protected UpdatableComponent updatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BodyComponent megaBody = getMegaman().getComponent(BodyComponent.class);
                if (bodyComponent.getMinX() >= megaBody.getMaxX()) {
                    setFacing(F_LEFT);
                } else if (bodyComponent.getMaxX() <= megaBody.getMinX()) {
                    setFacing(F_RIGHT);
                }
                if (isChomping() || !damageTimer.isFinished()) {
                    bodyComponent.setVelocity(0f, 0f);
                } else {
                    Vector2 vel = new Vector2();
                    vel.x = isFacing(F_LEFT) ? -HORIZ_SPEED : HORIZ_SPEED;
                    vel.y = megaBody.getMinY() <= bodyComponent.getMaxY() ? -VERT_SPEED :
                            (scannerSet.stream().anyMatch(f -> f.getFixtureType() == WATER) ? VERT_SPEED : 0f);
                    vel.scl(PPM);
                    bodyComponent.setVelocity(vel);
                }
                chompTimer.update(delta);
                scannerSet.clear();
            }
        });
    }

    /*
    protected PathfindingComponent pathfindingComponent() {
        PathfindingComponent pathfindingComponent = new PathfindingComponent(
                () -> getComponent(BodyComponent.class).getCenter(),
                () -> topCenterPoint(getMegaman().getComponent(BodyComponent.class).getCollisionBox()),
                target -> {
                    Vector2 targetCenter = centerPoint(target);
                    Vector2 thisCenter = getComponent(BodyComponent.class).getCenter();
                    float angle = atan2(targetCenter.y - thisCenter.y, targetCenter.x - thisCenter.x);
                    trajectory.set(cos(angle), sin(angle)).scl(SPEED * PPM);
                },
                target -> getComponent(BodyComponent.class).getCollisionBox().overlaps(target));
        pathfindingComponent.setDoAcceptPredicate(node ->
                node.getObjects().stream().anyMatch(o -> {
                    if (o instanceof Block || o instanceof GapingFish && !o.equals(this)) {
                        return false;
                    }
                    return o instanceof Entity entity && entity.hasComponent(BodyComponent.class) &&
                        entity.getComponent(BodyComponent.class).getFirstMatchingFixture(WATER).isPresent();
                }));
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
     */

    protected BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(spawn);
        // model 1
        Rectangle model1 = new Rectangle(0f, 0f, .75f * PPM, .2f * PPM);
        // head
        Fixture head = new Fixture(this, new Rectangle(model1), HEAD);
        head.setOffset(0f, .375f * PPM);
        bodyComponent.addFixture(head);
        // feet
        Fixture feet = new Fixture(this, new Rectangle(model1), FEET);
        feet.setOffset(0f, -.375f * PPM);
        bodyComponent.addFixture(feet);
        // water listener
        Fixture waterScanner = new Fixture(this, new Rectangle(model1), SCANNER);
        waterScanner.putUserData(COLLECTION, scannerSet);
        bodyComponent.addFixture(waterScanner);
        // box model
        Rectangle boxModel = new Rectangle(0f, 0f, .75f * PPM, PPM);
        // hit box
        Fixture hitBox = new Fixture(this, new Rectangle(boxModel), DAMAGEABLE);
        bodyComponent.addFixture(hitBox);
        // damage box
        Fixture damageBox = new Fixture(this, new Rectangle(boxModel), DAMAGER);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    protected SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(F_LEFT);
            }

        });
    }

    private AnimationComponent animationComponent() {
        Supplier<String> keySupplier = () -> {
            if (isChomping()) {
                return "Chomping";
            }
            return isInvincible() ? "Gaping" : "Swimming";
        };
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_1.getSrc(), TextureAtlas.class);
        Map<String, TimedAnimation> timedAnimations = Map.of(
                "Chomping", new TimedAnimation(textureAtlas.findRegion("GapingFish/Chomping"), 2, .1f),
                "Gaping", new TimedAnimation(textureAtlas.findRegion("GapingFish/Gaping"), 2, .15f),
                "Swimming", new TimedAnimation(textureAtlas.findRegion("GapingFish/Swimming"), 2, .15f));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

}
