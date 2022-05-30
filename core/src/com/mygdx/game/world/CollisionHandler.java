package com.mygdx.game.world;

import com.badlogic.gdx.math.Rectangle;

public interface CollisionHandler {
    void handleCollision(BodyComponent bc1, BodyComponent bc2, Rectangle overlap, float delta);
}
