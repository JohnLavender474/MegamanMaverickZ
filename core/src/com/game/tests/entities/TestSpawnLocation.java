package com.game.tests.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.game.Entity;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import com.game.debugging.DebugRectComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.game.utils.UtilMethods.*;

public class TestSpawnLocation extends Entity {

    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final List<IEntity> entities = new ArrayList<>();
    private final Supplier<IEntity> entitySupplier;
    private final Rectangle bounds;
    private final Camera camera;
    private final Timer timer;
    private final int max;

    public TestSpawnLocation(IEntitiesAndSystemsManager entitiesAndSystemsManager, Camera camera, Rectangle bounds,
                             int max, float duration, Supplier<IEntity> entitySupplier) {
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        this.entitySupplier = entitySupplier;
        this.timer = new Timer(duration);
        this.bounds = bounds;
        this.camera = camera;
        this.max = max;
        addComponent(defineUpdatableComponent());
        addComponent(defineDebugRectComponent());
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            entities.removeIf(IEntity::isDead);
            boolean isInCamBounds = camera.frustum.boundsInFrustum(rectToBBox(bounds));
            if (!isInCamBounds || entities.size() >= max) {
                timer.reset();
                return;
            }
            timer.update(delta);
            if (timer.isFinished()) {
                IEntity entity = entitySupplier.get();
                entitiesAndSystemsManager.addEntity(entity);
                entities.add(entity);
                timer.reset();
            }
        });
    }

    private DebugRectComponent defineDebugRectComponent() {
        return new DebugRectComponent(() -> bounds, () -> Color.BLUE);
    }

}
