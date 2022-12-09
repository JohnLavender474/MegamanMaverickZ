package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.Entity;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
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
import java.util.function.Function;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.GRAY;
import static com.game.GlobalKeys.COLLECTION;
import static com.game.GlobalKeys.FUNCTION;
import static com.game.ViewVals.PPM;
import static com.game.ViewVals.VIEW_HEIGHT;
import static com.game.assets.TextureAsset.ENEMIES_1;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.entities.contracts.Facing.F_RIGHT;
import static com.game.health.HealthVals.MAX_HEALTH;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.enums.Position.CENTER;
import static com.game.world.BodyType.ABSTRACT;
import static com.game.world.FixtureType.*;

public class MagFly extends AbstractEnemy implements Faceable {

    private static final float FORCE_FLASH_DURATION = .1f;
    private static final float X_VEL_NORMAL = 3f;
    private static final float X_VEL_SLOW = 1f;
    private static final float PULL_FORCE_X = .1f;
    private static final float PULL_FORCE_Y = .75f;
    private static final float DAMAGE_DURATION = .1f;

    private final Timer forceFlashTimer = new Timer(FORCE_FLASH_DURATION);
    private final Set<Fixture> leftScannerSet = new HashSet<>();
    private final Set<Fixture> rightScannerSet = new HashSet<>();
    private final Rectangle forceScannerRect = new Rectangle(0f, 0f, .5f * PPM, VIEW_HEIGHT * PPM);

    private boolean flash;
    @Getter
    @Setter
    private Facing facing;

    public MagFly(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, RectangleMapObject spawnObj) {
        this(gameContext, megamanSupplier, centerPoint(spawnObj.getRectangle()));
    }

    public MagFly(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, DAMAGE_DURATION);
        addComponent(updatableComponent());
        addComponent(bodyComponent(spawn));
        addComponent(spriteComponent());
        addComponent(animationComponent(gameContext));
        addComponent(shapeComponent());
        setFacing(isMegamanRight() ? F_RIGHT : F_LEFT);
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

    private boolean megamanOverlapForceScanner() {
        Rectangle megamanBounds = getMegaman().getComponent(BodyComponent.class).getCollisionBox();
        return forceScannerRect.overlaps(megamanBounds);
    }

    private boolean isMegamanRight() {
        BodyComponent mmBody = getMegaman().getComponent(BodyComponent.class);
        BodyComponent thisBody = getComponent(BodyComponent.class);
        return mmBody.isRightOf(thisBody);
    }

    private boolean isMegamanAbove() {
        BodyComponent mmBody = getMegaman().getComponent(BodyComponent.class);
        BodyComponent thisBody = getComponent(BodyComponent.class);
        return mmBody.isAbove(thisBody);
    }

    private boolean facingAndMMDirMatch() {
        return (isMegamanRight() && isFacing(F_RIGHT)) || (!isMegamanRight() && isFacing(F_LEFT));
    }

    private ShapeComponent shapeComponent() {
        ShapeHandle shapeHandle = new ShapeHandle();
        shapeHandle.setShapeSupplier(() -> forceScannerRect);
        shapeHandle.setColorSupplier(() -> GRAY);
        shapeHandle.setDoRenderSupplier(() -> flash);
        return new ShapeComponent(shapeHandle);
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                // force flash
                forceFlashTimer.update(delta);
                if (forceFlashTimer.isFinished()) {
                    flash = !flash;
                    forceFlashTimer.reset();
                }
                // facing and velocity
                boolean slow = megamanOverlapForceScanner();
                if (!slow && !isMegamanAbove() && !facingAndMMDirMatch()) {
                    setFacing(isMegamanRight() ? F_RIGHT : F_LEFT);
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if ((isFacing(F_LEFT) && leftScannerSet.stream().anyMatch(f -> f.getFixtureType() == BLOCK)) ||
                        (isFacing(F_RIGHT) && rightScannerSet.stream().anyMatch(f -> f.getFixtureType() == BLOCK))) {
                    bodyComponent.setVelocityX(0f);
                } else {
                    float vel = (slow ? X_VEL_SLOW : X_VEL_NORMAL) * PPM;
                    bodyComponent.setVelocityX(isFacing(F_LEFT) ? -vel : vel);
                }
                // clear scannerskz
                leftScannerSet.clear();
                rightScannerSet.clear();
            }
        });
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(spawn);
        // left and right scanners
        Rectangle sideScannerModel = new Rectangle(0f, 0f, .1f * PPM, .1f * PPM);
        Fixture leftScanner = new Fixture(this, new Rectangle(sideScannerModel), SCANNER);
        leftScanner.setOffset(-.6f * PPM, -.4f * PPM);
        leftScanner.putUserData(COLLECTION, leftScannerSet);
        bodyComponent.addFixture(leftScanner);
        Fixture rightScanner = new Fixture(this, new Rectangle(sideScannerModel), SCANNER);
        rightScanner.setOffset(.6f * PPM, -.4f * PPM);
        rightScanner.putUserData(COLLECTION, rightScannerSet);
        bodyComponent.addFixture(rightScanner);
        // force scanner
        Fixture forceScanner = new Fixture(this, forceScannerRect, CUSTOM);
        forceScanner.setOffset(0f, -VIEW_HEIGHT * PPM / 2f);
        bodyComponent.addFixture(forceScanner);
        // pull force fixture
        Fixture pullFixture = new Fixture(this, new Rectangle(forceScannerRect), FORCE);
        pullFixture.putUserData(FUNCTION, (Function<Entity, Vector2>) e -> {
            if (e instanceof Megaman megaman && !megaman.isDamaged()) {
                return new Vector2((isFacing(F_RIGHT) ? PULL_FORCE_X : -PULL_FORCE_X) * PPM, PULL_FORCE_Y * PPM);
            }
            return Vector2.Zero;
        });
        pullFixture.setOffset(0f, -VIEW_HEIGHT * PPM / 2f);
        bodyComponent.addFixture(pullFixture);
        // damager and damageable
        Circle hitCircle = new Circle();
        hitCircle.setRadius(.5f * PPM);
        bodyComponent.addFixture(new Fixture(this, hitCircle, DAMAGER));
        bodyComponent.addFixture(new Fixture(this, hitCircle, DAMAGEABLE));
        return bodyComponent;
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

            @Override
            public boolean isFlipX() {
                return isFacing(F_LEFT);
            }

        });
    }

    private AnimationComponent animationComponent(GameContext2d gameContext) {
        TextureRegion textureRegion = gameContext.getAsset(ENEMIES_1.getSrc(), TextureAtlas.class).findRegion("MagFly");
        TimedAnimation animation = new TimedAnimation(textureRegion, 2, .1f);
        return new AnimationComponent(animation);
    }

}
