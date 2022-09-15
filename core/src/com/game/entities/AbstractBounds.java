package com.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.world.BodyComponent;

import static com.game.world.BodyType.*;

public class AbstractBounds extends Entity {

    public static final String ABSTRACT_BOUNDS = "AbstractBounds";

    public AbstractBounds(GameContext2d gameContext, Rectangle bounds) {
        super(gameContext);
        addComponent(defineBodyComponent(bounds));
    }

    private BodyComponent defineBodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setCustomCollisionBit(ABSTRACT_BOUNDS);
        bodyComponent.set(bounds);
        return bodyComponent;
    }

}
