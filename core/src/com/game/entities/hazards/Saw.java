package com.game.entities.hazards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.ConstVals;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.core.IAssetLoader;
import com.game.debugging.DebugRectComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.badlogic.gdx.graphics.Color.*;
import static com.game.core.ConstVals.TextureAsset.*;
import static com.game.core.ConstVals.ViewVals.PPM;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class Saw extends Entity {

    public enum SawType {
        NONE,
        PENDULUM_SAW,
        ROTATING_SAW,
        TRAJECTORY_SAW
    }

    public Saw(IAssetLoader assetLoader, Vector2 center) {
        this(assetLoader, center, SawType.NONE);
    }

    public Saw(IAssetLoader assetLoader, Vector2 center, String sawType) {
        this(assetLoader, center, SawType.valueOf(sawType));
    }

    public Saw(IAssetLoader assetLoader, Vector2 center, SawType sawType) {
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(defineSpriteComponent(center));
        addComponent(defineBodyComponent(center));
        addComponent(defineDebugRectComponent());
    }

    private SpriteComponent defineSpriteComponent(Vector2 center) {
        Sprite sprite = new Sprite();
        sprite.setSize(2f * PPM, 2f * PPM);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        TextureRegion textureRegion = assetLoader.getAsset(HAZARDS_1_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                .findRegion("Saw");
        TimedAnimation timedAnimation = new TimedAnimation(textureRegion, 2, .1f);
        return new AnimationComponent(timedAnimation);
    }

    private BodyComponent defineBodyComponent(Vector2 center) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setSize(2f * PPM, 2f * PPM);
        bodyComponent.setCenter(center);
        // Death 1
        Fixture death1 = new Fixture(this, DEATH);
        death1.setSize(2f * PPM, PPM);
        death1.setCenter(center);
        bodyComponent.addFixture(death1);
        // Death 2
        Fixture death2 = new Fixture(this, DEATH);
        death2.setSize(PPM, 2f * PPM);
        death2.setCenter(center);
        bodyComponent.addFixture(death2);
        return bodyComponent;
    }

    private DebugRectComponent defineDebugRectComponent() {
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
            debugRectComponent.addDebugHandle(fixture::getFixtureBox, () -> RED));
        return debugRectComponent;
    }

}
