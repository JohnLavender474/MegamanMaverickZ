package com.game.entities.special;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.entities.megaman.Megaman;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Direction;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.game.assets.SoundAsset.*;
import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.PPM;

import static com.game.controllers.ControllerUtils.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Direction.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class SpringBounce extends Entity {

    private static final float BOUNCE_DURATION = .5f;

    private final Timer bounceTimer = new Timer(BOUNCE_DURATION);
    private final Direction direction;

    public SpringBounce(GameContext2d gameContext, RectangleMapObject bouncerObj) {
        super(gameContext);
        bounceTimer.setToEnd();
        String dirStr = bouncerObj.getProperties().get("direction", "u", String.class);
        direction = getDirectionFromString(dirStr);
        addComponent(new SoundComponent());
        addComponent(updatableComponent());
        addComponent(animationComponent());
        addComponent(bodyComponent(bouncerObj));
        addComponent(spriteComponent(bouncerObj.getRectangle()));
    }

    private float getBounce(Entity entity) {
        float bounce = 0f;
        if (entity instanceof Megaman) {
            bounce = gameContext.isControllerButtonPressed(getButtonFromDirection(direction)) ? 30f : 15f;
        }
        return equalsAny(direction, DIR_UP, DIR_RIGHT) ? bounce : -bounce;
    }

    private void onBounce() {
        bounceTimer.reset();
        getComponent(SoundComponent.class).requestSound(DINK_SOUND);
    }

    private BodyComponent bodyComponent(RectangleMapObject bouncerObj) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT, bouncerObj.getRectangle());
        Fixture bouncer = new Fixture(this, new Rectangle(bouncerObj.getRectangle()), BOUNCER);
        String funcKey = equalsAny(direction, DIR_UP, DIR_DOWN) ? "yFunc" : "xFunc";
        bouncer.putUserData(funcKey, (Function<Entity, Float>) this::getBounce);
        bouncer.putUserData("onBounce", (Runnable) this::onBounce);
        bodyComponent.addFixture(bouncer);
        return bodyComponent;
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(bounceTimer::update);
    }

    private SpriteComponent spriteComponent(Rectangle boundsRect) {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(boundsRect);
                switch (direction) {
                    case DIR_UP -> position.setData(BOTTOM_CENTER);
                    case DIR_DOWN -> position.setData(TOP_CENTER);
                    case DIR_LEFT -> position.setData(CENTER_RIGHT);
                    case DIR_RIGHT -> position.setData(CENTER_LEFT);
                }
                return true;
            }

            @Override
            public float getRotation() {
                switch (direction) {
                    case DIR_LEFT -> {
                        return 90f;
                    }
                    case DIR_DOWN -> {
                        return 180f;
                    }
                    case DIR_RIGHT -> {
                        return 270f;
                    }
                    default -> {
                        return 0f;
                    }
                }
            }

        });
    }

    private AnimationComponent animationComponent() {
        Supplier<String> keySupplier = () -> bounceTimer.isFinished() ? "still" : "bounce";
        TextureAtlas textureAtlas = gameContext.getAsset(OBJECTS.getSrc(), TextureAtlas.class);
        Map<String, TimedAnimation> animationMap = Map.of(
                "still", new TimedAnimation(textureAtlas.findRegion("SpringBounceStill")),
                "bounce", new TimedAnimation(textureAtlas.findRegion("SpringBounce"), 5, .05f));
        return new AnimationComponent(keySupplier, animationMap::get);
    }

}
