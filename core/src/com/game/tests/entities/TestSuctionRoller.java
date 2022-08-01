package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.DamageNegotiation;
import com.game.debugging.DebugLinesComponent;
import com.game.debugging.DebugRectComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.health.HealthComponent;
import com.game.pathfinding.PathfindingComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.game.ConstVals.TextureAsset.ENEMIES_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.enums.Position.*;
import static com.game.utils.UtilMethods.*;
import static com.game.world.BodySense.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestSuctionRoller extends Entity implements Damager, Damageable, Faceable {

    private final IAssetLoader assetLoader;
    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations = new HashMap<>();
    private final Rectangle nextTarget = new Rectangle();
    private final Timer offWallGrace = new Timer(.15f);
    private final Timer damageTimer = new Timer(.25f);

    private Facing facing;
    private boolean isOnWall;
    private boolean wasOnWall;

    public TestSuctionRoller(IEntitiesAndSystemsManager entitiesAndSystemsManager, IAssetLoader assetLoader,
                             Supplier<TestPlayer> testPlayerSupplier, Vector2 spawn) {
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        this.assetLoader = assetLoader;
        damageTimer.setToEnd();
        offWallGrace.setToEnd();
        defineDamageNegotiations();
        addComponent(defineSpriteComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(new HealthComponent(30, this::disintegrate));
        addComponent(defineUpdatableComponent(testPlayerSupplier));
        addComponent(definePathfindingComponent(testPlayerSupplier));
        addComponent(defineAnimationComponent(assetLoader.getAsset(ENEMIES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)));
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 2f));
        addComponent(new CullOnCamTransComponent());
        addComponent(defineDebugRectComponent());
        addComponent(defineDebugLinesComponent());
    }

    private void defineDamageNegotiations() {
        damageNegotiations.put(TestBullet.class, new DamageNegotiation(10));
        damageNegotiations.put(TestFireball.class, new DamageNegotiation(10));
        damageNegotiations.put(TestChargedShot.class, new DamageNegotiation(30));
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return damageNegotiations.keySet();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        DamageNegotiation damageNegotiation = damageNegotiations.get(damager.getClass());
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
        getComponent(HealthComponent.class).sub(damageNegotiation.damage());
        damageNegotiation.runOnDamage();
        damageTimer.reset();
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished();
    }

    private void disintegrate() {
        entitiesAndSystemsManager.addEntity(new TestDisintegration(assetLoader,
                getComponent(BodyComponent.class).getCenter(), new Vector2(2f * PPM, 2f * PPM)));
        Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> testPlayerSupplier) {
        return new UpdatableComponent(delta -> {
            if (testPlayerSupplier.get().isDead()) {
                return;
            }
            damageTimer.update(delta);
            BodyComponent thisBody = getComponent(BodyComponent.class);
            wasOnWall = isOnWall;
            isOnWall = (isFacing(F_LEFT) && thisBody.is(TOUCHING_BLOCK_LEFT)) ||
                    (isFacing(F_RIGHT) && thisBody.is(TOUCHING_BLOCK_RIGHT));
            offWallGrace.update(delta);
            if (wasOnWall && !isOnWall) {
                offWallGrace.reset();
            }
            BodyComponent playerBody = testPlayerSupplier.get().getComponent(BodyComponent.class);
            if (thisBody.is(FEET_ON_GROUND)) {
                if (bottomRightPoint(playerBody.getCollisionBox()).x < thisBody.getPosition().x) {
                    setFacing(F_LEFT);
                } else if (playerBody.getPosition().x > bottomRightPoint(thisBody.getCollisionBox()).x) {
                    setFacing(F_RIGHT);
                }
            }
        });
    }

    private PathfindingComponent definePathfindingComponent(Supplier<TestPlayer> testPlayerSupplier) {
        PathfindingComponent pathfindingComponent = new PathfindingComponent(
                () -> getComponent(BodyComponent.class).getCenter(), () -> testPlayerSupplier.get().getFocus(),
                nextTarget::set,
                target -> getComponent(BodyComponent.class).getCollisionBox().contains(centerPoint(target)));
        pathfindingComponent.setDoAcceptPredicate(node ->
                node.getObjects().stream().noneMatch(o -> o instanceof TestBlock) &&
                        (node.getObjects().contains("Ground") || node.getObjects().contains("LeftWall") ||
                                node.getObjects().contains("RightWall")));
        pathfindingComponent.setDoAllowDiagonal(() -> false);
        Timer timer = new Timer(.25f);
        pathfindingComponent.setDoUpdatePredicate(delta -> {
            timer.update(delta);
            boolean isFinished = timer.isFinished();
            if (isFinished) {
                timer.reset();
            }
            return isFinished;
        });
        return pathfindingComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(isOnWall ? (isFacing(F_LEFT) ? CENTER_LEFT : CENTER_RIGHT) : BOTTOM_CENTER);
                return true;
            }

            @Override
            public float getRotation() {
                return isOnWall ? (isFacing(F_LEFT) ? -90f : 90f) : 0f;
            }

            @Override
            public boolean isFlipX() {
                if (isOnWall || !offWallGrace.isFinished()) {
                    return isFacing(F_RIGHT) ? nextTarget.y >= getComponent(BodyComponent.class).getCenter().y :
                            nextTarget.y < getComponent(BodyComponent.class).getCenter().y;
                }
                return isFacing(F_RIGHT);
            }

        });
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        return new AnimationComponent(new TimedAnimation(textureAtlas.findRegion("SuctionRoller"), 5, .1f));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setGravity(-35f * PPM);
        bodyComponent.setSize(.75f * PPM, PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> {
            if (isOnWall) {
                if (!wasOnWall) {
                    bodyComponent.setVelocityX(0f);
                }
                boolean moveUp = centerPoint(nextTarget).y > bodyComponent.getCenter().y;
                bodyComponent.setVelocityY((moveUp ? 2.5f : -2.5f) * PPM);
            }
            if (!isOnWall) {
                if (wasOnWall) {
                    bodyComponent.setVelocityY(0f);
                }
                bodyComponent.setVelocityX((isFacing(F_RIGHT) ? 2.5f : -2.5f) * PPM);
            }
        });
        // hit box
        Fixture hitbox = new Fixture(this, DAMAGEABLE_BOX);
        hitbox.setSize(.75f * PPM, PPM);
        bodyComponent.addFixture(hitbox);
        // damager box
        Fixture damagerbox = new Fixture(this, DAMAGER_BOX);
        damagerbox.setSize(.75f * PPM, PPM);
        bodyComponent.addFixture(damagerbox);
        // feet
        Fixture feet = new Fixture(this, FEET);
        feet.setSize(8f, .75f);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        // left
        Fixture left = new Fixture(this, LEFT);
        left.setSize(1f, PPM - 1f);
        left.setOffset(-.375f * PPM, 0f);
        bodyComponent.addFixture(left);
        // right
        Fixture right = new Fixture(this, RIGHT);
        right.setSize(1f, PPM - 1f);
        right.setOffset(.375f * PPM, 0f);
        bodyComponent.addFixture(right);
        return bodyComponent;
    }

    private DebugRectComponent defineDebugRectComponent() {
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
           debugRectComponent.addDebugHandle(fixture::getFixtureBox, () -> {
               if (equalsAny(fixture.getFixtureType(), LEFT, RIGHT)) {
                   return GREEN;
               }
               return BLUE;
           }));
        return debugRectComponent;
    }

    private DebugLinesComponent defineDebugLinesComponent() {
        return new DebugLinesComponent(() -> getComponent(PathfindingComponent.class).getPathPoints(), () -> RED);
    }

}
