package com.game.tests.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOnOutOfCamBoundsComponent;
import com.game.debugging.DebugComponent;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.enums.Position.*;
import static com.game.utils.UtilMethods.*;
import static com.game.world.BodySense.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class TestSuctionRoller extends Entity implements Damager, Damageable, Faceable {

    @Getter
    private final Set<Class<? extends Damager>> damagerMaskSet = Set.of(TestBullet.class);

    @Getter
    @Setter
    private Facing facing;

    private boolean isOnWall;
    private boolean wasOnWall;

    public TestSuctionRoller(IAssetLoader assetLoader,
                             Supplier<TestPlayer> testPlayerSupplier, Vector2 spawn) {
        addComponent(defineBodyComponent(spawn, testPlayerSupplier));
        addComponent(defineDebugComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineUpdatableComponent(testPlayerSupplier));
        addComponent(defineAnimationComponent(assetLoader.getAsset(ENEMIES_TEXTURE_ATLAS, TextureAtlas.class)));
        addComponent(new CullOnCamTransComponent());
        addComponent(new CullOnOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 2f));
    }

    @Override
    public void takeDamageFrom(Damager damager) {

    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> testPlayerSupplier) {
        return new UpdatableComponent(delta -> {
            BodyComponent thisBody = getComponent(BodyComponent.class);
            wasOnWall = isOnWall;
            isOnWall = (isFacing(F_LEFT) && thisBody.is(TOUCHING_BLOCK_LEFT)) ||
                    (isFacing(F_RIGHT) && thisBody.is(TOUCHING_BLOCK_RIGHT));
            BodyComponent playerBody = testPlayerSupplier.get().getComponent(BodyComponent.class);
            if (isOnWall) {
                
            } else {
                setFacing(thisBody.isLeftOf(playerBody) ? F_RIGHT : F_LEFT);
            }
        });
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
                return isFacing(F_RIGHT);
            }

        });
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        return new AnimationComponent(new TimedAnimation(textureAtlas.findRegion("SuctionRoller"), 5, .1f));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn, Supplier<TestPlayer> testPlayerSupplier) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(.75f * PPM, PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> {
            BodyComponent thisBody = getComponent(BodyComponent.class);
            BodyComponent testPlayerBody = testPlayerSupplier.get().getComponent(BodyComponent.class);
            if (isOnWall) {
                if (!wasOnWall) {
                    bodyComponent.setGravity(0f);
                    bodyComponent.setVelocityX(0f);
                }
                boolean playerIsAbove = testPlayerBody.isAbove(thisBody);
                bodyComponent.setVelocityY((playerIsAbove ? 2.5f : -2.5f) * PPM);
                if (!playerIsAbove && testPlayerBody.is(FEET_ON_GROUND)) {
                    isOnWall = false;
                }
            }
            if (!isOnWall) {
                if (wasOnWall) {
                    bodyComponent.setGravity(-50f * PPM);
                    bodyComponent.setVelocityY(0f);
                }
                bodyComponent.setVelocityX((testPlayerBody.isRightOf(thisBody) ? 2.5f : -2.5f) * PPM);
            }
        });
        // feet
        Fixture feet = new Fixture(this, FEET);
        feet.setSize(9.5f, .75f);
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

    private DebugComponent defineDebugComponent() {
        DebugComponent debugComponent = new DebugComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
           debugComponent.addDebugHandle(fixture::getFixtureBox, () -> {
               if (equalsAny(fixture.getFixtureType(), LEFT, RIGHT)) {
                   return Color.GREEN;
               }
               return Color.BLUE;
           }));
        return debugComponent;
    }

}
