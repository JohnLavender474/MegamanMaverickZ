package com.game.entities.special;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.shapes.ShapeComponent;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import java.util.function.Function;

import static com.game.GlobalKeys.FUNCTION;
import static com.game.ViewVals.PPM;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

@Getter
public class Force extends Entity {

    public Force(GameContext2d gameContext, RectangleMapObject mapObject) {
        this(gameContext, mapObject.getRectangle(), getStaticForceFunction(mapObject));
    }

    private static Function<Entity, Vector2> getStaticForceFunction(RectangleMapObject mapObject) {
        MapProperties properties = mapObject.getProperties();
        float forceX = properties.get("forceX", Float.class);
        float forceY = properties.get("forceY", Float.class);
        Vector2 force = new Vector2(forceX, forceY).scl(PPM);
        return e -> force;
    }

    public Force(GameContext2d gameContext, Rectangle bounds, Function<Entity, Vector2> forceFunction) {
        super(gameContext);
        addComponent(bodyComponent(bounds, forceFunction));
        addComponent(new ShapeComponent(bounds));
    }

    private BodyComponent bodyComponent(Rectangle bounds, Function<Entity, Vector2> forceFunction) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.set(bounds);
        Fixture forceFixture = new Fixture(this, bounds, FORCE);
        forceFixture.putUserData(FUNCTION, forceFunction);
        bodyComponent.addFixture(forceFixture);
        return bodyComponent;
    }

}
