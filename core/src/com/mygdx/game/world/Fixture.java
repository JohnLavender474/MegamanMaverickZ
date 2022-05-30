package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Fixture {

    private boolean active;
    private Updatable updatable;
    private final FixtureType fixtureType;
    private Color debugColor = Color.BLUE;
    private final BodyComponent bodyComponent;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();

    @Override
    public boolean equals(Object o) {
        return o instanceof Fixture fixture &&
                fixtureType.equals(fixture.getFixtureType()) &&
                bodyComponent.equals(fixture.getBodyComponent());
    }

    @Override
    public int hashCode() {
        return fixtureType.hashCode() + bodyComponent.hashCode();
    }

}
