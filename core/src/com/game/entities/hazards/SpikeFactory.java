package com.game.entities.hazards;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;

import java.util.ArrayList;
import java.util.List;

import static com.game.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;

public class SpikeFactory {

    public static void create(GameContext2d gameContext, RectangleMapObject spawnObj) {
        Rectangle bounds = spawnObj.getRectangle();
        Vector2 pos = getPoint(bounds, BOTTOM_LEFT);
        int x = (int) (bounds.width / PPM);
        int y = (int) (bounds.height / PPM);
        List<Spike> spikes = new ArrayList<>();
        String textureKey = spawnObj.getProperties().get("textureKey", String.class);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                spikes.add(new Spike(gameContext, pos.cpy().add(i * PPM, j * PPM), textureKey));
            }
        }
        gameContext.addEntities(spikes);
    }

}
