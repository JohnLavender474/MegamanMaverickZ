package com.game.entities.special;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.game.core.constants.SoundAsset.*;
import static com.game.core.constants.TextureAsset.*;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class SpringyBouncer extends Entity {

    private final Timer bounceTimer = new Timer(.5f);

    public SpringyBouncer(GameContext2d gameContext, RectangleMapObject bouncerObj) {
        super(gameContext);
        bounceTimer.setToEnd();
        addComponent(new SoundComponent());
        addComponent(updatableComponent());
        addComponent(animationComponent());
        addComponent(bodyComponent(bouncerObj));
        addComponent(spriteComponent(bouncerObj.getRectangle()));
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(bounceTimer::update);
    }

    private BodyComponent bodyComponent(RectangleMapObject bouncerObj) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT, bouncerObj.getRectangle());
        Float x = bouncerObj.getProperties().get("xBounce", Float.class);
        Float y = bouncerObj.getProperties().get("yBounce", Float.class);
        Fixture bouncer = new Fixture(this, new Rectangle(bouncerObj.getRectangle()), BOUNCER);
        if (x != null) {
            bouncer.putUserData("xFunc", (Function<Entity, Float>) e -> x);
        }
        if (y != null) {
            bouncer.putUserData("yFunc", (Function<Entity, Float>) e -> y);
        }
        bouncer.putUserData("onBounce", (Runnable) () -> {
            bounceTimer.reset();
            getComponent(SoundComponent.class).requestSound(DINK_SOUND);
        });
        bodyComponent.addFixture(bouncer);
        return bodyComponent;
    }

    private SpriteComponent spriteComponent(Rectangle boundsRect) {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(boundsRect);
                position.setData(BOTTOM_CENTER);
                return true;
            }
        });
    }

    private AnimationComponent animationComponent() {
        Supplier<String> keySupplier = () -> bounceTimer.isFinished() ? "still" : "bounce";
        TextureAtlas textureAtlas = gameContext.getAsset(OBJECTS.getSrc(), TextureAtlas.class);
        Map<String, TimedAnimation> animationMap = Map.of(
                "still", new TimedAnimation(textureAtlas.findRegion("SpringyBouncerStill")),
                "bounce", new TimedAnimation(textureAtlas.findRegion("SpringyBouncer"), 5, .05f));
        return new AnimationComponent(keySupplier, animationMap::get);
    }

}
