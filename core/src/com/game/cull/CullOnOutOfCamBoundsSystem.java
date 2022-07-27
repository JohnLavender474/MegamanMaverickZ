package com.game.cull;

import com.badlogic.gdx.graphics.Camera;
import com.game.System;
import com.game.core.IEntity;
import com.game.utils.UtilMethods;

import java.util.Set;

public class CullOnOutOfCamBoundsSystem extends System {

    private final Camera camera;

    public CullOnOutOfCamBoundsSystem(Camera camera) {
        super(Set.of(CullOutOfCamBoundsComponent.class));
        this.camera = camera;
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        CullOutOfCamBoundsComponent cullComponent = entity.getComponent(CullOutOfCamBoundsComponent.class);
        if (camera.frustum.boundsInFrustum(UtilMethods.rectToBBox(cullComponent.getBounds()))) {
            cullComponent.resetCullTimer();
            return;
        }
        cullComponent.updateCullTimer(delta);
        if (cullComponent.isCullTimerFinished()) {
            entity.setDead(true);
        }
    }

}
