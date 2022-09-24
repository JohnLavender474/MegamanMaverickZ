package com.game.entities.blocks;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.core.GameContext2d;
import com.game.entities.blocks.impl.JeffBezosLittleDickRocket;

public class BlockFactory {

    public static void create(GameContext2d gameContext, RectangleMapObject blockObj) {
        if (blockObj.getName() != null) {
            if (blockObj.getName().equals("Jeffy")) {
                gameContext.addEntity(new JeffBezosLittleDickRocket(gameContext, blockObj));
            }
        } else {
            gameContext.addEntity(new Block(gameContext, blockObj));
        }
    }

}
