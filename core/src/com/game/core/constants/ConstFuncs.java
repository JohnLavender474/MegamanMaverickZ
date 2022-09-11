package com.game.core.constants;

import com.badlogic.gdx.math.Vector3;

import static com.game.core.constants.ViewVals.*;
import static com.game.core.constants.ViewVals.PPM;

public class ConstFuncs {

    public static Vector3 getCamInitPos() {
        return new Vector3(VIEW_WIDTH * PPM / 2f, VIEW_HEIGHT * PPM / 2f, 0f);
    }

}
