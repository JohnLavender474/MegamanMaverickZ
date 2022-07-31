package com.game.spawns;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collection;

import static com.game.utils.UtilMethods.rectToBBox;

@Getter
@RequiredArgsConstructor
public class SpawnManager {

    private final Camera camera;
    private final Collection<Rectangle> playerSpawns;
    private final Collection<Spawn> spawns;

    @Setter
    private Rectangle currentPlayerSpawn;

    public void update() {
        spawns.forEach(spawn -> spawn.update(camera));
        playerSpawns.stream().filter(playerSpawn -> camera.frustum.boundsInFrustum(rectToBBox(playerSpawn)))
                .findFirst().ifPresent(this::setCurrentPlayerSpawn);
    }

}
