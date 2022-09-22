package com.game.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.entities.enemies.*;
import com.game.entities.megaman.Megaman;
import com.game.spawns.SpawnLocation;

import java.util.function.Supplier;

import static com.game.core.constants.RenderingGround.PLAYGROUND;
import static com.game.utils.UtilMethods.getPoint;
import static com.game.utils.enums.Position.*;
import static com.game.utils.enums.Position.BOTTOM_CENTER;

public class EnemyFactory {
    
    public static Supplier<Entity> get(GameContext2d gameContext, RectangleMapObject spawnObj,
                                       Supplier<Megaman> megamanSupplier) {
        switch (spawnObj.getName()) {
            case "met" -> {
                return () -> new Met(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "sniper_joe" -> {
                return () -> new SniperJoe(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "suction_roller" -> {
                return () -> new SuctionRoller(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "floating_can" -> {
                return () -> new SpawnLocation(gameContext, gameContext.getViewport(PLAYGROUND).getCamera(),
                        spawnObj.getRectangle(), 4, 3f, () -> new FloatingCan(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER)));
            }
            case "bat" -> {
                return () -> new Bat(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), TOP_CENTER));
            }
            case "dragonfly" -> {
                return () -> new Dragonfly(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), CENTER));
            }
            case "matasaburo" -> {
                return () -> new Matasaburo(gameContext, megamanSupplier,
                        getPoint(spawnObj.getRectangle(), BOTTOM_CENTER));
            }
            case "spring_head" -> {
                return () -> new SpringHead(gameContext, megamanSupplier, spawnObj);
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }
    
}
