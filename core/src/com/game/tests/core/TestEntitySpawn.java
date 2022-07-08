package com.game.tests.core;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.lwjgl.Sys;

import java.util.function.Supplier;

import static com.game.utils.UtilMethods.rectToBBox;

@Getter
@RequiredArgsConstructor
public class TestEntitySpawn {

    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Supplier<IEntity> spawnSupplier;
    private final Rectangle spawnBounds;

    private IEntity entity;
    private boolean inCamBounds;
    private boolean wasInCamBounds;

    @Setter
    private Supplier<Boolean> doSpawn = () -> true;

    public void update(Camera camera) {
        if (entity != null && entity.isDead()) {
            System.out.println("Culled " + entity.getClass().getSimpleName());
            entity = null;
        }
        wasInCamBounds = inCamBounds;
        inCamBounds = camera.frustum.boundsInFrustum(rectToBBox(spawnBounds));
        if (entity == null && !wasInCamBounds && inCamBounds && doSpawn.get()) {
            entity = spawnSupplier.get();
            entitiesAndSystemsManager.addEntity(entity);
            System.out.println("Spawned " + entity.getClass().getSimpleName());
        }
    }

}
