package com.game.entities.blocks.impl;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.blocks.Block;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;

import static com.badlogic.gdx.graphics.Color.RED;
import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.CUSTOM_TILES_1;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.enums.Position.*;

public class GearTrolley extends Block {

    private static final float WIDTH = 1.25f;
    private static final float HEIGHT = .35f;

    public GearTrolley(GameContext2d gameContext, RectangleMapObject rectObj) {
        super(gameContext, initBounds(rectObj), initProperties(rectObj.getProperties()));
        addComponent(spriteComponent());
        addComponent(animationComponent());
        addComponent(shapeComponent());
    }

    private static MapProperties initProperties(MapProperties properties) {
        properties.put("feetSticky", true);
        return properties;
    }

    private static Rectangle initBounds(RectangleMapObject rectObj) {
        Vector2 pos = centerPoint(rectObj.getRectangle());
        return new Rectangle(pos.x - (WIDTH * PPM / 2f), pos.y, WIDTH * PPM, HEIGHT * PPM);
    }

    protected SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(CENTER);
                return true;
            }

            @Override
            public float getOffsetY() {
                return -PPM / 16f;
            }
        });
    }

    protected AnimationComponent animationComponent() {
        TextureRegion region = gameContext.getAsset(CUSTOM_TILES_1.getSrc(), TextureAtlas.class)
                .findRegion("GearTrolleyPlatform");
        TimedAnimation animation = new TimedAnimation(region, 2, .15f);
        return new AnimationComponent(animation);
    }

    protected ShapeComponent shapeComponent() {
        ShapeHandle shapeHandle = new ShapeHandle();
        shapeHandle.setShapeSupplier(() -> getComponent(BodyComponent.class).getCollisionBox());
        shapeHandle.setColorSupplier(() -> RED);
        return new ShapeComponent(shapeHandle);
    }

}
