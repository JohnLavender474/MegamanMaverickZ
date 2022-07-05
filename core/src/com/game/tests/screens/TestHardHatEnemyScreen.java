package com.game.tests.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.core.IEntity;
import com.game.entities.contracts.contracts.Damageable;
import com.game.entities.contracts.contracts.Damager;
import com.game.entities.contracts.contracts.Faceable;
import com.game.entities.contracts.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.screens.levels.CullOnLevelCamTrans;
import com.game.screens.levels.CullOnOutOfGameCamBounds;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Position;
import com.game.utils.Timer;
import com.game.utils.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.game.ConstVals.ViewVals.PPM;

public class TestHardHatEnemyScreen extends ScreenAdapter {

    @Getter
    static class TestMet implements IEntity, Faceable, Damager, Damageable,
            CullOnLevelCamTrans, CullOnOutOfGameCamBounds {

        enum MetBehavior {
            SHIELDING, POP_UP, RUNNING, PANIC
        }

        private MetBehavior metBehavior;
        private final Map<MetBehavior, Timer> metBehaviorTimers = new EnumMap<>(MetBehavior.class) {{
            put(MetBehavior.SHIELDING, new Timer(1.5f));
            put(MetBehavior.POP_UP, new Timer(.75f));
            put(MetBehavior.RUNNING, new Timer(.5f));
            put(MetBehavior.PANIC, new Timer(1f));
        }};

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private final Set<Class<? extends Damager>> damagerMaskSet = new HashSet<>();
        @Setter private boolean dead;
        @Setter private Facing facing;

        private final Timer damageTimer = new Timer(.25f);
        private final Timer blinkTimer = new Timer(.05f);
        private final Timer cullTimer = new Timer(2f);

        public TestMet(Megaman megaman, Vector2 spawn) {
            addComponent(defineUpdatableComponent(megaman));
            addComponent(defineBodyComponent(spawn));
            addComponent(defineSpriteComponent());
            setMetBehavior(MetBehavior.SHIELDING);
            damageTimer.setToEnd();
        }

        @Override
        public Rectangle getCullBoundingBox() {
            return getComponent(BodyComponent.class).getCollisionBox();
        }

        @Override
        public void takeDamageFrom(Class<? extends Damager> damagerClass) {
            damageTimer.reset();
        }

        @Override
        public boolean isInvincible() {
            return !damageTimer.isFinished();
        }

        public void setMetBehavior(MetBehavior metBehavior) {
            metBehaviorTimers.values().forEach(Timer::reset);
            getComponent(BodyComponent.class).setVelocity(0f, 0f);
            this.metBehavior = metBehavior;
        }

        private void shoot() {

        }

        private void explode() {

        }

        private UpdatableComponent defineUpdatableComponent(Megaman megaman) {
            return new UpdatableComponent(delta -> {
                damageTimer.update(delta);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                bodyComponent.getFirstMatchingFixture(FixtureType.SHIELD).ifPresent(
                        shield -> shield.setActive(metBehavior == MetBehavior.SHIELDING));
                bodyComponent.getFirstMatchingFixture(FixtureType.HIT_BOX).ifPresent(
                        hitBox -> hitBox.setActive(metBehavior != MetBehavior.SHIELDING));
                setFacing(megaman.getComponent(BodyComponent.class).getPosition().x <
                        getComponent(BodyComponent.class).getPosition().x ? Facing.LEFT : Facing.RIGHT);
                switch (metBehavior) {
                    case SHIELDING -> {
                        Timer shieldingTimer = metBehaviorTimers.get(MetBehavior.SHIELDING);
                        shieldingTimer.update(delta);
                        if (shieldingTimer.isFinished()) {
                            setMetBehavior(MetBehavior.POP_UP);
                        }
                    }
                    case POP_UP -> {
                        Timer popUpTimer = metBehaviorTimers.get(MetBehavior.POP_UP);
                        if (popUpTimer.isAtBeginning()) {
                            shoot();
                        }
                        popUpTimer.update(delta);
                        if (popUpTimer.isFinished()) {
                            setMetBehavior(MetBehavior.RUNNING);
                        }
                    }
                    case RUNNING -> {
                        Timer runningTimer = metBehaviorTimers.get(MetBehavior.RUNNING);
                        runningTimer.update(delta);
                        bodyComponent.setVelocity(8f * PPM * (getFacing() == Facing.LEFT ? -1f : 1f), 0f);
                        if (runningTimer.isFinished()) {
                            setMetBehavior(MetBehavior.SHIELDING);
                        }
                    }
                    case PANIC -> {
                        Timer panicTimer = metBehaviorTimers.get(MetBehavior.PANIC);
                        metBehaviorTimers.get(MetBehavior.PANIC).update(delta);
                        if (panicTimer.isFinished()) {
                            explode();
                            setDead(true);
                        }
                    }
                }
            });
        }

        private BodyComponent defineBodyComponent(Vector2 spawn) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
            bodyComponent.set(spawn.x, spawn.y, .75f * PPM, .75f * PPM);
            // shield
            Fixture shield = new Fixture(this, FixtureType.SHIELD);
            shield.setActive(false);
            shield.setSize(PPM, 1.5f * PPM);
            bodyComponent.addFixture(shield);
            // hit box
            Fixture hitBox = new Fixture(this, FixtureType.HIT_BOX);
            hitBox.setSize(.75f * PPM, .75f * PPM);
            bodyComponent.addFixture(hitBox);
            return bodyComponent;
        }

        private SpriteComponent defineSpriteComponent() {
            Sprite sprite = new Sprite();
            sprite.setSize(1.85f, 1.85f);
            return new SpriteComponent(sprite, new SpriteAdapter() {

                @Override
                public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                    bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                    position.setData(Position.BOTTOM_CENTER);
                    return true;
                }

                @Override
                public boolean isFlipX() {
                    return getFacing() == Facing.LEFT;
                }

            });
        }

    }

}
