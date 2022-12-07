package com.game.entities.blocks;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.GameContext2d;
import com.game.entities.blocks.impl.GearTrolley;
import com.game.entities.blocks.impl.JeffBezosLittleDickRocket;

public class BlockFactory {

    public static void create(GameContext2d gameContext, RectangleMapObject blockObj) {
        Block block;
        if (blockObj.getName() != null) {
            switch (blockObj.getName()) {
                case "jeffy" -> block = new JeffBezosLittleDickRocket(gameContext, blockObj);
                case "gear_trolley" -> block = new GearTrolley(gameContext, blockObj);
                case "conveyor_belt" -> block = new ConveyorBelt(gameContext, blockObj);
                default -> throw new IllegalStateException("No block obj assigned to " + blockObj.getName());
            }
        } else {
            block = new Block(gameContext, blockObj);
        }
        gameContext.addEntity(block);
    }

}
