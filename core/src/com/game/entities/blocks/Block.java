package com.game.entities.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.entities.decorations.DecorativeSprite;
import com.game.graph.GraphComponent;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;

import java.util.ArrayList;
import java.util.List;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.world.FixtureType.*;

public class Block extends Entity {

    public Block(Rectangle bounds, Vector2 friction) {
        this(bounds, friction, false, false, false, false, false);
    }

    public Block(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn,
                 boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        addComponent(defineGraphComponent());
        addComponent(defineBodyComponent(bounds, friction, resistance, gravityOn,
                wallSlideLeft, wallSlideRight, feetSticky));
    }

    public List<DecorativeSprite> generateDecorativeBlocks(TextureRegion textureRegion) {
        List<DecorativeSprite> decorativeSprites = new ArrayList<>();
        Vector2 size = getComponent(BodyComponent.class).getSize().scl(1f / PPM);
        for (int i = 0; i < (int) size.x; i++) {
            for (int j = 0; j < (int) size.y; j++) {
                final int finalI = i; final int finalJ = j;
                decorativeSprites.add(new DecorativeSprite(textureRegion, new Vector2(PPM, PPM),
                        () -> getComponent(BodyComponent.class).getPosition().add(finalI * PPM, finalJ * PPM)));
            }
        }
        return decorativeSprites;
    }

    private BodyComponent defineBodyComponent(Rectangle bounds, Vector2 friction, boolean resistance, boolean gravityOn,
                                              boolean wallSlideLeft, boolean wallSlideRight, boolean feetSticky) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.set(bounds);
        bodyComponent.setFriction(friction);
        bodyComponent.setGravityOn(gravityOn);
        bodyComponent.setAffectedByResistance(resistance);
        Fixture block = new Fixture(this, BLOCK);
        block.set(bodyComponent.getCollisionBox());
        bodyComponent.addFixture(block);
        if (wallSlideLeft) {
            Fixture leftWallSlide = new Fixture(this, WALL_SLIDE_SENSOR);
            leftWallSlide.setSize(PPM / 3f, bodyComponent.getCollisionBox().height - PPM / 3f);
            leftWallSlide.setOffset(-bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(leftWallSlide);
        }
        if (wallSlideRight) {
            Fixture rightWallSlide = new Fixture(this, WALL_SLIDE_SENSOR);
            rightWallSlide.setSize(PPM / 3f, bodyComponent.getCollisionBox().height - PPM / 3f);
            rightWallSlide.setOffset(bodyComponent.getCollisionBox().width / 2f, 0f);
            bodyComponent.addFixture(rightWallSlide);
        }
        if (feetSticky) {
            Fixture feetSticker = new Fixture(this, FEET_STICKER);
            feetSticker.setSize(bodyComponent.getCollisionBox().width, PPM / 3f);
            feetSticker.setOffset(0f, (bodyComponent.getCollisionBox().height / 2f) - 2f);
            bodyComponent.addFixture(feetSticker);
        }
        return bodyComponent;
    }

    private GraphComponent defineGraphComponent() {
        GraphComponent graphComponent = new GraphComponent();
        graphComponent.addSupplier(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> List.of(this));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle groundBounds = new Rectangle();
            groundBounds.setSize(bodyBounds.width + 5f, 1f);
            Vector2 center = centerPoint(bodyBounds).add(0f, (bodyBounds.height / 2f) + (PPM / 2f));
            groundBounds.setCenter(center);
            return groundBounds;
        }, () -> List.of("Ground"));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle leftBounds = new Rectangle();
            leftBounds.setSize(1f, bodyBounds.height + 5f);
            Vector2 center = centerPoint(bodyBounds).sub((bodyBounds.width / 2f) + (PPM / 2f), 0f);
            leftBounds.setCenter(center);
            return leftBounds;
        }, () -> List.of("LeftWall"));
        graphComponent.addSupplier(() -> {
            Rectangle bodyBounds = getComponent(BodyComponent.class).getCollisionBox();
            Rectangle rightBounds = new Rectangle();
            rightBounds.setSize(1f, bodyBounds.height + 5f);
            Vector2 center = centerPoint(bodyBounds).add((bodyBounds.width / 2f) + (PPM / 2f), 0f);
            rightBounds.setCenter(center);
            return rightBounds;
        }, () -> List.of("RightWall"));
        return graphComponent;
    }

}
