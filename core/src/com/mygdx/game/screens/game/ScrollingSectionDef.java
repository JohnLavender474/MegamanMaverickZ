package com.mygdx.game.screens.game;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.TimeTicker;

/**
 * The definition class of a scrolling section in a game level.
 */
public record ScrollingSectionDef(String key, Vector2 target, TimeTicker timeTicker) {}
