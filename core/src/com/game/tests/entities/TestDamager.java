package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.core.IEntity;
import com.game.debugging.DebugComponent;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.health.HealthComponent;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Timer;
import com.game.utils.UtilMethods;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.game.world.FixtureType.DAMAGER_BOX;
import static com.game.world.FixtureType.DAMAGEABLE_BOX;

@Getter
@Setter
public class TestDamager implements IEntity, Damager, Damageable, CullOnOutOfCamBounds, CullOnLevelCamTrans {

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Set<Class<? extends Damager>> damagerMaskSet = new HashSet<>() {{
        add(TestBullet.class);
    }};
    private final Timer damageTimer = new Timer(.5f);
    private final Timer cullTimer = new Timer(.5f);
    private boolean dead;

    public TestDamager(Rectangle bounds) {
        damageTimer.setToEnd();
        addComponent(new HealthComponent(100));
        addComponent(defineBodyComponent(bounds));
        addComponent(defineDebugComponent());
        addComponent(defineUpdatableComponent());
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        damageTimer.reset();
        if (damager instanceof TestBullet) {
            getComponent(HealthComponent.class).sub(20);
            Gdx.audio.newSound(Gdx.files.internal("sounds/EnemyDamage.mp3")).play();
        }
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished();
    }

    private BodyComponent defineBodyComponent(Rectangle bounds) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
        bodyComponent.set(bounds);
        bodyComponent.setGravityOn(false);
        bodyComponent.setFriction(0f, 0f);
        bodyComponent.setAffectedByResistance(false);
        Fixture hitBox = new Fixture(this, DAMAGEABLE_BOX);
        hitBox.set(UtilMethods.getScaledRect(bounds, 1.05f));
        bodyComponent.addFixture(hitBox);
        Fixture damageBox = new Fixture(this, DAMAGER_BOX);
        damageBox.set(UtilMethods.getScaledRect(bounds, 1.05f));
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    private DebugComponent defineDebugComponent() {
        DebugComponent debugComponent = new DebugComponent();
        debugComponent.addDebugHandle(() -> getComponent(BodyComponent.class).getCollisionBox(),
                () -> damageTimer.isFinished() ? Color.PURPLE : Color.RED);
        return debugComponent;
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            if (!damageTimer.isFinished()) {
                damageTimer.update(delta);
            }
        });
    }

    @Override
    public Rectangle getCullBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

}
