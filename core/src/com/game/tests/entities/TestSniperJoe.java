package com.game.tests.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Position;
import com.game.utils.Timer;
import com.game.utils.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class TestSniperJoe extends Entity implements Faceable {

    enum SniperJoeBehavior {
        SHIELDED,
        SHOOTING,
    }

    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Map<SniperJoeBehavior, Timer> sniperJoeBehaviorTimers = new EnumMap<>(SniperJoeBehavior.class) {{
        put(SniperJoeBehavior.SHIELDED, new Timer(1.5f));
        put(SniperJoeBehavior.SHOOTING, new Timer(1f));
    }};
    private SniperJoeBehavior sniperJoeBehavior;
    private Facing facing;

    public TestSniperJoe(IEntitiesAndSystemsManager entitiesAndSystemsManager, IAssetLoader assetLoader,
                         Supplier<TestPlayer> testPlayerSupplier, Vector2 spawn) {
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        addComponent(defineUpdatableComponent(testPlayerSupplier));
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(assetLoader.getAsset(ENEMIES_TEXTURE_ATLAS, TextureAtlas.class)));
        addComponent(defineBodyComponent(spawn));
    }

    private UpdatableComponent defineUpdatableComponent(Supplier<TestPlayer> testPlayerSupplier) {
        return new UpdatableComponent(delta -> {

        });
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 2.5f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(Facing.LEFT);
            }

        });
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        Supplier<String> keySupplier = () -> switch (sniperJoeBehavior) {
            case SHOOTING -> "Shooting";
            case SHIELDED -> "Shielded";
        };
        Map<String, TimedAnimation> timedAnimations = new HashMap<>() {{
            put("Shooting", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShooting")));
            put("Shielded", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShielded")));
        }};
        return new AnimationComponent(new Animator(keySupplier, timedAnimations));
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.set(spawn.x, spawn.y, PPM, 1.5f * PPM);
        bodyComponent.setGravity(-50f * PPM);
        /*
        // hit box
        Fixture hitBox = new Fixture(this, FixtureType.HIT_BOX);
        hitBox.setSize(.75f * PPM, 1.5f * PPM);
        bodyComponent.addFixture(hitBox);
        // shield
        Fixture shield = new Fixture(this, FixtureType.SHIELD);
        shield.setSize(.15f * PPM, 1.5f * PPM);
        bodyComponent.addFixture(shield);
         */
        return bodyComponent;
    }

}
