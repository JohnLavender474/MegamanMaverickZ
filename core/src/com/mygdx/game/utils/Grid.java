package com.mygdx.game.utils;

import com.badlogic.gdx.math.Rectangle;

public class Grid {

    private final Rectangle rectangle = new Rectangle();
    private final Float pixelsPerMeter;
    private final Integer widthInMeters;
    private final Integer heightInMeters;

    public Grid(Float pixelsPerMeter, Integer widthInMeters, Integer heightInMeters) {
        this.pixelsPerMeter = pixelsPerMeter;
        this.widthInMeters = widthInMeters;
        this.heightInMeters = heightInMeters;
        rectangle.setSize(pixelsPerMeter * widthInMeters, pixelsPerMeter * heightInMeters);
    }

    public void setPosition(float x, float y) {
        rectangle.setPosition(x, y);
    }

}
