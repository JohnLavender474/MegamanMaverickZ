package com.mygdx.game.screens.ui;

import com.badlogic.gdx.math.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class UiCell {
    @Getter private final int row;
    @Getter private final int col;
    @Getter private final UiTable table;
    @Getter @Setter private Object userData;
    @Getter @Setter private Class<?> userDataClass;
    @Getter(AccessLevel.PROTECTED) private final Rectangle cell = new Rectangle();
}
