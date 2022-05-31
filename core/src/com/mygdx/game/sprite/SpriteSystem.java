package com.mygdx.game.sprite;

import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import com.mygdx.game.GameState;
import com.mygdx.game.System;

import java.util.Set;

public class SpriteSystem extends System {

    @Override
    public Set<GameState> getSwitchOffStates() {
        return null;
    }

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return null;
    }

    @Override
    protected void processEntity(Entity entity, float delta) {

    }

}
