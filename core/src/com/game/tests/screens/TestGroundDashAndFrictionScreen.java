package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.behaviors.MegamanRun;
import com.game.utils.*;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import static com.game.ConstVals.ViewVals.*;
import static com.game.ConstVals.ViewVals.PPM;

public class TestGroundDashAndFrictionScreen extends ScreenAdapter {

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
    private FontHandle message1;
    private FontHandle message2;
    private FontHandle message3;
    private FontHandle message4;

    private boolean runningLeft;
    private boolean runningRight;

    private final Timer groundSlideTimer = new Timer(0.5f);

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        message1 = new FontHandle("Megaman10Font.ttf", 6);
        message1.getFont().setColor(Color.WHITE);
        message1.getPosition().set(-VIEW_WIDTH * PPM / 4f, -VIEW_HEIGHT * PPM / 4f);
        message2 = new FontHandle("Megaman10Font.ttf", 6);
        message2.getFont().setColor(Color.WHITE);
        message2.getPosition().set(-VIEW_WIDTH * PPM / 4f, (-VIEW_HEIGHT * PPM / 4f) + PPM);
        message3 = new FontHandle("Megaman10Font.ttf", 6);
        message3.getFont().setColor(Color.WHITE);
        message3.getPosition().set(-VIEW_WIDTH * PPM / 4f, (-VIEW_HEIGHT * PPM / 4f) + 2 * PPM);
        message4 = new FontHandle("Megaman10Font.ttf", 6);
        message4.getFont().setColor(Color.WHITE);
        message4.getPosition().set(-VIEW_WIDTH * PPM / 4f, (-VIEW_HEIGHT * PPM / 4f) + 3 * PPM);
        shapeRenderer = new ShapeRenderer();
        uiViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        worldSystem = new WorldSystem(
                new WorldContactListenerImpl(),
                new Vector2(1.05f, 1.05f), WorldVals.FIXED_TIME_STEP);
        debugSystem = new DebugSystem(shapeRenderer,
                                      (OrthographicCamera) playgroundViewport.getCamera());
        behaviorSystem = new BehaviorSystem();
        defineBlocks();
        definePlayer();
    }

    private void defineBlocks() {
        // first
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent(BodyType.STATIC);
        bodyComponent1.set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.setFriction(.035f, .15f);
        Fixture block1 = new Fixture(entity1, FixtureType.BLOCK);
        block1.set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.addFixture(block1);
        entity1.addComponent(bodyComponent1);
        DebugComponent debugComponent1 = new DebugComponent();
        debugComponent1.addDebugHandle(new DebugHandle(
                block1::getFixtureBox, Color.RED));
        entity1.addComponent(debugComponent1);
        worldSystem.addEntity(entity1);
        debugSystem.addEntity(entity1);
        // second
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent(BodyType.STATIC);
        bodyComponent2.setFriction(1.05f, 1.15f);
        bodyComponent2.set(23f * PPM, 0f, 20f * PPM, PPM);
        Fixture block2 = new Fixture(entity2, FixtureType.BLOCK);
        block2.set(23f * PPM, 0f, 20f * PPM, PPM);
        bodyComponent2.addFixture(block2);
        entity2.addComponent(bodyComponent2);
        DebugComponent debugComponent2 = new DebugComponent();
        debugComponent2.addDebugHandle(new DebugHandle(
                block2::getFixtureBox, Color.RED));
        entity2.addComponent(debugComponent2);
        worldSystem.addEntity(entity2);
        debugSystem.addEntity(entity2);
        // third
        Entity entity3 = new Entity();
        BodyComponent bodyComponent3 = new BodyComponent(BodyType.STATIC);
        bodyComponent3.setFriction(1.5f, 1.65f);
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
        Behavior jump = new Behavior() {

            private boolean isJumping;

            @Override
            protected boolean evaluate(float delta) {
                return isJumping ?
                        // case 1
                        bodyComponent.getVelocity().y >= 0f &&
                                Gdx.input.isKeyPressed(Keys.W) &&
                                !bodyComponent.isColliding(Direction.DOWN) :
                        // case 2
                        Gdx.input.isKeyJustPressed(Keys.W) &&
                                bodyComponent.isColliding(Direction.DOWN);
            }

            @Override
            protected void init() {
                isJumping = true;
                bodyComponent.applyImpulse(0f, 16f * PPM);
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
        Behavior groundSlide = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    message1.setText("STOP: Left or right pressed");
                    return false;
                }
                if (player.isFacing(Facing.LEFT) && bodyComponent.isColliding(Direction.LEFT)) {
                    message1.setText("STOP: Face and collide left");
                    return false;
                }
                if (player.isFacing(Facing.RIGHT) && bodyComponent.isColliding(Direction.RIGHT)) {
                    message1.setText("STOP: Face and collide right");
                    return false;
                }
                boolean bool3 = bodyComponent.is(BodySense.FEET_ON_GROUND);
                if (!bool3) {
                    message1.setText("Feet not on ground");
                    return false;
                }
                if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) &&
                        bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    message1.setText("CONTINUE: Head touch block");
                    return true;
                }
                boolean bool4 = !groundSlideTimer.isFinished();
                if (!bool4) {
                    message1.setText("Timer finished");
                    return false;
                }
                if (!behaviorComponent.is(BehaviorType.GROUND_SLIDING)) {
                    return Gdx.input.isKeyPressed(Keys.DOWN) &&
                            Gdx.input.isKeyJustPressed(Keys.D);
                } else {
                    return Gdx.input.isKeyPressed(Keys.DOWN) &&
                            Gdx.input.isKeyPressed(Keys.D);
                }
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(BehaviorType.GROUND_SLIDING);
            }

            @Override
            protected void act(float delta) {
                groundSlideTimer.update(delta);
                float x = 20f * PPM;
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
                    bodyComponent.setVelocityX(-5f * PPM);
                } else {
                    bodyComponent.setVelocityX(5f * PPM);
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
                new DebugHandle(feet::getFixtureBox, Color.GREEN));
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
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.setFacing(Facing.LEFT);
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.setFacing(Facing.RIGHT);
        }
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        message1.draw(spriteBatch);
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        message2.setText("Feet on ground: " + bodyComponent.is(BodySense.FEET_ON_GROUND));
        message2.draw(spriteBatch);
        message3.setText("Velocity: " +
                                 Math.round(bodyComponent.getVelocity().x) + ", " +
                                 Math.round(bodyComponent.getVelocity().y));
        message3.draw(spriteBatch);
        message4.setText("Friction: " + bodyComponent.getFriction());
        message4.draw(spriteBatch);
        spriteBatch.end();
        boolean wasRunningRight = runningRight;
        runningRight = Gdx.input.isKeyPressed(Keys.RIGHT);
        if (runningRight && bodyComponent.getVelocity().x < MegamanRun.RUN_SPEED * PPM) {
            bodyComponent.applyImpulse(PPM * 0.5f, 0f);
        }
        if (wasRunningRight && !runningRight) {
            bodyComponent.setVelocityX(0f);
        }
        boolean wasRunningLeft = runningLeft;
        runningLeft = Gdx.input.isKeyPressed(Keys.LEFT);
        if (runningLeft && bodyComponent.getVelocity().x > -MegamanRun.RUN_SPEED * PPM) {
            bodyComponent.applyImpulse(-PPM * 0.5f, 0f);
        }
        if (wasRunningLeft && !runningLeft) {
            bodyComponent.setVelocityX(0f);
        }
        worldSystem.update(delta);
        debugSystem.update(delta);
        behaviorSystem.update(delta);
        playgroundViewport.getCamera().position.x = bodyComponent.getCollisionBox().x;
        playgroundViewport.apply();
        uiViewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }

}
