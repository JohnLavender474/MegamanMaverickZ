package com.game.world;

import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.entities.megaman.Megaman;
import com.game.health.HealthComponent;
import com.game.sounds.SoundComponent;

import java.util.ArrayList;
import java.util.Collection;

import static com.game.core.constants.MiscellaneousVals.*;
import static com.game.core.constants.SoundAsset.*;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.entities.megaman.Megaman.*;
import static com.game.entities.megaman.Megaman.AButtonTask.*;
import static com.game.utils.ShapeUtils.*;
import static com.game.world.BodySense.*;
import static com.game.world.FixtureType.*;

/**
 * Implementation pairOf {@link WorldContactListener}.
 */
public class WorldContactListenerImpl implements WorldContactListener {

    @Override
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
        } else if (contact.acceptMask(HITTER_BOX) && contact.mask1stEntity() instanceof Hitter hitter) {
            hitter.hit(contact.mask2ndFixture());
        } else if (contact.acceptMask(BOUNCEABLE, BOUNCER)) {
            Fixture bouncer = contact.mask2ndFixture();
            Float x = bouncer.getUserData("x", Float.class);
            Float y = bouncer.getUserData("y", Float.class);
            BodyComponent bounceable = contact.mask1stBody();
            if (x != null) {
                bounceable.setVelocityX(x * PPM);
            }
            if (y != null) {
                bounceable.setVelocityY(y * PPM);
            }
            Runnable runnable = contact.mask2ndFixture().getUserData("onBounce", Runnable.class);
            if (runnable != null) {
                runnable.run();
            }
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
                megaman.setAButtonTask(_JUMP);
            }
        } else if (contact.acceptMask(FEET, FEET_STICKER)) {
            contact.mask1stBody().translate(contact.mask2ndBody().getPosDelta());
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stEntity().getComponent(BodyComponent.class).setIs(HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGER, DAMAGEABLE) &&
                contact.mask1stEntity() instanceof Damager damager &&
                contact.mask2ndEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager);
            damager.onDamageInflictedTo(damageable);
        } else if (contact.acceptMask(HITTER_BOX) && contact.mask1stEntity() instanceof Hitter hitter) {
            hitter.hit(contact.getMask().getSecond());
        } else if (contact.acceptMask(LASER, BLOCK)) {
            Fixture first = contact.mask1stFixture();
            Fixture second = contact.mask2ndFixture();
            Collection<Vector2> contactPoints = first.getUserData(BLOCK_CONTACT_POINTS, Collection.class);
            Collection<Vector2> temp = new ArrayList<>();
            if (intersectLineRect((Polyline) first.getFixtureShape(), (Rectangle) second.getFixtureShape(), temp)) {
                contactPoints.addAll(temp);
            }
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
        } else if (contact.acceptMask(LEFT, DAMAGEABLE) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIsNot(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
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
