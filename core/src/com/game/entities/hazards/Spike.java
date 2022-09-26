package com.game.entities.hazards;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.game.ViewVals.PPM;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class Spike extends Entity {

    public Spike(GameContext2d gameContext, Vector2 pos, String textureKey) {
        super(gameContext);
        addComponent(bodyComponent(pos));
        // addComponent(spriteComponent(gameContext, textureKey));
    }

    private BodyComponent bodyComponent(Vector2 pos) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setPosition(pos);
        Rectangle deathRect = new Rectangle();
        deathRect.setSize(PPM * .95f, PPM * .95f);
        bodyComponent.addFixture(new Fixture(this, deathRect, DEATH));
        return bodyComponent;
    }

    /*
    private SpriteComponent spriteComponent(GameContext2d gameContext, String textureKey) {
        TextureRegion textureRegion = gameContext.getAsset(SPIKES.getSrc(), TextureAtlas.class).findRegion(textureKey);
        Sprite sprite = new Sprite(textureRegion);
        sprite.setSize(PPM, PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {
            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(BOTTOM_LEFT);
                return true;
            }
        });
    }
     */

}
