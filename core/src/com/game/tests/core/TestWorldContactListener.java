package com.game.tests.core;

import com.badlogic.gdx.Gdx;
import com.game.core.IEntity;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Hitter;
import com.game.health.HealthComponent;
import com.game.tests.entities.TestPlayer;
import com.game.world.BodyComponent;
import com.game.world.Contact;
import com.game.world.WorldContactListener;

import static com.game.world.BodySense.*;
import static com.game.world.FixtureType.*;

public class TestWorldContactListener implements WorldContactListener {

    @Override
    public void beginContact(Contact contact, float delta) {
        if (contact.acceptMask(DAMAGEABLE_BOX, DEATH)) {
            contact.mask1stEntity().getComponent(HealthComponent.class).setHealth(0);
        } else if (contact.acceptMask(LEFT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(LEFT, DAMAGEABLE_BOX) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE_BOX) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            IEntity entity = contact.mask1stEntity();
            entity.getComponent(BodyComponent.class).setIs(FEET_ON_GROUND);
            if (entity instanceof TestPlayer testPlayer) {
                testPlayer.setAButtonTask(TestPlayer.AButtonTask.JUMP);
                Gdx.audio.newSound(Gdx.files.internal("sounds/MegamanLand.mp3")).play();
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stBody().setIs(HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGER_BOX, DAMAGEABLE_BOX) &&
                contact.mask1stEntity() instanceof Damager damager &&
                contact.mask2ndEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager);
            damager.onDamageInflictedTo(damageable);
        } else if (contact.acceptMask(HITTER_BOX) && contact.mask1stEntity() instanceof Hitter hitter) {
            hitter.hit(contact.getMask().getSecond());
        }
    }

    @Override
    public void continueContact(Contact contact, float delta) {
        if (contact.acceptMask(LEFT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.mask1stBody().setIs(TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.mask1stBody().setIs(TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(LEFT, DAMAGEABLE_BOX) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE_BOX) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIs(TOUCHING_HITBOX_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            IEntity entity = contact.mask1stEntity();
            entity.getComponent(BodyComponent.class).setIs(FEET_ON_GROUND);
            if (entity instanceof TestPlayer testPlayer) {
                testPlayer.setAButtonTask(TestPlayer.AButtonTask.JUMP);
            }
        } else if (contact.acceptMask(FEET, FEET_STICKER)) {
            contact.mask1stBody().translate(contact.mask2ndBody().getPosDelta());
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stEntity().getComponent(BodyComponent.class).setIs(HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGER_BOX, DAMAGEABLE_BOX) &&
                contact.mask1stEntity() instanceof Damager damager &&
                contact.mask2ndEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager);
            damager.onDamageInflictedTo(damageable);
        } else if (contact.acceptMask(HITTER_BOX) && contact.mask1stEntity() instanceof Hitter hitter) {
            hitter.hit(contact.getMask().getSecond());
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
        } else if (contact.acceptMask(LEFT, DAMAGEABLE_BOX) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIsNot(TOUCHING_HITBOX_LEFT);
        } else if (contact.acceptMask(RIGHT, DAMAGEABLE_BOX) &&
                !contact.mask1stEntity().equals(contact.mask2ndEntity())) {
            contact.mask1stBody().setIsNot(TOUCHING_HITBOX_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            contact.mask1stBody().setIsNot(FEET_ON_GROUND);
            if (contact.mask1stEntity() instanceof TestPlayer testPlayer) {
                testPlayer.setAButtonTask(TestPlayer.AButtonTask.AIR_DASH);
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.mask1stBody().setIsNot(HEAD_TOUCHING_BLOCK);
        }
    }

}

