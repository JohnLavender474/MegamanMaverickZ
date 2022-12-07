package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.Entity;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;

import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.CONVEYOR_BELT;
import static com.game.utils.UtilMethods.equalsAny;
import static com.game.utils.enums.Position.BOTTOM_LEFT;

public class ConveyorBeltPart extends Entity {

    public ConveyorBeltPart(GameContext2d gameContext, Vector2 pos, String part, boolean isMovingLeft) {
        super(gameContext);
        addComponent(spriteComponent(new Rectangle(pos.x, pos.y, PPM, PPM / 2f)));
        addComponent(animationComponent(part, isMovingLeft));
    }

    protected SpriteComponent spriteComponent(Rectangle conveyorBounds) {
        Sprite sprite = new Sprite();
        sprite.setSize(PPM, PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public int getSpriteRenderPriority() {
                return -1;
            }

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(conveyorBounds);
                position.setData(BOTTOM_LEFT);
                return true;
            }

        });
    }

    protected AnimationComponent animationComponent(String part, boolean isMovingLeft) {
        TextureAtlas conveyorAtlas = gameContext.getAsset(CONVEYOR_BELT.getSrc(), TextureAtlas.class);
        TextureRegion region;
        if (equalsAny(part, "left", "right")) {
            if (part.equals("left")) {
                region = conveyorAtlas.findRegion(isMovingLeft ? "LeftPart-MoveLeft" : "LeftPart-MoveRight");
            } else {
                region = conveyorAtlas.findRegion(isMovingLeft ? "RightPart-MoveLeft" : "RightPart-MoveRight");
            }
        } else {
            region = conveyorAtlas.findRegion("MiddlePart");
        }
        TimedAnimation animation = new TimedAnimation(region, 2, .15f);
        return new AnimationComponent(animation);
    }

}
