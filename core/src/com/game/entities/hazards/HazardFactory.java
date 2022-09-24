package com.game.entities.hazards;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.GameContext2d;

public class HazardFactory {

    public static void create(GameContext2d gameContext, RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "saw" -> gameContext.addEntity(new Saw(gameContext, spawnObj));
            case "laser_beamer" -> gameContext.addEntity(new LaserBeamer(gameContext, spawnObj));
            case "spikes" -> SpikeFactory.create(gameContext, spawnObj);
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

}
