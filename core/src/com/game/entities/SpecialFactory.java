package com.game.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.entities.special.Force;
import com.game.entities.special.SpringyBouncer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.game.world.BodyType.ABSTRACT;
import static com.game.world.FixtureType.SHIELD;

public class SpecialFactory {

    public static Entity get(GameContext2d gameContext, RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "bounce" -> {
                return new SpringyBouncer(gameContext, spawnObj);
            }
            case "force" -> {
                return new Force(gameContext, spawnObj);
            }
            case "shield" -> {
                Entity entity = new Entity(gameContext);
                BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
                bodyComponent.set(spawnObj.getRectangle());
                Fixture shield = new Fixture(entity, spawnObj.getRectangle(), SHIELD);
                String reflectDir = spawnObj.getProperties().get("reflectDir", String.class);
                shield.putUserData("reflectDir", reflectDir);
                bodyComponent.addFixture(shield);
                entity.addComponent(bodyComponent);
                return entity;
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

}
