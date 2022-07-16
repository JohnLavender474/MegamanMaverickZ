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
import com.game.utils.Position;
import com.game.utils.Wrapper;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;

@Getter
@Setter
public class TestSuctionRoller extends Entity implements Damager, Damageable, Faceable {

    private final Set<Class<? extends Damager>> damagerMaskSet = Set.of(TestBullet.class);

    private Facing facing;

    public TestSuctionRoller(IAssetLoader assetLoader, Supplier<TestPlayer> testPlayerSupplier, Vector2 spawn) {
        addComponent(defineBodyComponent(spawn, testPlayerSupplier));
        addComponent(defineDebugComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(assetLoader.getAsset(ENEMIES_TEXTURE_ATLAS, TextureAtlas.class)));
        addComponent(new CullOnCamTransComponent());
        addComponent(new CullOnOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 2f));
    }

    @Override
    public void takeDamageFrom(Damager damager) {

    }

    private boolean onWall() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        return (isFacing(Facing.LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                (isFacing(Facing.RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT));
    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> testPlayerSupplier) {
        return new UpdatableComponent(delta -> {
            BodyComponent thisBody = getComponent(BodyComponent.class);
            BodyComponent playerBody = testPlayerSupplier.get().getComponent(BodyComponent.class);
            if (onWall()) {
                
            } else {
                setFacing(thisBody.isLeftOf(playerBody) ? Facing.RIGHT : Facing.LEFT);
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
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public float getRotation() {
                return onWall() ? (isFacing(Facing.LEFT) ? -90f : 90f) : 0f;
            }

        });
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        return new AnimationComponent(new TimedAnimation(textureAtlas.findRegion("SuctionRoller"), 5, .1f));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn, Supplier<TestPlayer> testPlayerSupplier) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setSize(.75f * PPM, PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setPreProcess(delta -> {
            BodyComponent thisBody = getComponent(BodyComponent.class);
            BodyComponent testPlayerBody = testPlayerSupplier.get().getComponent(BodyComponent.class);
            if (onWall()) {
                bodyComponent.setGravity(0f);
                bodyComponent.setVelocity(0f, (testPlayerBody.isAbove(thisBody) ? 2.5f : -2.5f) * PPM);
            } else {
                bodyComponent.setGravity(-10f * PPM);
                bodyComponent.setVelocity((testPlayerBody.isRightOf(thisBody) ? 2.5f : -2.5f) * PPM, 0f);
            }
        });
        // feet
        Fixture feet = new Fixture(this, FixtureType.FEET);
        feet.setSize(9.5f, .75f);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        // left
        Fixture left = new Fixture(this, FixtureType.LEFT);
        left.setSize(1f, PPM / 4f);
        left.setOffset(-.375f * PPM, 0f);
        bodyComponent.addFixture(left);
        // right
        Fixture right = new Fixture(this, FixtureType.RIGHT);
        right.setSize(1f, PPM / 4f);
        right.setOffset(.375f * PPM, 0f);
        bodyComponent.addFixture(right);
        return bodyComponent;
    }

    private DebugComponent defineDebugComponent() {
        DebugComponent debugComponent = new DebugComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
            debugComponent.addDebugHandle(fixture::getFixtureBox, () -> Color.GREEN));
        return debugComponent;
    }

}
