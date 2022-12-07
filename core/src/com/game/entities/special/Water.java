package com.game.entities.special;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.game.GameContext2d;
import com.game.entities.Entity;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.badlogic.gdx.graphics.Color.BLUE;
import static com.game.world.BodyType.ABSTRACT;
import static com.game.world.FixtureType.WATER;

public class Water extends Entity {

    public Water(GameContext2d gameContext, RectangleMapObject rectObj) {
        super(gameContext);
        addComponent(bodyComponent(rectObj.getRectangle()));
        addComponent(shapeComponent(rectObj.getRectangle()));
    }

    protected BodyComponent bodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT, bounds);
        bodyComponent.addFixture(new Fixture(this, bounds, WATER));
        return bodyComponent;
    }

    protected ShapeComponent shapeComponent(Rectangle bounds) {
        ShapeComponent shapeComponent = new ShapeComponent();
        ShapeHandle shapeHandle = new ShapeHandle();
        shapeHandle.setColorSupplier(() -> BLUE);
        shapeHandle.setShapeSupplier(() -> bounds);
        shapeComponent.addShapeHandle(shapeHandle);
        return shapeComponent;
    }

}
