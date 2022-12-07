package com.game.world;

import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorType;
import com.game.entities.Entity;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.AbstractProjectile;
import com.game.health.HealthComponent;
import com.game.sounds.SoundComponent;
import com.game.updatables.UpdatableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.Vector2.*;
import static com.game.GlobalKeys.*;
import static com.game.assets.SoundAsset.MEGAMAN_LAND_SOUND;
import static com.game.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;
import static com.game.entities.megaman.Megaman.AButtonTask;
import static com.game.entities.megaman.Megaman.AButtonTask._AIR_DASH;
import static com.game.entities.megaman.Megaman.AButtonTask._JUMP;
import static com.game.utils.ShapeUtils.intersectLineRect;
import static com.game.world.BodySense.*;
import static com.game.world.FixtureType.*;

/**
 * Implementation of {@link WorldContactListener}.
 */
public class WorldContactListenerImpl implements WorldContactListener {

    @Override
    @SuppressWarnings("unchecked")
    public void beginContact(Contact contact, float delta) {
        if (contact.acceptMask(DAMAGEABLE, DEATH)) {
            contact.mask1stEntity().getComponent(HealthComponent.class).setHealth(0);
        } else if (contact.acceptMask(LEFT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(LEFT, DAMAGEABLE) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            Entity entity = contact.mask1stEntity();
            entity.getComponent(BodyComponent.class).setIs(FEET_ON_GROUND);
            if (entity instanceof Megaman megaman) {
                megaman.setAButtonTask(AButtonTask._JUMP);
                megaman.getComponent(SoundComponent.class).requestSound(MEGAMAN_LAND_SOUND);
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stBody().setIs(HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGER, DAMAGEABLE) &&
                contact.mask1stEntity() instanceof Damager damager &&
                contact.mask2ndEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager);
            damager.onDamageInflictedTo(damageable);
        } else if (contact.acceptMask(HITTER) && contact.mask1stEntity() instanceof Hitter hitter) {
            hitter.hit(contact.mask2ndFixture());
        } else if (contact.acceptMask(BOUNCEABLE, BOUNCER)) {
            Fixture bouncer = contact.mask2ndFixture();
            Function<Entity, Float> xFunc = (Function<Entity, Float>) bouncer.getUserData("xFunc");
            Function<Entity, Float> yFunc = (Function<Entity, Float>) bouncer.getUserData("yFunc");
            Entity bounceableEntity = contact.mask1stEntity();
            BodyComponent bounceableBody = contact.mask1stBody();
            bounceableBody.setVelocity(Zero);
            if (xFunc != null) {
                float x = xFunc.apply(bounceableEntity);
                bounceableBody.setVelocityX(x * PPM);
            }
            if (yFunc != null) {
                float y = yFunc.apply(bounceableEntity);
                bounceableBody.setVelocityY(y * PPM);
            }
            Runnable runnable = contact.mask2ndFixture().getUserData("onBounce", Runnable.class);
            if (runnable != null) {
                runnable.run();
            }
        } else if (contact.acceptMask(FORCE, FORCE_LISTENER)) {
            Fixture forceFixture = contact.mask1stFixture();
            Fixture forceListenerFixture = contact.mask2ndFixture();
            Function<Entity, Vector2> forceFunction = (Function<Entity, Vector2>) forceFixture.getUserData(FUNCTION);
            Collection<Class<? extends Entity>> forceListenerMask = (Collection<Class<? extends Entity>>)
                    forceListenerFixture.getUserData(COLLECTION);
            if (!forceListenerMask.contains(forceFixture.getEntity().getClass())) {
                return;
            }
            Vector2 force = forceFunction.apply(contact.mask2ndEntity());
            BodyComponent forceListenerBody = contact.mask2ndBody();
            if (forceListenerFixture.containsUserDataKey(CONTINUE) &&
                    forceListenerFixture.getUserData(CONTINUE, Boolean.class)) {
                Supplier<Boolean> doUpdate = (Supplier<Boolean>) forceListenerFixture.getUserData(UPDATE + PREDICATE);
                Supplier<Boolean> doRemove = (Supplier<Boolean>) forceListenerFixture.getUserData(REMOVE + PREDICATE);
                Entity forceListenerEntity = contact.mask2ndEntity();
                if (!forceListenerEntity.hasComponent(UpdatableComponent.class)) {
                    forceListenerEntity.addComponent(new UpdatableComponent());
                }
                forceListenerEntity.getComponent(UpdatableComponent.class).addUpdatable(d ->
                        forceListenerBody.translateVelocity(force), doUpdate, doRemove);
            } else {
                forceListenerBody.translateVelocity(force);
            }
        } else if (contact.acceptMask(SCANNER)) {
            Fixture scannerFixture = contact.mask1stFixture();
            Fixture other = contact.mask2ndFixture();
            ((Collection<FixtureType>) scannerFixture.getUserData(COLLECTION)).add(other.getFixtureType());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void continueContact(Contact contact, float delta) {
        if (contact.acceptMask(LEFT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(LEFT, DAMAGEABLE) && contact.areEntitiesDifferent()) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE) && contact.areEntitiesDifferent()) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            Entity entity = contact.mask1stEntity();
            entity.getComponent(BodyComponent.class).setIs(FEET_ON_GROUND);
            if (entity instanceof Megaman megaman) {
                megaman.setAButtonTask(_JUMP);
            }
        } else if (contact.acceptMask(FEET, FEET_STICKER)) {
            contact.mask1stBody().translate(contact.mask2ndBody().getPosDelta());
        } else if (contact.acceptMask(FEET, CONVEYOR)) {
            contact.mask1stBody().translate(contact.mask2ndFixture().getUserData(APPLY, Vector2.class));
        } else if (contact.acceptMask(FEET, ICE)) {
            contact.mask1stBody().getResistance().x = .95f;
        } else if (contact.acceptMask(LEFT, ICE) || contact.acceptMask(RIGHT, ICE)) {
            if (contact.mask1stEntity() instanceof Megaman megaman &&
                    megaman.getComponent(BehaviorComponent.class).is(WALL_SLIDING)) {
                megaman.getComponent(BodyComponent.class).setVelocity(0f, -15f * PPM);
                System.out.println("Doing");
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stEntity().getComponent(BodyComponent.class).setIs(HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGER, DAMAGEABLE) &&
                contact.mask1stEntity() instanceof Damager damager &&
                contact.mask2ndEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager);
            damager.onDamageInflictedTo(damageable);
        } else if (contact.acceptMask(HITTER, FORCE) && contact.mask1stEntity() instanceof AbstractProjectile p) {
            p.setOwner(null);
        } else if (contact.acceptMask(HITTER) && contact.mask1stEntity() instanceof Hitter hitter) {
            hitter.hit(contact.getMask().getSecond());
        } else if (contact.acceptMask(LASER, BLOCK) && contact.areEntitiesDifferent()) {
            Fixture first = contact.mask1stFixture();
            Fixture second = contact.mask2ndFixture();
            Collection<Vector2> contactPoints = first.getUserData(COLLECTION, Collection.class);
            Collection<Vector2> temp = new ArrayList<>();
            if (intersectLineRect((Polyline) first.getFixtureShape(), (Rectangle) second.getFixtureShape(), temp)) {
                contactPoints.addAll(temp);
            }
        } else if (contact.acceptMask(FORCE, FORCE_LISTENER)) {
            Function<Entity, Vector2> forceFunction = (Function<Entity, Vector2>) contact.mask1stFixture()
                    .getUserData(FUNCTION);
            Fixture forceListener = contact.mask2ndFixture();
            BodyComponent forceListenerBody = contact.mask2ndBody();
            if (!forceListener.containsUserDataKey(CONTINUE) || !forceListener.getUserData(CONTINUE, Boolean.class)) {
                Vector2 force = forceFunction.apply(forceListener.getEntity());
                forceListenerBody.translateVelocity(force);
            }
        } else if (contact.acceptMask(SCANNER)) {
            Fixture scannerFixture = contact.mask1stFixture();
            Fixture other = contact.mask2ndFixture();
            ((Collection<FixtureType>) scannerFixture.getUserData(COLLECTION)).add(other.getFixtureType());
        }
    }

    @Override
    public void endContact(Contact contact, float delta) {
        if (contact.acceptMask(LEFT, BLOCK)) {
            contact.mask1stBody().setIsNot(TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.mask1stBody().setIsNot(TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIsNot(TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIsNot(TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(LEFT, DAMAGEABLE) && contact.areEntitiesDifferent()) {
            contact.mask1stBody().setIsNot(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE) && contact.areEntitiesDifferent()) {
            contact.mask1stBody().setIsNot(TOUCHING_HITBOX_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            contact.mask1stBody().setIsNot(FEET_ON_GROUND);
            if (contact.mask1stEntity() instanceof Megaman megaman) {
                megaman.setAButtonTask(_AIR_DASH);
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stBody().setIsNot(HEAD_TOUCHING_BLOCK);
        }
    }

}
