package com.game.tests.core;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.game.utils.interfaces.Resettable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collection;

import static com.game.utils.UtilMethods.rectToBBox;

@Getter
@RequiredArgsConstructor
public class TestEntitySpawnManager implements Resettable {

    private final Camera camera;
    private final ShapeRenderer shapeRenderer;
    private final Collection<Rectangle> playerSpawns;
    private final Collection<TestEntitySpawn> enemySpawns;

    @Setter
    private Rectangle currentPlayerSpawn;

    public void update() {
        boolean isDrawing = shapeRenderer.isDrawing();
        if (!isDrawing) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }
        shapeRenderer.setColor(Color.YELLOW);
        enemySpawns.forEach(enemySpawn -> {
            enemySpawn.update(camera);
            Rectangle spawnBounds = enemySpawn.getSpawnBounds();
            shapeRenderer.rect(spawnBounds.x, spawnBounds.y, spawnBounds.width, spawnBounds.height);

        });
        shapeRenderer.setColor(Color.BLUE);
        playerSpawns.forEach(playerSpawn -> shapeRenderer.rect(
                playerSpawn.x, playerSpawn.y, playerSpawn.width, playerSpawn.height));
        if (!isDrawing) {
            shapeRenderer.end();
        }
        playerSpawns.stream().filter(playerSpawn -> camera.frustum.boundsInFrustum(rectToBBox(playerSpawn)))
                .findFirst().ifPresent(this::setCurrentPlayerSpawn);
    }

    @Override
    public void reset() {
        enemySpawns.forEach(enemySpawn -> {
            enemySpawn.cull();
            enemySpawn.resetCamBounds();
        });
    }

    public int amountOfEnemySpawnsInCamBounds() {
        return (int) enemySpawns.stream().filter(TestEntitySpawn::isInCamBounds).count();
    }

}
