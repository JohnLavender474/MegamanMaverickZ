package com.game.entities.enemies;

import com.game.Entity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOnOutOfCamBoundsComponent;
import com.game.world.BodyComponent;

public class Enemy extends Entity {

    public Enemy(float cullTimer) {
        addComponent(new CullOnCamTransComponent());
        addComponent(new CullOnOutOfCamBoundsComponent(
                () -> getComponent(BodyComponent.class).getCollisionBox(), 2f));

    }

}
