package com.game.tests.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Component;
import com.game.ConstVals.WorldVals;
import com.game.Entity;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.screens.levels.LevelCameraManager;
import com.game.utils.Direction;
import com.game.utils.FontHandle;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.WorldContactListenerImpl;
import com.game.world.WorldSystem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.ConstVals.ViewVals.*;
import static com.game.screens.levels.LevelScreen.LEVEL_CAM_TRANS_DURATION;
import static com.game.screens.levels.LevelScreen.MEGAMAN_DELTA_ON_CAM_TRANS;

public class TestCameraRoomShiftScreen extends ScreenAdapter {

    @Getter
    @Setter
    private static class TestEntity implements Entity, LevelCameraFocusable {

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        @Override
        public Rectangle getCurrentBoundingBox() {
            return getComponent(BodyComponent.class).getCollisionBox();
        }

        @Override
        public Rectangle getPriorBoundingBox() {
            return getComponent(BodyComponent.class).getPriorCollisionBox();
        }

    }

    private TestEntity player;
    private WorldSystem worldSystem;
    private BodyComponent bodyComponent;
    private LevelCameraManager levelCameraManager;
    private List<Rectangle> grid;
    private Map<Rectangle, String> gameRooms;

    private Viewport uiViewport;
    private Viewport playgroundViewport;
    private FontHandle fontHandle;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private Direction direction;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        worldSystem = new WorldSystem(
                new WorldContactListenerImpl(),
                WorldVals.AIR_RESISTANCE, WorldVals.FIXED_TIME_STEP);
        playgroundViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        uiViewport.getCamera().position.x = 0f;
        uiViewport.getCamera().position.y = 0f;
        fontHandle = new FontHandle("Megaman10Font.ttf", 4);
        fontHandle.getFont().setColor(Color.WHITE);
        fontHandle.getPosition().set(-VIEW_WIDTH * PPM / 2f, -VIEW_HEIGHT * PPM / 3f);
        definePlayer();
        defineGameRooms();
        levelCameraManager = new LevelCameraManager(
                playgroundViewport.getCamera(), new Timer(1f),
                gameRooms, player);
        grid = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            for (int j = 0; j < 60; j++) {
                float x = (-30f * PPM) + (i * PPM);
                float y = (-30f * PPM) + (j * PPM);
                grid.add(new Rectangle(x, y, PPM, PPM));
            }
        }
    }

    private void definePlayer() {
        player = new TestEntity();
        bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.set(15f * PPM, 15f * PPM, PPM, PPM);
        player.addComponent(bodyComponent);
        worldSystem.addEntity(player);
    }

    private void defineGameRooms() {
        gameRooms = new HashMap<>();
        Rectangle startRoom = new Rectangle(0f, 0f, 30f * PPM, 30f * PPM);
        gameRooms.put(startRoom, "Start");
        Rectangle leftRoom = new Rectangle(-30f * PPM, 0f, 30f * PPM, 30f * PPM);
        gameRooms.put(leftRoom, "Left");
        Rectangle rightRoom = new Rectangle(30f * PPM, 0f, 30f * PPM, 30f * PPM);
        gameRooms.put(rightRoom, "Right");
        Rectangle upperRoom = new Rectangle(0f, 30f * PPM, 30f * PPM, 30f * PPM);
        gameRooms.put(upperRoom, "Upper");
        Rectangle lowerRoom = new Rectangle(0f, -30f * PPM, 30f * PPM, 30f * PPM);
        gameRooms.put(lowerRoom, "Lower");
    }

    @Override
    public void render(float delta) {
        worldSystem.update(delta);
        levelCameraManager.update(delta);
        shapeRenderer.setProjectionMatrix(playgroundViewport.getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        gameRooms.keySet().forEach(rectangle ->
            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        shapeRenderer.setColor(Color.GREEN);
        grid.forEach(rectangle ->
                shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        shapeRenderer.setColor(Color.BLUE);
        Rectangle collisionBox = bodyComponent.getCollisionBox();
        shapeRenderer.rect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
        shapeRenderer.end();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        fontHandle.setText("Trans: " + levelCameraManager.getTransitionState() +
                                   ", Player Bounds: " + bodyComponent.getCollisionBox());
        fontHandle.draw(spriteBatch);
        spriteBatch.end();
        uiViewport.apply();
        playgroundViewport.apply();
        if (levelCameraManager.getTransitionState() != null) {
            switch (levelCameraManager.getTransitionState()) {
                case BEGIN -> bodyComponent.getVelocity().setZero();
                case CONTINUE -> {
                    Direction direction = levelCameraManager.getTransitionDirection();
                    switch (direction) {
                        case UP -> bodyComponent.getCollisionBox().y +=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case DOWN -> bodyComponent.getCollisionBox().y -=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case LEFT -> bodyComponent.getCollisionBox().x -=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                        case RIGHT -> bodyComponent.getCollisionBox().x +=
                                (MEGAMAN_DELTA_ON_CAM_TRANS * PPM * delta) / LEVEL_CAM_TRANS_DURATION;
                    }
                }
            }
        } else {
            Direction oldDirection = direction;
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                direction = Direction.LEFT;
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                direction = Direction.RIGHT;
            } else if (Gdx.input.isKeyPressed(Keys.UP)) {
                direction = Direction.UP;
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                direction = Direction.DOWN;
            } else {
                direction = null;
            }
            if (oldDirection != direction) {
                bodyComponent.getVelocity().setZero();
            }
            if (direction == Direction.LEFT) {
                bodyComponent.setVelocityX(-5f * PPM);
            } else if (direction == Direction.RIGHT) {
                bodyComponent.setVelocityX(5f * PPM);
            } else if (direction == Direction.UP) {
                bodyComponent.setVelocityY(5f * PPM);
            } else if (direction == Direction.DOWN) {
                bodyComponent.setVelocityY(-5f * PPM);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height);
        playgroundViewport.update(width, height);
    }

}
