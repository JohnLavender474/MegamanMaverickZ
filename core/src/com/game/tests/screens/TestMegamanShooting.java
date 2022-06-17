package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.TextureAssets;
import com.game.ConstVals.WorldVals;
import com.game.entities.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.AnimationSystem;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorSystem;
import com.game.behaviors.BehaviorType;
import com.game.controllers.*;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugHandle;
import com.game.debugging.DebugSystem;
import com.game.entities.megaman.behaviors.MegamanRun;
import com.game.entities.projectiles.Projectile;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteSystem;
import com.game.utils.*;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.*;
import static com.game.behaviors.BehaviorType.*;
import static com.game.controllers.ControllerButtonStatus.*;
import static com.game.controllers.ControllerUtils.*;

public class TestMegamanShooting extends ScreenAdapter {

    private enum W_BUTTON_TASK {
        JUMP,
        AIR_DASH
    }

    @Getter @Setter
    private static class TestPlayer extends Entity implements Faceable {
        private Facing facing = Facing.RIGHT;
        private W_BUTTON_TASK w_button_task = W_BUTTON_TASK.JUMP;
        private final Timer airDashTimer = new Timer(0.25f);
        private final Timer groundSlideTimer = new Timer(0.35f);
        private final Timer wallJumpImpetusTimer = new Timer(0.2f);
    }

    private static class TestWorldContactListener implements WorldContactListener {

