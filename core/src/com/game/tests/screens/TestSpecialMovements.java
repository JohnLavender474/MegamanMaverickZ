package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.WorldVals;
import com.game.entities.Entity;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorSystem;
import com.game.behaviors.BehaviorType;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugHandle;
import com.game.debugging.DebugSystem;
import com.game.entities.megaman.behaviors.MegamanRun;
import com.game.utils.*;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.ViewVals.*;
import static com.game.ConstVals.ViewVals.PPM;

public class TestSpecialMovements extends ScreenAdapter {

    private enum W_BUTTON_TASK {
        JUMP,
        AIR_DASH
    }

    @Getter
    @Setter
    private static class TestPlayer extends Entity implements Faceable {
        private Facing facing = Facing.RIGHT;
    }

    private TestPlayer player;
    private WorldSystem worldSystem;
    private DebugSystem debugSystem;
    private BehaviorSystem behaviorSystem;

    private Viewport uiViewport;
    private Viewport playgroundViewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private FontHandle message2;
    private FontHandle message3;
    private FontHandle message4;

    private boolean runningLeft;
    private boolean runningRight;

    private W_BUTTON_TASK w_button_task = W_BUTTON_TASK.JUMP;
    private final Timer airDashTimer = new Timer(0.25f);
    private final Timer groundSlideTimer = new Timer(0.35f);
    private final Timer wallJumpImpetusTimer = new Timer(0.2f);

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        message2 = new FontHandle("Megaman10Font.ttf", 6);
        message2.getFont().setColor(Color.WHITE);
        message2.getPosition().set(-VIEW_WIDTH * PPM / 3f, (-VIEW_HEIGHT * PPM / 4f));
        message3 = new FontHandle("Megaman10Font.ttf", 6);
        message3.getFont().setColor(Color.WHITE);
        message3.getPosition().set(-VIEW_WIDTH * PPM / 3f, (-VIEW_HEIGHT * PPM / 4f) + PPM);
        message4 = new FontHandle("Megaman10Font.ttf", 6);
        message4.getFont().setColor(Color.WHITE);
        message4.getPosition().set(-VIEW_WIDTH * PPM / 3f, (-VIEW_HEIGHT * PPM / 4f) + 2 * PPM);
        shapeRenderer = new ShapeRenderer();
        uiViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        worldSystem = new WorldSystem(
                new WorldContactListenerImpl(),
                WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP);
        debugSystem = new DebugSystem(shapeRenderer,
                                      (OrthographicCamera) playgroundViewport.getCamera());
        behaviorSystem = new BehaviorSystem();
        defineBlocks();
        definePlayer();
        wallJumpImpetusTimer.setToEnd();
    }

    private void defineBlocks() {
        // first
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent(BodyType.STATIC);
        bodyComponent1.set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.setFriction(.02f, .02f);
        Fixture block1 = new Fixture(entity1, FixtureType.BLOCK);
        block1.set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.addFixture(block1);
        entity1.addComponent(bodyComponent1);
        DebugComponent debugComponent1 = new DebugComponent();
        debugComponent1.addDebugHandle(new DebugHandle(
                block1::getFixtureBox, Color.GREEN));
        entity1.addComponent(debugComponent1);
        worldSystem.addEntity(entity1);
        debugSystem.addEntity(entity1);
        // second
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent(BodyType.STATIC);
        bodyComponent2.setFriction(.05f, .05f);
        bodyComponent2.set(23f * PPM, 0f, 20f * PPM, PPM);
        Fixture block2 = new Fixture(entity2, FixtureType.BLOCK);
        block2.set(23f * PPM, 0f, 20f * PPM, PPM);
        bodyComponent2.addFixture(block2);
        entity2.addComponent(bodyComponent2);
        DebugComponent debugComponent2 = new DebugComponent();
        debugComponent2.addDebugHandle(new DebugHandle(
                block2::getFixtureBox, Color.YELLOW));
        entity2.addComponent(debugComponent2);
        worldSystem.addEntity(entity2);
        debugSystem.addEntity(entity2);
        // third
        Entity entity3 = new Entity();
        BodyComponent bodyComponent3 = new BodyComponent(BodyType.STATIC);
        bodyComponent3.setFriction(.05f, .75f);
        bodyComponent3.set(10f * PPM, 2f * PPM, PPM, 30f * PPM);
        Fixture block3 = new Fixture(entity3, FixtureType.BLOCK);
        block3.set(10f * PPM, 2f * PPM, PPM, 30f * PPM);
        bodyComponent3.addFixture(block3);
        entity3.addComponent(bodyComponent3);
        DebugComponent debugComponent3 = new DebugComponent();
        debugComponent3.addDebugHandle(new DebugHandle(
                block3::getFixtureBox, Color.RED));
        entity3.addComponent(debugComponent3);
        worldSystem.addEntity(entity3);
        debugSystem.addEntity(entity3);
    }

    private void definePlayer() {
        // Player
        player = new TestPlayer();
        // Body component
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.set(3f * PPM, 3f * PPM, PPM, PPM);
        bodyComponent.setPreProcess((delta) -> {
            if (bodyComponent.getVelocity().y < 0f && !bodyComponent.isColliding(Direction.DOWN)) {
                bodyComponent.setGravity(-60f * PPM);
            } else {
                bodyComponent.setGravity(-20f * PPM);
            }
        });
        Fixture feet = new Fixture(player, FixtureType.FEET);
        feet.setSize(12f, 3f);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        Fixture head = new Fixture(player, FixtureType.HEAD);
        head.setSize(12f, 3f);
        head.setOffset(0f, PPM / 2f);
        bodyComponent.addFixture(head);
        player.addComponent(bodyComponent);
        // Behavior component
        BehaviorComponent behaviorComponent = new BehaviorComponent();
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
                return direction != null && (
                        (direction == Direction.LEFT && Gdx.input.isKeyPressed(Keys.LEFT)) ||
                                (direction == Direction.RIGHT && Gdx.input.isKeyPressed(Keys.RIGHT)) &&
                                        !bodyComponent.isColliding(Direction.DOWN)) &&
                        wallJumpImpetusTimer.isFinished();
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(BehaviorType.WALL_SLIDING);
                w_button_task = W_BUTTON_TASK.JUMP;
            }

            @Override
            protected void act(float delta) {}

            @Override
            protected void end() {
                behaviorComponent.setIsNot(BehaviorType.WALL_SLIDING);
                w_button_task = W_BUTTON_TASK.AIR_DASH;
            }

        };
        behaviorComponent.addBehavior(wallSlide);
        // Jump
        Behavior jump = new Behavior() {

            private boolean isJumping;

            @Override
            protected boolean evaluate(float delta) {
                if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                    return false;
                }
                return isJumping ?
                        // case 1
                        bodyComponent.getVelocity().y >= 0f &&
                                Gdx.input.isKeyPressed(Keys.W) &&
                                !bodyComponent.isColliding(Direction.DOWN) :
                        // case 2
                        w_button_task == W_BUTTON_TASK.JUMP &&
                                Gdx.input.isKeyJustPressed(Keys.W) &&
                                (bodyComponent.isColliding(Direction.DOWN) ||
                                        behaviorComponent.is(BehaviorType.WALL_SLIDING));
            }

            @Override
            protected void init() {
                isJumping = true;
                w_button_task = W_BUTTON_TASK.AIR_DASH;
                if (behaviorComponent.is(BehaviorType.WALL_SLIDING)) {
                    bodyComponent.applyImpulse((player.isFacing(Facing.LEFT) ? -1f : 1f) * 7f * PPM,
                                               15f * PPM);
                    wallJumpImpetusTimer.reset();
                } else {
                    bodyComponent.applyImpulse(0f, 18f * PPM);
                }
            }

            @Override
            protected void act(float delta) {}

            @Override
            protected void end() {
                bodyComponent.getVelocity().y = 0f;
                isJumping = false;
            }

        };
        behaviorComponent.addBehavior(jump);
        // Air dash
        Behavior airDash = new Behavior() {

            private boolean isAirDashing;

            @Override
            protected boolean evaluate(float delta) {
                boolean stop = behaviorComponent.is(BehaviorType.WALL_SLIDING) ||
                        bodyComponent.isColliding(Direction.DOWN) ||
                        airDashTimer.isFinished();
                if (stop) {
                    return false;
                }
                return isAirDashing ? Gdx.input.isKeyPressed(Keys.W) :
                        Gdx.input.isKeyJustPressed(Keys.W) && w_button_task == W_BUTTON_TASK.AIR_DASH;
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(BehaviorType.AIR_DASHING);
                bodyComponent.setGravityOn(false);
                w_button_task = W_BUTTON_TASK.JUMP;
                isAirDashing = true;
            }

            @Override
            protected void act(float delta) {
                airDashTimer.update(delta);
                float x = 12f * PPM;
                if (player.isFacing(Facing.LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
                bodyComponent.setVelocityY(0f);
            }

            @Override
            protected void end() {
                isAirDashing = false;
                airDashTimer.reset();
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
                if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    return false;
                }
                if (player.isFacing(Facing.LEFT) && bodyComponent.isColliding(Direction.LEFT)) {
                    return false;
                }
                if (player.isFacing(Facing.RIGHT) && bodyComponent.isColliding(Direction.RIGHT)) {
                    return false;
                }
                boolean bool3 = bodyComponent.is(BodySense.FEET_ON_GROUND);
                if (!bool3) {
                    return false;
                }
                if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) &&
                        bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return true;
                }
                boolean bool4 = !groundSlideTimer.isFinished();
                if (!bool4) {
                    return false;
                }
                if (!behaviorComponent.is(BehaviorType.GROUND_SLIDING)) {
                    return Gdx.input.isKeyPressed(Keys.DOWN) &&
                            Gdx.input.isKeyJustPressed(Keys.W);
                } else {
                    return Gdx.input.isKeyPressed(Keys.DOWN) &&
                            Gdx.input.isKeyPressed(Keys.W);
                }
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(BehaviorType.GROUND_SLIDING);
            }

            @Override
            protected void act(float delta) {
                groundSlideTimer.update(delta);
                float x = 12f * PPM;
                if (player.isFacing(Facing.LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
            }

            @Override
            protected void end() {
                groundSlideTimer.reset();
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
        debugComponent.getDebugHandles().add(
                new DebugHandle(bodyComponent::getCollisionBox,Color.BLUE));
        debugComponent.getDebugHandles().add(
                new DebugHandle(feet::getFixtureBox, Color.PINK));
        debugComponent.getDebugHandles().add(
                new DebugHandle(head::getFixtureBox, Color.ORANGE));
        player.addComponent(debugComponent);
        // Add entity to systems
        behaviorSystem.addEntity(player);
        worldSystem.addEntity(player);
        debugSystem.addEntity(player);
    }

    @Override
    public void render(float delta) {
        if (wallJumpImpetusTimer.isFinished()) {
            BehaviorComponent behaviorComponent = player.getComponent(BehaviorComponent.class);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                player.setFacing(behaviorComponent.is(BehaviorType.WALL_SLIDING) ?
                                         Facing.RIGHT : Facing.LEFT);
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                player.setFacing(behaviorComponent.is(BehaviorType.WALL_SLIDING) ?
                                         Facing.LEFT : Facing.RIGHT);
            }
        } else {
            wallJumpImpetusTimer.update(delta);
        }
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        BehaviorComponent behaviorComponent = player.getComponent(BehaviorComponent.class);
        if ((bodyComponent.isColliding(Direction.DOWN) && bodyComponent.getVelocity().y < 0f) ||
                behaviorComponent.is(BehaviorType.WALL_SLIDING)) {
            w_button_task = W_BUTTON_TASK.JUMP;
        }
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        message2.setText("Player velocity: " + bodyComponent.getVelocity());
        message2.draw(spriteBatch);
        message3.setText("W button task: " + w_button_task);
        message3.draw(spriteBatch);
        message4.setText("Active behaviors: " + behaviorComponent.getActiveBehaviors());
        message4.draw(spriteBatch);
        spriteBatch.end();
        runningRight = Gdx.input.isKeyPressed(Keys.RIGHT);
        if (runningRight && bodyComponent.getVelocity().x < MegamanRun.RUN_SPEED * PPM) {
            bodyComponent.applyImpulse(PPM * 0.75f, 0f);
        }
        runningLeft = Gdx.input.isKeyPressed(Keys.LEFT);
        if (runningLeft && bodyComponent.getVelocity().x > -MegamanRun.RUN_SPEED * PPM) {
            bodyComponent.applyImpulse(-PPM * 0.75f, 0f);
        }
        worldSystem.update(delta);
        debugSystem.update(delta);
        behaviorSystem.update(delta);
        playgroundViewport.getCamera().position.x = bodyComponent.getCollisionBox().x;
        playgroundViewport.getCamera().position.y = bodyComponent.getCollisionBox().y;
        playgroundViewport.apply();
        uiViewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }

}
