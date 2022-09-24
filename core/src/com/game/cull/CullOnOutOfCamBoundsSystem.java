package com.game.cull;

import com.badlogic.gdx.graphics.Camera;
import com.game.Entity;
import com.game.System;

import static com.game.utils.UtilMethods.*;

public class CullOnOutOfCamBoundsSystem extends System {

    private final Camera camera;

    public CullOnOutOfCamBoundsSystem(Camera camera) {
        super(CullOutOfCamBoundsComponent.class);
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        CullOutOfCamBoundsComponent cullComponent = entity.getComponent(CullOutOfCamBoundsComponent.class);
        if (camera.frustum.boundsInFrustum(rectToBBox(cullComponent.getBounds()))) {
            cullComponent.resetCullTimer();
            return;
        }
        cullComponent.updateCullTimer(delta);
        if (cullComponent.isCullTimerFinished()) {
            entity.setDead(true);
        }
    }

}
