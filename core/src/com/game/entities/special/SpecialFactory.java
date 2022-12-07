package com.game.entities.special;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.game.world.BodyType.ABSTRACT;
import static com.game.world.FixtureType.SHIELD;

public class SpecialFactory {

    public static void create(GameContext2d gameContext, RectangleMapObject spawnObj) {
        switch (spawnObj.getName()) {
            case "ice" -> gameContext.addEntity(new Ice(gameContext, spawnObj));
            case "force" -> gameContext.addEntity(new Force(gameContext, spawnObj));
            case "spring_bounce" -> gameContext.addEntity(new SpringBounce(gameContext, spawnObj));
            case "shield" -> {
                Entity entity = new Entity(gameContext);
                BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
                bodyComponent.set(spawnObj.getRectangle());
                Fixture shield = new Fixture(entity, spawnObj.getRectangle(), SHIELD);
                String reflectDir = spawnObj.getProperties().get("reflectDir", String.class);
                shield.putUserData("reflectDir", reflectDir);
                bodyComponent.addFixture(shield);
                entity.addComponent(bodyComponent);
                gameContext.addEntity(entity);
            }
            default -> throw new IllegalStateException("Cannot find matching entity for <" + spawnObj.getName() + ">");
        }
    }

}
