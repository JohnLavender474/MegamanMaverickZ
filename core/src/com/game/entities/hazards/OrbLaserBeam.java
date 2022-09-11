package com.game.entities.hazards;

import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.damage.Damager;
import com.game.world.BodyComponent;
import com.game.world.BodyType;

public class OrbLaserBeam extends Entity implements Damager {

    public OrbLaserBeam(GameContext2d gameContext) {
        super(gameContext);
    }

    private BodyComponent defineBodyComponent() {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        return bodyComponent;
    }

}
