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
import com.game.utils.Timer;
import com.game.world.*;

import static com.game.ConstVals.ViewVals.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.megaman.Megaman.MEGAMAN_GRAVITY;

public class TestWorldSystem1 extends ScreenAdapter {

    private Entity player;
    private WorldSystem worldSystem;
    private DebugSystem debugSystem;
    private BehaviorSystem behaviorSystem;

    private Viewport uiViewport;
    private SpriteBatch spriteBatch;
    private Viewport playgroundViewport;
    private ShapeRenderer shapeRenderer;
    private FontHandle collisionFont;

    private final Timer gravityTimer = new Timer(1f);

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
                new WorldContactListenerImpl(), WorldVals.FIXED_TIME_STEP);
        debugSystem = new DebugSystem(shapeRenderer,
                                      (OrthographicCamera) playgroundViewport.getCamera());
        behaviorSystem = new BehaviorSystem();
        defineBlocks();
        definePlayer();
    }

    private void defineBlocks() {
        Entity entity = new Entity();
        BodyComponent bodyComponent = new BodyComponent(BodyType.STATIC);
        bodyComponent.getCollisionBox().set(0f, 0f, 20f * PPM, PPM);
        Fixture block = new Fixture(FixtureType.BLOCK);
        block.getFixtureBox().set(0f, 0f, 20f * PPM, PPM);
        bodyComponent.getFixtures().add(block);
        entity.addComponent(bodyComponent);
        DebugComponent debugComponent = new DebugComponent();
        debugComponent.getDebugHandles().add(new DebugHandle(
                bodyComponent::getCollisionBox, Color.RED));
        entity.addComponent(debugComponent);
        worldSystem.addEntity(entity);
        debugSystem.addEntity(entity);
    }

    private void definePlayer() {
        // Player
        player = new Entity();
        // Body component
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.getCollisionBox().set(3f * PPM, 3f * PPM, PPM, PPM);
        bodyComponent.getGravity().set(0f, MEGAMAN_GRAVITY);
        bodyComponent.setPreProcess((delta) -> {
            // gravity
            if (bodyComponent.isColliding(Direction.DOWN)) {
                gravityTimer.reset();
                bodyComponent.getGravity().y = -0.25f * PPM;
            } else {
                bodyComponent.getGravity().y = -7f * PPM;
            }
            // gravity scale
            if (bodyComponent.isColliding(Direction.DOWN)) {
                bodyComponent.getGravityScalar().y = 0f;
            } else if (bodyComponent.isColliding(Direction.LEFT) ||
                    bodyComponent.isColliding(Direction.RIGHT)) {
                bodyComponent.getGravityScalar().y = 0.2f;
            } else {
                bodyComponent.getGravityScalar().y = 1f;
            }
        });
        Fixture feet = new Fixture(FixtureType.FEET);
        feet.getFixtureBox().setSize(15f, 3f);
        feet.getOffset().set(0f, -PPM / 2f);
        bodyComponent.getFixtures().add(feet);
        Fixture head = new Fixture(FixtureType.HEAD);
        head.getFixtureBox().setSize(15f, 3f);
        head.getOffset().set(0f, PPM / 2f);
        bodyComponent.getFixtures().add(head);
        player.addComponent(bodyComponent);
        // Behavior component
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        Behavior jump = new Behavior() {

            private boolean isJumping;
            private final Timer timer = new Timer(0.65f);

            @Override
            protected boolean evaluate(float delta) {
                return isJumping ?
                        // case 1
                        bodyComponent.getImpulse().y >= 0f &&
                                !bodyComponent.isColliding(Direction.DOWN) &&
                                Gdx.input.isKeyPressed(Keys.W) :
                        // case 2
                        Gdx.input.isKeyJustPressed(Keys.W) &&
                                bodyComponent.isColliding(Direction.DOWN);
            }

            @Override
            protected void init() {
                isJumping = true;
            }

            @Override
            protected void act(float delta) {
                timer.update(delta);
                float inverseRatio = 20f * timer.getRatio();
                bodyComponent.getImpulse().y += (20f - inverseRatio) * PPM;
            }

            @Override
            protected void end() {
                isJumping = false;
                timer.reset();
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
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            bodyComponent.getImpulse().x = -PPM * MegamanRun.RUN_SPEED_PER_SECOND;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            bodyComponent.getImpulse().x = PPM * MegamanRun.RUN_SPEED_PER_SECOND;
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
