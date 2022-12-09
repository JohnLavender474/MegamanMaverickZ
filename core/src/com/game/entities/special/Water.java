package com.game.entities.special;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.assets.TextureAsset;
import com.game.entities.Entity;
import com.game.entities.decorations.WaterSprite;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.badlogic.gdx.graphics.Color.BLUE;
import static com.game.ViewVals.PPM;
import static com.game.world.BodyType.ABSTRACT;
import static com.game.world.FixtureType.WATER;

public class Water extends Entity {

    public Water(GameContext2d gameContext, RectangleMapObject rectObj) {
        super(gameContext);
        Rectangle bounds = rectObj.getRectangle();
        addComponent(spriteComponent(gameContext, bounds));
        addComponent(bodyComponent(bounds));
        // addComponent(shapeComponent(bounds));
        int rows = (int) (bounds.height / PPM);
        int cols = (int) (bounds.width / PPM);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Vector2 pos = new Vector2(bounds.x + x * PPM, bounds.y + y * PPM);
                WaterSprite waterSprite = new WaterSprite(gameContext, pos, y == rows - 1);
                gameContext.addEntity(waterSprite);
            }
        }
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

    protected SpriteComponent spriteComponent(GameContext2d gameContext, Rectangle bounds) {
        TextureRegion waterRegion = gameContext.getAsset(TextureAsset.WATER.getSrc(), TextureAtlas.class)
                .findRegion("Water");
        Sprite sprite = new Sprite(waterRegion);
        sprite.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public int getSpriteRenderPriority() {
                return 4;
            }

            @Override
            public float getAlpha() {
                return .5f;
            }

        });
    }

}
