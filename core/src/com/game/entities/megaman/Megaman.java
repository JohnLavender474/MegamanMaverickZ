package com.game.entities.megaman;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.ConstVals.TextureAssets;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.behaviors.BehaviorComponent;
import com.game.contracts.Faceable;
import com.game.contracts.Facing;
import com.game.health.HealthComponent;
import com.game.entities.megaman.behaviors.MegamanRun;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.sprites.SpriteComponent;
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
public class Megaman implements Entity, Faceable, LevelCameraFocusable {

    public static final float MEGAMAN_GRAVITY = -7f;

    public enum A_ButtonAction {
        JUMP,
        AIR_DASH
    }

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;

    private final Timer gravityTimer = new Timer(0.5f);
    private final Map<String, Rectangle> spawns;
    private final MegamanStats megamanStats;
    private A_ButtonAction aButtonAction;
    private Facing facing = Facing.RIGHT;
    private String currentSpawn;

    /**
     * Instantiates a new Megaman.
     */
    public Megaman(GameContext2d gameContext, Map<String, Rectangle> spawns, MegamanStats megamanStats) {
        this.spawns = spawns;
        this.megamanStats = megamanStats;
        addComponent(defineBodyComponent());
        addComponent(defineHealthComponent());
        addComponent(defineBehaviorComponent(gameContext));
        addComponent(defineAnimationComponent(gameContext.getAsset(TextureAssets.MEGAMAN_TEXTURE_ATLAS, TextureAtlas.class)));
        addComponent(defineSpriteComponent());
    }

    @Override
    public Rectangle getCurrentBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    @Override
    public Rectangle getPriorBoundingBox() {
        return getComponent(BodyComponent.class).getPriorCollisionBox();
    }

    private BodyComponent defineBodyComponent() {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setSize(0.75f * PPM, 1.5f * PPM);
        // TODO: Define gravity, friction, etc.
        return bodyComponent;
    }

    private HealthComponent defineHealthComponent() {
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.setHealthUpdater(delta -> {
            // TODO: Define health updater
        });
        return healthComponent;
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
        animations.put("Climb", new TimedAnimation(textureAtlas.findRegion("Climb"), 2, 0.125f));
        animations.put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, 0.15f}));
        animations.put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 3, 0.05f));
        animations.put("Run", new TimedAnimation(textureAtlas.findRegion("Run"), 4, 0.125f));
        animations.put("Jump", new TimedAnimation(textureAtlas.findRegion("Run"), 4, 0.125f));
        animations.put("WallSlide", new TimedAnimation(textureAtlas.findRegion("WallSlide")));
        animations.put("GroundSlide", new TimedAnimation(textureAtlas.findRegion("GroundSlide")));
        animations.put("AirDash", new TimedAnimation(textureAtlas.findRegion("AirDash")));
        // Define animation component
        Animator animator = new Animator(keySupplier, animations);
        AnimationComponent animationComponent = new AnimationComponent(animator);
        return animationComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.setSpriteUpdater(delta -> {
            // TODO: Define sprite updater
        });
        return spriteComponent;
    }

}
