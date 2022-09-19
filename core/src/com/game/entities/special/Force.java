package com.game.entities.special;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.shapes.ShapeComponent;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import java.util.function.Supplier;

import static com.game.core.constants.MiscellaneousVals.SUPPLIER;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
public class Force extends Entity {

    public Force(GameContext2d gameContext, RectangleMapObject mapObject) {
        this(gameContext, mapObject.getRectangle(), getStaticForceSupplier(mapObject));
    }

    private static Supplier<Vector2> getStaticForceSupplier(RectangleMapObject mapObject) {
        MapProperties properties = mapObject.getProperties();
        float forceX = properties.get("forceX", Float.class);
        float forceY = properties.get("forceY", Float.class);
        Vector2 force = new Vector2(forceX, forceY).scl(PPM);
        return () -> force;
    }

    public Force(GameContext2d gameContext, Rectangle bounds, Supplier<Vector2> forceSupplier) {
        super(gameContext);
        addComponent(defineBodyComponent(bounds, forceSupplier));
        addComponent(new ShapeComponent(bounds));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, Supplier<Vector2> forceSupplier) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.set(bounds);
        Fixture forceFixture = new Fixture(this, bounds, FORCE);
        forceFixture.putUserData(SUPPLIER, forceSupplier);
        bodyComponent.addFixture(forceFixture);
        return bodyComponent;
    }

}
