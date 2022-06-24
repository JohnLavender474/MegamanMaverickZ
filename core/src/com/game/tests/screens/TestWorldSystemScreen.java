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
import com.game.Entity;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorSystem;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugHandle;
import com.game.debugging.DebugSystem;
import com.game.megaman.behaviors.MegamanRun;
import com.game.utils.Direction;
import com.game.utils.FontHandle;
import com.game.world.*;

import static com.game.ConstVals.ViewVals.*;
import static com.game.ConstVals.ViewVals.PPM;

public class TestWorldSystemScreen extends ScreenAdapter {

    private Entity player;
    private WorldSystem worldSystem;
    private DebugSystem debugSystem;
    private BehaviorSystem behaviorSystem;

    private Viewport uiViewport;
    private SpriteBatch spriteBatch;
    private Viewport playgroundViewport;
    private ShapeRenderer shapeRenderer;
    private FontHandle collisionFont;

    private boolean runningLeft;
    private boolean runningRight;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        collisionFont =  new FontHandle("Megaman10Font.ttf", 6);
        collisionFont.getFont().setColor(Color.WHITE);
        collisionFont.getPosition().set(-VIEW_WIDTH * PPM / 4f, -VIEW_HEIGHT * PPM / 4f);
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
    }

    private void defineBlocks() {
        // first
        Entity entity1 = new Entity();
        BodyComponent bodyComponent1 = new BodyComponent(BodyType.STATIC);
        bodyComponent1.getCollisionBox().set(0f, 0f, 20f * PPM, PPM);
        Fixture block1 = new Fixture(entity1, FixtureType.BLOCK);
        block1.getFixtureBox().set(0f, 0f, 20f * PPM, PPM);
        bodyComponent1.getFixtures().add(block1);
        entity1.addComponent(bodyComponent1);
        DebugComponent debugComponent1 = new DebugComponent();
        debugComponent1.getDebugHandles().add(new DebugHandle(
                bodyComponent1::getCollisionBox, Color.RED));
        entity1.addComponent(debugComponent1);
        worldSystem.addEntity(entity1);
        debugSystem.addEntity(entity1);
        // second
        Entity entity2 = new Entity();
        BodyComponent bodyComponent2 = new BodyComponent(BodyType.STATIC);
        bodyComponent2.getCollisionBox().set(23f * PPM, 0f, 20f * PPM, PPM);
        Fixture block2 = new Fixture(entity2, FixtureType.BLOCK);
        block2.getFixtureBox().set(23f * PPM, 0f, 20f * PPM, PPM);
        bodyComponent2.getFixtures().add(block2);
        entity2.addComponent(bodyComponent2);
        DebugComponent debugComponent2 = new DebugComponent();
        debugComponent2.getDebugHandles().add(new DebugHandle(
                bodyComponent2::getCollisionBox, Color.RED));
        entity2.addComponent(debugComponent2);
        worldSystem.addEntity(entity2);
        debugSystem.addEntity(entity2);
    }

    private void definePlayer() {
        // Player
        player = new Entity();
        // Body component
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.getCollisionBox().set(3f * PPM, 3f * PPM, PPM, PPM);
        bodyComponent.setPreProcess((delta) -> {
            if (bodyComponent.getVelocity().y < 0f && !bodyComponent.isColliding(Direction.DOWN)) {
                bodyComponent.setGravity(-50f * PPM);
            } else {
                bodyComponent.setGravity(-20f * PPM);
            }
        });
        Fixture feet = new Fixture(player, FixtureType.FEET);
        feet.getFixtureBox().setSize(10f, 3f);
        feet.getOffset().set(0f, -PPM / 2f);
        bodyComponent.getFixtures().add(feet);
        Fixture head = new Fixture(player, FixtureType.HEAD);
        head.getFixtureBox().setSize(15f, 3f);
        head.getOffset().set(0f, PPM / 2f);
        bodyComponent.getFixtures().add(head);
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
                                !bodyComponent.is(BodySense.FEET_ON_GROUND) :
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
        behaviorComponent.getBehaviors().add(jump);
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
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        collisionFont.setText("Up " + bodyComponent.isColliding(Direction.UP) + ", " +
                                      "Down " + bodyComponent.isColliding(Direction.DOWN));
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        collisionFont.draw(spriteBatch);
        spriteBatch.end();
        boolean wasRunningRight = runningRight;
        runningRight = Gdx.input.isKeyPressed(Keys.RIGHT);
        if (runningRight && bodyComponent.getVelocity().x < MegamanRun.RUN_SPEED * PPM) {
            bodyComponent.applyImpulse(PPM * 0.5f, 0f);
        }
        if (wasRunningRight && !runningRight) {
            bodyComponent.getVelocity().x = 0f;
        }
        boolean wasRunningLeft = runningLeft;
        runningLeft = Gdx.input.isKeyPressed(Keys.LEFT);
        if (runningLeft && bodyComponent.getVelocity().x > -MegamanRun.RUN_SPEED * PPM) {
            bodyComponent.applyImpulse(-PPM * 0.5f, 0f);
        }
        if (wasRunningLeft && !runningLeft) {
            bodyComponent.getVelocity().x = 0f;
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
