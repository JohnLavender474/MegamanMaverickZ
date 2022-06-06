package com.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Fixture {

    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();

    private boolean active;
    private Object userData;
    private Updatable updatable;
    private Color debugColor = Color.BLUE;

}
