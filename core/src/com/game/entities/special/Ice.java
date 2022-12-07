package com.game.entities.special;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.game.GameContext2d;
import com.game.entities.Entity;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class Ice extends Entity {

    public Ice(GameContext2d gameContext, RectangleMapObject rectObj) {
        super(gameContext);
        addComponent(bodyComponent(rectObj.getRectangle()));
    }

    protected BodyComponent bodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT, bounds);
        bodyComponent.addFixture(new Fixture(this, bounds, ICE));
        return bodyComponent;
    }

}
