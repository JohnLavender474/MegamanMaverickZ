package com.game.blocks;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;

import java.util.List;

public class StaticBlockFactory extends BlockFactory {

    @Override
    protected BodyType bodyType() {
        return BodyType.STATIC;
    }

    @Override
    protected void defineBodyComponent(BodyComponent bodyComponent, RectangleMapObject rectangleMapObject) {
        super.defineBodyComponent(bodyComponent, rectangleMapObject);
        Boolean canWallSlideLeft = rectangleMapObject.getProperties().get(
                "CanWallSlideLeft", Boolean.class);
        if (canWallSlideLeft != null && canWallSlideLeft) {
            // TODO: Define left wall slide sensor
        }
        Boolean canWallSlideRight = rectangleMapObject.getProperties().get(
                "CanWallSlideRight", Boolean.class);
        if (canWallSlideRight != null && canWallSlideRight) {
            // TODO: Define right wall slide sensor
        }
    }

}
