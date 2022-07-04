package com.game.tests.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.entities.contracts.contracts.Damageable;
import com.game.entities.contracts.contracts.Damager;
import com.game.core.IEntity;
import com.game.sprites.SpriteComponent;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.game.ConstVals.ViewVals.PPM;

public class TestHardHatEnemyScreen extends ScreenAdapter {

    @Getter
    @Setter
    static class TestMet implements IEntity, Damager, Damageable {

        enum MetBehavior {
            SHIELDING,
            RUNNING,
            POP_UP,
            NAKED
        }

        private MetBehavior metBehavior;

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private final Set<Class<? extends Damager>> damagerMaskSet = new HashSet<>();
        private boolean invincible;
        private boolean dead;

        private final Timer shieldTimer = new Timer(1.5f);
        private final Timer popUpTimer = new Timer(.75f);
        private final Timer panicTimer = new Timer(1f);
        private final Timer runTimer = new Timer(.5f);

        public TestMet(Vector2 spawn) {
            addComponent(defineBodyComponent(spawn));
            setMetBehavior(MetBehavior.SHIELDING);
        }

        @Override
        public void takeDamageFrom(Class<? extends Damager> damagerClass) {

        }

        private BodyComponent defineBodyComponent(Vector2 spawn) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
            bodyComponent.set(spawn.x, spawn.y, .75f * PPM, .75f * PPM);
            Fixture shield = new Fixture(this, FixtureType.SHIELD);
            shield.setActive(false);
            shield.setSize(PPM, 1.5f * PPM);
            bodyComponent.addFixture(shield);
            Fixture hitBox = new Fixture(this, FixtureType.HIT_BOX);
            hitBox.setSize(.75f * PPM, .75f * PPM);
            bodyComponent.addFixture(hitBox);
            return bodyComponent;
        }

        private SpriteComponent defineSpriteComponent() {
            return null;
        }

    }

}
