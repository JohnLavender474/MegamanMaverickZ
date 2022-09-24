package com.game.backgrounds;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.core.GameContext2d;

import java.util.Collection;

import static com.game.backgrounds.WindyClouds.WINDY_CLOUDS;

public class BackgroundFactory {

    public static void create(GameContext2d gameContext, Collection<Background> backgrounds,
                              RectangleMapObject backgroundObj) {
        switch (backgroundObj.getName()) {
            case WINDY_CLOUDS -> backgrounds.add(new WindyClouds(gameContext, backgroundObj));
        }
    }

}