        @Override
        public void beginContact(Contact contact, float delta) {
            if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
                Entity entity = contact.maskFirstEntity();
                entity.getComponent(BodyComponent.class).setIs(BodySense.FEET_ON_GROUND);
                if (entity instanceof TestPlayer testPlayer) {
                    testPlayer.setW_button_task(W_BUTTON_TASK.JUMP);
                }
            } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
                contact.maskFirstEntity().getComponent(BodyComponent.class).setIs(BodySense.HEAD_TOUCHING_BLOCK);
            } else if (contact.acceptMask(FixtureType.PROJECTILE)) {
                Projectile projectile = (Projectile) contact.maskFirstEntity();
                projectile.hit(contact.getMask().second());
            }
        }

        @Override
        public void continueContact(Contact contact, float delta) {
            if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
                contact.maskFirstEntity().getComponent(BodyComponent.class).setIs(BodySense.FEET_ON_GROUND);
            } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
                contact.maskFirstEntity().getComponent(BodyComponent.class).setIs(BodySense.HEAD_TOUCHING_BLOCK);
            }
        }

        @Override
        public void endContact(Contact contact, float delta) {
            if (contact.acceptMask(FixtureType.FEET, FixtureType.BLOCK)) {
                Entity entity = contact.maskFirstEntity();
                entity.getComponent(BodyComponent.class).setIsNot(BodySense.FEET_ON_GROUND);
                if (entity instanceof TestPlayer testPlayer) {
                    testPlayer.setW_button_task(W_BUTTON_TASK.AIR_DASH);
                }
            } else if (contact.acceptMask(FixtureType.HEAD, FixtureType.BLOCK)) {
                contact.maskFirstEntity().getComponent(BodyComponent.class).setIsNot(BodySense.HEAD_TOUCHING_BLOCK);
            }
        }

    }

    private static class TestController implements IController {

        private final Map<ControllerButton, ControllerButtonStatus> controllerButtons = new HashMap<>() {{
            for (ControllerButton controllerButton : ControllerButton.values()) {
                put(controllerButton, IS_RELEASED);
            }
        }};

        @Override
        public boolean isJustPressed(ControllerButton controllerButton) {
            return controllerButtons.get(controllerButton) == IS_JUST_PRESSED;
        }

        @Override
        public boolean isPressed(ControllerButton controllerButton) {
            return controllerButtons.get(controllerButton) == IS_JUST_PRESSED || controllerButtons.get(controllerButton) == IS_PRESSED;
        }

        @Override
        public boolean isJustReleased(ControllerButton controllerButton) {
            return controllerButtons.get(controllerButton) == IS_JUST_RELEASED;
        }

        @Override
        public void updateController() {
            for (ControllerButton controllerButton : ControllerButton.values()) {
                ControllerButtonStatus status = controllerButtons.get(controllerButton);
                boolean isControllerButtonPressed = isControllerConnected() ?
                        isControllerButtonPressed(controllerButton.getControllerBindingCode()) :
                        isKeyboardButtonPressed(controllerButton.getKeyboardBindingCode());
                if (isControllerButtonPressed) {
                    if (status == IS_RELEASED || status == IS_JUST_RELEASED) {
                        controllerButtons.replace(controllerButton, IS_JUST_PRESSED);
                    } else {
                        controllerButtons.replace(controllerButton, IS_PRESSED);
                    }
                } else if (status == IS_RELEASED || status == IS_JUST_RELEASED) {
                    controllerButtons.replace(controllerButton, IS_RELEASED);
                } else {
                    controllerButtons.replace(controllerButton, IS_JUST_RELEASED);
                }
            }
        }

    }

    private TestPlayer player;
    private TestController testController;
    private WorldSystem worldSystem;
    private DebugSystem debugSystem;
    private SpriteSystem spriteSystem;
    private AnimationSystem animationSystem;
    private ControllerSystem controllerSystem;
    private BehaviorSystem behaviorSystem;
    private TextureAtlas textureAtlas;

    private Viewport uiViewport;
    private Viewport playgroundViewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private FontHandle message;

    @Override
    public void show() {
        testController = new TestController();
        textureAtlas = new TextureAtlas(Gdx.files.internal(TextureAssets.MEGAMAN_TEXTURE_ATLAS));
        spriteBatch = new SpriteBatch();
        message = new FontHandle("Megaman10Font.ttf", 6);
        message.getFont().setColor(Color.WHITE);
        message.setText("NULL");
        message.getPosition().set(-VIEW_WIDTH * PPM / 3f, -VIEW_HEIGHT * PPM / 4f);
        shapeRenderer = new ShapeRenderer();
        uiViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        worldSystem = new WorldSystem(new TestWorldContactListener(), WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP);
        debugSystem = new DebugSystem(shapeRenderer, (OrthographicCamera) playgroundViewport.getCamera());
        controllerSystem = new ControllerSystem(testController);
        behaviorSystem = new BehaviorSystem();
        spriteSystem = new SpriteSystem((OrthographicCamera) playgroundViewport.getCamera(), spriteBatch);
        animationSystem = new AnimationSystem();
        defineBlocks();
        definePlayer();
        player.wallJumpImpetusTimer.setToEnd();
    }

    private void defineBlocks() {
        // first
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent(BodyType.STATIC);
        bodyComponent1.setGravityOn(false);
        bodyComponent1.set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.setFriction(.035f, .05f);
        Fixture block1 = new Fixture(entity1, FixtureType.BLOCK);
        block1.set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.addFixture(block1);
        entity1.addComponent(bodyComponent1);
        DebugComponent debugComponent1 = new DebugComponent();
        debugComponent1.addDebugHandle(new DebugHandle(block1::getFixtureBox, Color.GREEN));
        entity1.addComponent(debugComponent1);
        worldSystem.addEntity(entity1);
        debugSystem.addEntity(entity1);
        // second
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent(BodyType.STATIC);
        bodyComponent2.setGravityOn(false);
        bodyComponent2.setFriction(.035f, .05f);
        bodyComponent2.set(23f * PPM, 0f, 20f * PPM, PPM);
        Fixture block2 = new Fixture(entity2, FixtureType.BLOCK);
        block2.set(23f * PPM, 0f, 20f * PPM, PPM);
        bodyComponent2.addFixture(block2);
        entity2.addComponent(bodyComponent2);
        DebugComponent debugComponent2 = new DebugComponent();
        debugComponent2.addDebugHandle(new DebugHandle(block2::getFixtureBox, Color.YELLOW));
        entity2.addComponent(debugComponent2);
        worldSystem.addEntity(entity2);
        debugSystem.addEntity(entity2);
        // third
        Entity entity3 = new Entity();
        BodyComponent bodyComponent3 = new BodyComponent(BodyType.STATIC);
        bodyComponent3.setGravityOn(false);
        bodyComponent3.setFriction(.05f, .75f);
        bodyComponent3.set(10f * PPM, 2f * PPM, PPM, 30f * PPM);
        Fixture block3 = new Fixture(entity3, FixtureType.BLOCK);
        block3.set(10f * PPM, 2f * PPM, PPM, 30f * PPM);
        bodyComponent3.addFixture(block3);
        entity3.addComponent(bodyComponent3);
        DebugComponent debugComponent3 = new DebugComponent();
        debugComponent3.addDebugHandle(new DebugHandle(block3::getFixtureBox, Color.RED));
        entity3.addComponent(debugComponent3);
        worldSystem.addEntity(entity3);
        debugSystem.addEntity(entity3);
        // fourth
        Entity entity4 = new Entity();
        BodyComponent bodyComponent4 = new BodyComponent(BodyType.STATIC);
        bodyComponent4.setGravityOn(false);
        bodyComponent4.setFriction(.05f, .75f);
        bodyComponent4.set(4f * PPM, 1.65f * PPM, 10f * PPM, PPM);
        Fixture block4 = new Fixture(entity4, FixtureType.BLOCK);
        block4.set(4f * PPM, 1.5f * PPM, 10f * PPM, PPM);
        bodyComponent4.addFixture(block4);
        entity4.addComponent(bodyComponent4);
        DebugComponent debugComponent4 = new DebugComponent();
        debugComponent4.addDebugHandle(new DebugHandle(block4::getFixtureBox, Color.RED));
        entity4.addComponent(debugComponent4);
        worldSystem.addEntity(entity4);
        debugSystem.addEntity(entity4);
    }

    private void definePlayer() {
        // Player
        player = new TestPlayer();
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPosition(3f * PPM, 3f * PPM);
        bodyComponent.setWidth(0.75f * PPM);
        bodyComponent.setPreProcess((delta) -> {
            if (behaviorComponent.is(GROUND_SLIDING)) {
                bodyComponent.setHeight(0.45f * PPM);
            } else {
                bodyComponent.setHeight(0.95f * PPM);
            }
            if (bodyComponent.getVelocity().y < 0f && !bodyComponent.isColliding(Direction.DOWN)) {
                bodyComponent.setGravity(-60f * PPM);
            } else {
                bodyComponent.setGravity(-20f * PPM);
            }
        });
        Fixture feet = new Fixture(player, FixtureType.FEET);
        feet.setSize(9f, 3f);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        Fixture head = new Fixture(player, FixtureType.HEAD);
        head.setSize(7f, 5f);
        head.setOffset(0f, PPM / 2f);
        bodyComponent.addFixture(head);
        player.addComponent(bodyComponent);
        // Controller component
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.addControllerAdapter(ControllerButton.LEFT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (player.wallJumpImpetusTimer.isFinished()) {
                    player.setFacing(behaviorComponent.is(WALL_SLIDING) ? Facing.RIGHT : Facing.LEFT);
                }
                behaviorComponent.setIs(RUNNING);
                if (bodyComponent.getVelocity().x > -MegamanRun.RUN_SPEED * PPM) {
                    bodyComponent.applyImpulse(-PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased(float delta) {
                behaviorComponent.setIsNot(RUNNING);
            }

        });
        controllerComponent.addControllerAdapter(ControllerButton.RIGHT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (player.wallJumpImpetusTimer.isFinished()) {
                    player.setFacing(behaviorComponent.is(WALL_SLIDING) ? Facing.LEFT : Facing.RIGHT);
                }
                behaviorComponent.setIs(RUNNING);
                if (bodyComponent.getVelocity().x < MegamanRun.RUN_SPEED * PPM) {
                    bodyComponent.applyImpulse(PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased(float delta) {
                behaviorComponent.setIsNot(RUNNING);
            }

        });
        player.addComponent(controllerComponent);
        // Wall slide
        Behavior wallSlide = new Behavior() {

            private Direction direction;

            @Override
            protected boolean evaluate(float delta) {
                if (bodyComponent.isColliding(Direction.LEFT)) {
                    direction = Direction.LEFT;
                } else if (bodyComponent.isColliding(Direction.RIGHT)) {
                    direction = Direction.RIGHT;
                } else {
                    direction = null;
                }
                return direction != null && player.wallJumpImpetusTimer.isFinished() && !bodyComponent.is(BodySense.FEET_ON_GROUND) &&
                        ((direction == Direction.LEFT && testController.isPressed(ControllerButton.LEFT)) ||
                                (direction == Direction.RIGHT && testController.isPressed(ControllerButton.RIGHT)));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(WALL_SLIDING);
                player.setW_button_task(W_BUTTON_TASK.JUMP);
            }

            @Override
            protected void act(float delta) {}

            @Override
            protected void end() {
                behaviorComponent.setIsNot(WALL_SLIDING);
                player.setW_button_task(W_BUTTON_TASK.AIR_DASH);
            }

        };
        behaviorComponent.addBehavior(wallSlide);
        // Jump
        Behavior jump = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (testController.isPressed(ControllerButton.DOWN)) {
                    return false;
                }
                if (bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return false;
                }
                return behaviorComponent.is(JUMPING) ?
                        // case 1
                        bodyComponent.getVelocity().y >= 0f && testController.isPressed(ControllerButton.A) &&
                                !bodyComponent.isColliding(Direction.DOWN) :
                        // case 2
                        player.w_button_task == W_BUTTON_TASK.JUMP && testController.isJustPressed(ControllerButton.A) &&
                                (bodyComponent.isColliding(Direction.DOWN) || behaviorComponent.is(WALL_SLIDING));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(JUMPING);
                // player.setW_button_task(W_BUTTON_TASK.AIR_DASH);
                if (behaviorComponent.is(WALL_SLIDING)) {
                    bodyComponent.applyImpulse((player.isFacing(Facing.LEFT) ? -1f : 1f) * 9f * PPM, 16f * PPM);
                    player.wallJumpImpetusTimer.reset();
                } else {
                    bodyComponent.applyImpulse(0f, 18f * PPM);
                }
            }

            @Override
            protected void act(float delta) {}

            @Override
            protected void end() {
                // player.setW_button_task(W_BUTTON_TASK.AIR_DASH);
                behaviorComponent.setIsNot(JUMPING);
                bodyComponent.getVelocity().y = 0f;
            }

        };
        behaviorComponent.addBehavior(jump);
        // Air dash
        Behavior airDash = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                boolean stop = behaviorComponent.is(WALL_SLIDING) || bodyComponent.isColliding(Direction.DOWN) || player.airDashTimer.isFinished();
                if (stop) {
                    return false;
                }
                return behaviorComponent.is(AIR_DASHING) ? testController.isPressed(ControllerButton.A) :
                        testController.isJustPressed(ControllerButton.A) && player.getW_button_task() == W_BUTTON_TASK.AIR_DASH;
            }

            @Override
            protected void init() {
                bodyComponent.setGravityOn(false);
                behaviorComponent.setIs(BehaviorType.AIR_DASHING);
                player.setW_button_task(W_BUTTON_TASK.JUMP);
            }

            @Override
            protected void act(float delta) {
                player.airDashTimer.update(delta);
                bodyComponent.setVelocityY(0f);
                if (bodyComponent.isColliding(Direction.LEFT) || bodyComponent.isColliding(Direction.RIGHT)) {
                    return;
                }
                float x = 12f * PPM;
                if (player.isFacing(Facing.LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
            }

            @Override
            protected void end() {
                player.airDashTimer.reset();
                bodyComponent.setGravityOn(true);
                behaviorComponent.setIsNot(BehaviorType.AIR_DASHING);
                if (player.isFacing(Facing.LEFT)) {
                    bodyComponent.applyImpulse(-5f * PPM, 0f);
                } else {
                    bodyComponent.applyImpulse(5f * PPM, 0f);
                }
            }
        };
        behaviorComponent.addBehavior(airDash);
        // Ground slide
        Behavior groundSlide = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) && bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return true;
                }
                boolean bool3 = bodyComponent.is(BodySense.FEET_ON_GROUND);
                if (!bool3) {
                    return false;
                }
                boolean bool4 = !player.groundSlideTimer.isFinished();
                if (!bool4) {
                    return false;
                }
                if (!behaviorComponent.is(BehaviorType.GROUND_SLIDING)) {
                    return testController.isPressed(ControllerButton.DOWN) && testController.isJustPressed(ControllerButton.A);
                } else {
                    return testController.isPressed(ControllerButton.DOWN) && testController.isPressed(ControllerButton.A);
                }
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(BehaviorType.GROUND_SLIDING);
            }

            @Override
            protected void act(float delta) {
                player.groundSlideTimer.update(delta);
                if (bodyComponent.isColliding(Direction.LEFT) || bodyComponent.isColliding(Direction.RIGHT)) {
                    return;
                }
                float x = 12f * PPM;
                if (player.isFacing(Facing.LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
            }

            @Override
            protected void end() {
                player.groundSlideTimer.reset();
                behaviorComponent.setIsNot(BehaviorType.GROUND_SLIDING);
                if (player.isFacing(Facing.LEFT)) {
                    bodyComponent.applyImpulse(-5f * PPM, 0f);
                } else {
                    bodyComponent.applyImpulse(5f * PPM, 0f);
                }
            }
        };
        behaviorComponent.addBehavior(groundSlide);
        player.addComponent(behaviorComponent);
        // Debug component
        DebugComponent debugComponent = new DebugComponent();
        debugComponent.getDebugHandles().add(new DebugHandle(bodyComponent::getCollisionBox, Color.BLUE));
        debugComponent.getDebugHandles().add(new DebugHandle(feet::getFixtureBox, Color.PINK));
        debugComponent.getDebugHandles().add(new DebugHandle(head::getFixtureBox, Color.ORANGE));
        player.addComponent(debugComponent);
        // Sprite component
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.getSprite().setSize(1.65f * PPM, 1.35f * PPM);
        spriteComponent.setSpriteUpdater(delta -> {
            Sprite sprite = spriteComponent.getSprite();
            if (behaviorComponent.is(WALL_SLIDING)) {
                sprite.setFlip(player.isFacing(Facing.RIGHT), false);
            } else {
                sprite.setFlip(player.isFacing(Facing.LEFT), false);
            }
            Vector2 bottomCenter = UtilMethods.bottomCenterPoint(bodyComponent.getCollisionBox());
            sprite.setCenterX(bottomCenter.x);
            sprite.setY(bottomCenter.y);
            if (behaviorComponent.is(GROUND_SLIDING)) {
                sprite.translateY(-.035f * PPM);
            }
        });
        player.addComponent(spriteComponent);
        // Animation component
        Supplier<String> keySupplier = () -> {
            String key;
            if (behaviorComponent.is(DAMAGED)) {
                key = "Damaged";
            } else if (behaviorComponent.is(AIR_DASHING)) {
                key = "AirDash";
            } else if (behaviorComponent.is(GROUND_SLIDING)) {
                key = "GroundSlide";
            } else if (behaviorComponent.is(WALL_SLIDING)) {
                key = "WallSlide";
            } else if (behaviorComponent.is(JUMPING) || !bodyComponent.isColliding(Direction.DOWN)) {
                key = "Jump";
            } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && behaviorComponent.is(RUNNING)) {
                key = "Run";
            } else if (behaviorComponent.is(CLIMBING)) {
                key = "Climb";
            } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && Math.abs(bodyComponent.getVelocity().x) > 3f) {
                key = "SlipSlide";
            } else {
                key = "Stand";
            }
            return key;
        };
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
                textureAtlas.findRegion("Jump")));
        animations.put("WallSlide", new TimedAnimation(
                textureAtlas.findRegion("WallSlide")));
        animations.put("GroundSlide", new TimedAnimation(
                textureAtlas.findRegion("GroundSlide")));
        animations.put("AirDash", new TimedAnimation(
                textureAtlas.findRegion("AirDash")));
        animations.put("SlipSlide", new TimedAnimation(
                textureAtlas.findRegion("SlipSlide")));
        Animator animator = new Animator(keySupplier, animations);
        AnimationComponent animationComponent = new AnimationComponent(animator);
        player.addComponent(animationComponent);
        // Add entity to systems
        behaviorSystem.addEntity(player);
        worldSystem.addEntity(player);
        debugSystem.addEntity(player);
        spriteSystem.addEntity(player);
        animationSystem.addEntity(player);
        controllerSystem.addEntity(player);
    }

    @Override
    public void render(float delta) {
        testController.updateController();
        if (!player.wallJumpImpetusTimer.isFinished()) {
            player.wallJumpImpetusTimer.update(delta);
        }
        worldSystem.update(delta);
        debugSystem.update(delta);
        behaviorSystem.update(delta);
        controllerSystem.update(delta);
        animationSystem.update(delta);
        spriteSystem.update(delta);
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        Vector2 priorCenter = UtilMethods.bottomCenterPoint(bodyComponent.getPriorCollisionBox());
        Vector2 currentCenter = UtilMethods.bottomCenterPoint(bodyComponent.getCollisionBox());
        Vector2 interpolatedCenter = UtilMethods.interpolate(priorCenter, currentCenter, delta);
        message.setText("Player w button task: " + player.getW_button_task());
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        message.draw(spriteBatch);
        spriteBatch.end();
        playgroundViewport.getCamera().position.x = interpolatedCenter.x;
        playgroundViewport.getCamera().position.y = interpolatedCenter.y;
        playgroundViewport.apply();
        uiViewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }

    @Override
    public void dispose() {
        textureAtlas.dispose();
    }

}
