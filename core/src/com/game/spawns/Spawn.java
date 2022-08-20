package com.game.spawns;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

import static com.game.utils.UtilMethods.rectToBBox;

@Getter
@RequiredArgsConstructor
public class Spawn {

    private final GameContext2d gameContext;
    private final Supplier<Entity> spawnSupplier;
    private final Rectangle spawnBounds;

    private Entity entity;
    private boolean inCamBounds;
    private boolean wasInCamBounds;

    @Setter
    private Supplier<Boolean> doSpawn = () -> true;

    public void update(Camera camera) {
        if (entity != null && entity.isDead()) {
            entity = null;
        }
        wasInCamBounds = inCamBounds;
        inCamBounds = camera.frustum.boundsInFrustum(rectToBBox(spawnBounds));
        if (entity == null && !wasInCamBounds && inCamBounds && doSpawn.get()) {
            entity = spawnSupplier.get();
            gameContext.addEntity(entity);
        }
    }

    public void cull() {
        if (entity == null) {
            return;
        }
        entity.setDead(true);
        entity = null;
    }

}
