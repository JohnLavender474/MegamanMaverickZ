package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.contracts.Hitter;
import com.game.entities.megaman.Megaman;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.game.GlobalKeys.*;
import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class SpringHead extends AbstractEnemy implements Hitter, Faceable {

    private static final float DAMAGE_DURATION = .5f;
    private static final float BOUNCE_DURATION = 1.5f;
    private static final float TURN_DELAY = .25f;
    private static final float X_BOUNCE = .125f;
    private static final float Y_BOUNCE = .5f;
    private static final float SPEED_NORMAL = 1.5f;
    private static final float SPEED_SUPER = 5f;

    private final Timer turnTimer = new Timer(TURN_DELAY);
    private final Timer bounceTimer = new Timer(BOUNCE_DURATION, true);

    private final Set<FixtureType> leftScannerSet = EnumSet.noneOf(FixtureType.class);
    private final Set<FixtureType> rightScannerSet = EnumSet.noneOf(FixtureType.class);
    private final Rectangle speedUpScanner = new Rectangle(0f, 0f, VIEW_WIDTH * PPM, .25f * PPM);

    @Getter
    @Setter
    private Facing facing;

    public SpringHead(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, RectangleMapObject spawnObj) {
        super(gameContext, megamanSupplier, DAMAGE_DURATION);
        Vector2 spawn = bottomCenterPoint(spawnObj.getRectangle());
        addComponent(animationComponent(gameContext));
        addComponent(bodyComponent(spawn));
        addComponent(updatableComponent());
        addComponent(spriteComponent());
        addComponent(shapeComponent());
        setFacing(isMegamanRight() ? F_RIGHT : F_LEFT);
    }

    public boolean isBouncing() {
        return !bounceTimer.isFinished();
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.isFixtureType(BOUNCEABLE)) {
            bounceTimer.reset();
        }
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return new HashMap<>() {{

        }};
    }

    private boolean isMegamanRight() {
        BodyComponent megamanBody = getMegaman().getComponent(BodyComponent.class);
        return megamanBody.isRightOf(getComponent(BodyComponent.class));
    }

    private boolean megamanOverlapSpeedUpScanner() {
        Rectangle megamanBounds = getMegaman().getComponent(BodyComponent.class).getCollisionBox();
        return speedUpScanner.overlaps(megamanBounds);
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(delta -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            bounceTimer.update(delta);
            if (isBouncing()) {
                bodyComponent.setVelocityX(0f);
                return;
            }
            turnTimer.update(delta);
            if (turnTimer.isJustFinished()) {
                setFacing(isMegamanRight() ? F_RIGHT : F_LEFT);
            }
            if (turnTimer.isFinished() &&
                    ((isMegamanRight() && isFacing(F_LEFT)) || (!isMegamanRight() && isFacing(F_RIGHT)))) {
                turnTimer.reset();
            }
            if ((isFacing(F_LEFT) && !leftScannerSet.contains(BLOCK)) ||
                    (isFacing(F_RIGHT) && !rightScannerSet.contains(BLOCK))) {
                bodyComponent.setVelocityX(0f);
            } else {
                float vel = (megamanOverlapSpeedUpScanner() ? SPEED_SUPER : SPEED_NORMAL) * PPM;
                bodyComponent.setVelocityX(isFacing(F_RIGHT) ? vel : -vel);
            }
            leftScannerSet.clear();
            rightScannerSet.clear();
        });
    }

    private ShapeComponent shapeComponent() {
        List<ShapeHandle> shapeHandles = new ArrayList<>();
        getComponent(BodyComponent.class).getFixtures().forEach(f -> {
            ShapeHandle shapeHandle = new ShapeHandle();
            shapeHandle.setShapeSupplier(f::getFixtureShape);
            shapeHandle.setColorSupplier(() -> RED);
            shapeHandles.add(shapeHandle);
        });
        return new ShapeComponent(shapeHandles);
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(.25f * PPM, .25f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        // hit circle
        Circle hitCircle = new Circle();
        hitCircle.setRadius(.25f * PPM);
        bodyComponent.addFixture(new Fixture(this, hitCircle, DAMAGER));
        bodyComponent.addFixture(new Fixture(this, hitCircle, DAMAGEABLE));
        // shield
        Rectangle shieldRect = new Rectangle();
        shieldRect.setSize(.8f * PPM, .6f * PPM);
        Fixture shield = new Fixture(this, shieldRect, SHIELD);
        shield.setOffset(0f, .1f * PPM);
        shield.putUserData("reflectDir", "up");
        bodyComponent.addFixture(shield);
        // bouncer and hitter
        Circle modelCircle = new Circle();
        modelCircle.setRadius(.25f * PPM);
        Fixture bouncer = new Fixture(this, new Circle(modelCircle), BOUNCER);
        bouncer.putUserData("xFunc", (Function<Entity, Float>) e -> {
            BodyComponent entityBody = e.getComponent(BodyComponent.class);
            if (entityBody == null) {
                return 0f;
            }
            return (bodyComponent.isRightOf(entityBody) ? -X_BOUNCE : X_BOUNCE) * PPM;
        });
        bouncer.putUserData("yFunc", (Function<Entity, Float>) e -> {
            BodyComponent entityBody = e.getComponent(BodyComponent.class);
            return entityBody != null ? Y_BOUNCE  * PPM : 0f;
        });
        bodyComponent.addFixture(bouncer);
        Fixture hitter = new Fixture(this, new Circle(modelCircle), HITTER);
        bodyComponent.addFixture(hitter);
        // scanner model
        Rectangle scannerModel = new Rectangle(0f, 0f, .1f * PPM, .1f * PPM);
        // scanners
        Fixture leftScanner = new Fixture(this, new Rectangle(scannerModel), SCANNER);
        leftScanner.putUserData(COLLECTION, leftScannerSet);
        leftScanner.setOffset(-.4f * PPM, -.25f * PPM);
        bodyComponent.addFixture(leftScanner);
        Fixture rightScanner = new Fixture(this, new Rectangle(scannerModel), SCANNER);
        rightScanner.putUserData(COLLECTION, rightScannerSet);
        rightScanner.setOffset(.4f * PPM, -.25f * PPM);
        bodyComponent.addFixture(rightScanner);
        // custom
        Fixture customFixture = new Fixture(this, speedUpScanner, CUSTOM);
        bodyComponent.addFixture(customFixture);
        return bodyComponent;
    }
    
    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

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
    
    private AnimationComponent animationComponent(GameContext2d gameContext) {
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_1.getSrc(), TextureAtlas.class);
        Supplier<String> keySupplier = () -> isBouncing() ? "Unleashed" : "Compressed";
        Map<String, TimedAnimation> timedAnimations = Map.of(
                "Unleashed", new TimedAnimation(textureAtlas.findRegion("SpringHead/Unleashed"), 6, .1f),
                "Compressed", new TimedAnimation(textureAtlas.findRegion("SpringHead/Compressed")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

}
