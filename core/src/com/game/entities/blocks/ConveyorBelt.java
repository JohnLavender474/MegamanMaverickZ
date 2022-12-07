package com.game.entities.blocks;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;

import com.game.entities.decorations.ConveyorBeltPart;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Color.*;
import static com.game.GlobalKeys.APPLY;
import static com.game.ViewVals.PPM;
import static com.game.world.FixtureType.*;

public class ConveyorBelt extends Block {

    private static final float FORCE_AMOUNT = 1.75f;

    public ConveyorBelt(GameContext2d gameContext, RectangleMapObject rectObj) {
        super(gameContext, rectObj.getRectangle(), true);
        // create conveyor box
        boolean isMovingLeft = rectObj.getProperties().get("isMovingLeft", Boolean.class);
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        Rectangle bounds = rectObj.getRectangle();
        Rectangle conveyorBox = new Rectangle(bounds.x - (PPM / 16f), bounds.y - PPM / 2f,
                bounds.width + (PPM / 16f), bounds.height);
        // create conveyor fixture
        Fixture conveyorFixture = new Fixture(this, conveyorBox, CONVEYOR);
        Vector2 force = new Vector2(isMovingLeft ? -FORCE_AMOUNT : FORCE_AMOUNT, 0f);
        conveyorFixture.putUserData(APPLY, force);
        bodyComponent.addFixture(conveyorFixture);
        // conveyor belt parts
        int numParts = (int) (bounds.width / PPM);
        List<ConveyorBeltPart> parts = new ArrayList<>();
        for (int i = 0; i < numParts; i++) {
            String part;
            if (i == 0) {
                part = "left";
            } else if (i == numParts - 1) {
                part = "right";
            } else {
                part = "middle";
            }
            Vector2 pos = new Vector2(bounds.x + i * PPM, bounds.y);
            parts.add(new ConveyorBeltPart(gameContext, pos, part, isMovingLeft));
        }
        gameContext.addEntities(parts);
        // debug collision box
        addComponent(shapeComponent(bodyComponent));
    }

    protected ShapeComponent shapeComponent(BodyComponent bodyComponent) {
        ShapeComponent shapeComponent = new ShapeComponent();
        ShapeHandle shapeHandle = new ShapeHandle();
        shapeHandle.setShapeSupplier(bodyComponent::getCollisionBox);
        shapeHandle.setColorSupplier(() -> RED);
        shapeComponent.addShapeHandle(shapeHandle);
        return new ShapeComponent(shapeHandle);
    }

}
