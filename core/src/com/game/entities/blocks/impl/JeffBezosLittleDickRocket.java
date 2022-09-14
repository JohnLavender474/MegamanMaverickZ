package com.game.entities.blocks.impl;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.GameContext2d;
import com.game.core.constants.TextureAsset;
import com.game.entities.blocks.Block;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;

import static com.game.core.constants.TextureAsset.*;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;

public class JeffBezosLittleDickRocket extends Block {

    private static final float WIDTH = 1.5f;
    private static final float HEIGHT = 2.5f;

    public JeffBezosLittleDickRocket(GameContext2d gameContext, RectangleMapObject rectObj) {
        super(gameContext, initBounds(rectObj), rectObj.getProperties());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent());
    }

    private static Rectangle initBounds(RectangleMapObject rectObj) {
        Vector2 bottomCenter = bottomCenterPoint(rectObj.getRectangle());
        return new Rectangle(bottomCenter.x - (WIDTH  * PPM / 2f), bottomCenter.y, WIDTH * PPM, HEIGHT * PPM);
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(WIDTH * PPM, HEIGHT * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(BOTTOM_CENTER);
                return true;
            }
        });
    }

    private AnimationComponent defineAnimationComponent() {
        TextureRegion region = gameContext.getAsset(CUSTOM_TILES.getSrc(), TextureAtlas.class)
                .findRegion("JeffBezosLittleDickRocket");
        TimedAnimation animation = new TimedAnimation(region, 7, .05f);
        return new AnimationComponent(animation);
    }

}
