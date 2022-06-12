package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.ProcessState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.Map;

/**
 * Defines a fixture fixed to a body. Offset is from the center of the body.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Fixture {

    private final BodyComponent bodyComponent;
    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();

    public void set(float x, float y, float width, float height) {
        fixtureBox.set(x, y, width, height);
    }

    public void setOffset(float x, float y) {
        offset.set(x, y);
    }

    public void setSize(float x, float y) {
        fixtureBox.setSize(x, y);
    }

    public void setCenter(Vector2 center) {
        setCenter(center.x, center.y);
    }

    public void setCenter(float x, float y) {
        fixtureBox.setCenter(x, y);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Fixture fixture &&
                fixture.getBodyComponent().equals(bodyComponent) &&
                fixture.getFixtureType().equals(fixtureType);
    }

    @Override
    public int hashCode() {
        int hash = 49;
        hash += 7 * bodyComponent.hashCode();
        hash += 7 * fixtureType.hashCode();
        return hash;
    }

}
