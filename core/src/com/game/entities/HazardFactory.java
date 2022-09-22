package com.game.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.entities.hazards.LaserBeamer;
import com.game.entities.hazards.Saw;

public class HazardFactory {

    public static Entity get(GameContext2d gameContext, RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "saw" -> {
                return new Saw(gameContext, spawnObj);
            }
            case "laser_beamer" -> {
                return new LaserBeamer(gameContext, spawnObj);
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

}
