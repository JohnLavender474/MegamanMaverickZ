package com.mygdx.game.screens.menus;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.screens.ui.UiTable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class UiCanvas {

    private final Rectangle parent = new Rectangle();
    private final Map<String, Rectangle> children = new HashMap<>();

    public UiCanvas(float width, float height) {
        parent.set(0f, 0f, width, height);
    }



}
