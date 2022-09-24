package com.game;

import com.badlogic.gdx.math.Vector3;

import static com.game.ViewVals.*;
import static com.game.ViewVals.PPM;

public class ConstFuncs {

    public static Vector3 getCamInitPos() {
        return new Vector3(VIEW_WIDTH * PPM / 2f, VIEW_HEIGHT * PPM / 2f, 0f);
    }

}
