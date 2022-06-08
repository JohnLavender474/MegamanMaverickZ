package com.game.megaman;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.game.ConstVals.TextureAssets;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.Entity;
import com.game.behaviors.BehaviorComponent;
import com.game.controllers.ControllerAdapter;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerComponent;
import com.game.health.HealthComponent;
import com.game.megaman.behaviors.MegamanRun;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.sprites.SpriteComponent;
import com.game.utils.*;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;

/**
 * Megaman implementation of {@link Entity}.
 */
@Getter
@Setter
public class Megaman extends Entity implements Faceable, LevelCameraFocusable, Resettable {

    public enum A_ButtonAction {
        JUMP,
        AIR_DASH
    }

    private final MegamanStats megamanStats;
    private final Set<Direction> movementDirections = new HashSet<>();
    private final Timer dashingTimer = new Timer(0.75f);
    private final Timer wallJumpingTimer = new Timer(0.1f);
    // Set either facing left or right
    private Facing facing = Facing.RIGHT;
    // Action for the A button, no action if null
    private A_ButtonAction aButtonAction;
    // Reset to false before every contact detection cycle, set to true if head fixture touches block
    private boolean headTouchingBlock;

    /**
     * Instantiates a new Megaman.
     */
    public Megaman(GameContext2d gameContext, MegamanStats megamanStats) {
        this.megamanStats = megamanStats;
        addComponent(defineBodyComponent());
        addComponent(defineHealthComponent());
        addComponent(defineControllerComponent());
        addComponent(defineBehaviorComponent(gameContext));
        addComponent(defineAnimationComponent(gameContext.loadAsset(
                TextureAssets.MEGAMAN_TEXTURE_ATLAS, TextureAtlas.class)));
        addComponent(defineSpriteComponent());
    }

    @Override
    public void reset() {
        getComponent(HealthComponent.class).reset();
    }

    @Override
    public Rectangle getBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    private BodyComponent defineBodyComponent() {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.getCollisionBox().setSize(0.75f * PPM, 1.5f * PPM);
        bodyComponent.setPreProcess((delta) -> headTouchingBlock = false);
        // TODO: define body component
        return bodyComponent;
    }

    private HealthComponent defineHealthComponent() {
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.setHealthUpdater((delta) -> {

        });
        return healthComponent;
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.getControllerAdapters().put(ControllerButton.LEFT, new ControllerAdapter() {
            @Override
            public void onPressContinued(float delta) {
                ControllerAdapter.super.onPressContinued(delta);
            }
        });
        return controllerComponent;
    }

    private BehaviorComponent defineBehaviorComponent(GameContext2d gameContext) {
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        behaviorComponent.getBehaviors().add(new MegamanRun(this, gameContext));
        return behaviorComponent;
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        // Key supplier
        Supplier<String> keySupplier = () -> {
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (behaviorComponent.is(DAMAGED)) {
                return "Damaged";
            } else if (behaviorComponent.is(AIR_DASHING)) {
                return "AirDash";
            } else if (behaviorComponent.is(GROUND_SLIDING)) {
                return "GroundSlide";
            } else if (behaviorComponent.is(WALL_SLIDING)) {
                return "WallSlide";
            } else if (behaviorComponent.is(JUMPING)) {
                return "Jump";
            } else if (behaviorComponent.is(RUNNING)) {
                return "Run";
            } else if (behaviorComponent.is(CLIMBING)) {
                return "Climb";
            } else {
                return "Stand";
            }
        };
        // Define animations
        Map<String, TimedAnimation> animations = new HashMap<>();
        animations.put("Climb", new TimedAnimation(
                textureAtlas.findRegion("Climb"), 2, 0.125f));
        animations.put("Stand", new TimedAnimation(
                textureAtlas.findRegion("Stand"), new float[]{1.5f, 0.15f}));
        animations.put("Damaged", new TimedAnimation(
                textureAtlas.findRegion("Damaged"), 3, 0.05f));
        animations.put("Run", new TimedAnimation(
                textureAtlas.findRegion("Run"), 4, 0.125f));
        animations.put("Jump", new TimedAnimation(
                textureAtlas.findRegion("Run"), 4, 0.125f));
        animations.put("WallSlide", new TimedAnimation(
                textureAtlas.findRegion("WallSlide")));
        animations.put("GroundSlide", new TimedAnimation(
                textureAtlas.findRegion("GroundSlide")));
        animations.put("AirDash", new TimedAnimation(
                textureAtlas.findRegion("AirDash")));
        // Define component
        Animator animator = new Animator(keySupplier, animations);
        AnimationComponent animationComponent = new AnimationComponent(animator);
        return animationComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.setSpriteUpdater(sprite -> {

        });
        return spriteComponent;
    }

}
