package com.game.spawns;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.Entity;
import com.game.GameContext2d;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.game.utils.UtilMethods.*;

@Getter
@RequiredArgsConstructor
public class EntitySpawn {

    private final GameContext2d gameContext;
    private final RectangleMapObject rectangleMapObject;
    private final Function<RectangleMapObject, Entity> spawnFunction;

    private Entity entity;
    private boolean inCamBounds;
    private boolean wasInCamBounds;

    @Setter
    private Supplier<Boolean> doSpawn = () -> true;

    public void update(Camera camera) {
        if (entity.isDead()) {
            entity = null;
        }
        wasInCamBounds = inCamBounds;
        inCamBounds = camera.frustum.boundsInFrustum(rectToBBox(rectangleMapObject.getRectangle()));
        if (entity == null && !wasInCamBounds && inCamBounds && doSpawn.get()) {
            entity = spawnFunction.apply(rectangleMapObject);
            gameContext.addEntity(entity);
        }
    }

}
