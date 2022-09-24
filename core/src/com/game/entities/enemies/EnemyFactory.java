package com.game.entities.enemies;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.entities.megaman.Megaman;
import com.game.spawns.SpawnLocation;

import java.util.function.Supplier;

import static com.game.sprites.RenderingGround.PLAYGROUND;
import static com.game.utils.UtilMethods.*;

public class EnemyFactory {
    
    public static Supplier<Entity> get(GameContext2d gameContext, RectangleMapObject spawnObj,
                                       Supplier<Megaman> megamanSupplier) {
        switch (spawnObj.getName()) {
            case "met" -> {
                return () -> new Met(gameContext, megamanSupplier,
                        bottomCenterPoint(spawnObj.getRectangle()));
            }
            case "sniper_joe" -> {
                return () -> new SniperJoe(gameContext, megamanSupplier,
                        bottomCenterPoint(spawnObj.getRectangle()));
            }
            case "suction_roller" -> {
                return () -> new SuctionRoller(gameContext, megamanSupplier,
                        bottomCenterPoint(spawnObj.getRectangle()));
            }
            case "floating_can" -> {
                return () -> new SpawnLocation(gameContext, gameContext.getViewport(PLAYGROUND).getCamera(),
                        spawnObj.getRectangle(), 4, 3f, () -> new FloatingCan(gameContext, megamanSupplier,
                        bottomCenterPoint(spawnObj.getRectangle())));
            }
            case "bat" -> {
                return () -> new Bat(gameContext, megamanSupplier,
                        topCenterPoint(spawnObj.getRectangle()));
            }
            case "dragonfly" -> {
                return () -> new Dragonfly(gameContext, megamanSupplier,
                        centerPoint(spawnObj.getRectangle()));
            }
            case "matasaburo" -> {
                return () -> new Matasaburo(gameContext, megamanSupplier,
                        bottomCenterPoint(spawnObj.getRectangle()));
            }
            case "spring_head" -> {
                return () -> new SpringHead(gameContext, megamanSupplier, spawnObj);
            }
            case "mag_fly" -> {
                return () -> new MagFly(gameContext, megamanSupplier, centerPoint(spawnObj.getRectangle()));
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }
    
}
