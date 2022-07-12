package com.game.tests.core;

import com.badlogic.gdx.Gdx;
import com.game.core.IEntity;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.projectiles.IProjectile;
import com.game.health.HealthComponent;
import com.game.tests.entities.TestBlock;
import com.game.tests.entities.TestPlayer;
import com.game.world.BodyComponent;
import com.game.world.BodySense;
import com.game.world.Contact;
import com.game.world.WorldContactListener;

import static com.game.world.FixtureType.*;

public class TestWorldContactListener implements WorldContactListener {

    @Override
    public void beginContact(Contact contact, float delta) {
        if (contact.acceptMask(HIT_BOX, DEATH)) {
            contact.maskFirstEntity().getComponent(HealthComponent.class).setHealth(0);
        } else if (contact.acceptMask(LEFT, BLOCK)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            IEntity entity = contact.maskFirstEntity();
            entity.getComponent(BodyComponent.class).setIs(BodySense.FEET_ON_GROUND);
            if (entity instanceof TestPlayer testPlayer) {
                testPlayer.setAButtonTask(TestPlayer.AButtonTask.JUMP);
                Gdx.audio.newSound(Gdx.files.internal("sounds/MegamanLand.mp3")).play();
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.maskFirstBody().setIs(BodySense.HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGE_BOX, HIT_BOX) &&
                contact.maskFirstEntity() instanceof Damager damager &&
                contact.maskSecondEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager.getClass());
            damager.onDamageInflictedTo(damageable.getClass());
        } else if (contact.acceptMask(PROJECTILE)) {
            ((IProjectile) contact.maskFirstEntity()).hit(contact.getMask().second());
        }
    }

    @Override
    public void continueContact(Contact contact, float delta) {
        if (contact.acceptMask(LEFT, BLOCK)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.maskFirstBody().setIs(BodySense.TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            IEntity entity = contact.maskFirstEntity();
            entity.getComponent(BodyComponent.class).setIs(BodySense.FEET_ON_GROUND);
            if (entity instanceof TestPlayer testPlayer) {
                testPlayer.setAButtonTask(TestPlayer.AButtonTask.JUMP);
            }
        } else if (contact.acceptMask(FEET, FEET_STICKER) && contact.maskFirstEntity() instanceof TestPlayer &&
                contact.maskSecondEntity() instanceof TestBlock) {
            contact.maskFirstBody().translate(contact.maskSecondBody().getPosDelta());
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.maskFirstEntity().getComponent(BodyComponent.class).setIs(BodySense.HEAD_TOUCHING_BLOCK);
        } else if (contact.acceptMask(DAMAGE_BOX, HIT_BOX) &&
                contact.maskFirstEntity() instanceof Damager damager &&
                contact.maskSecondEntity() instanceof Damageable damageable &&
                damageable.canBeDamagedBy(damager) && damager.canDamage(damageable)) {
            damageable.takeDamageFrom(damager.getClass());
            damager.onDamageInflictedTo(damageable.getClass());
        } else if (contact.acceptMask(PROJECTILE)) {
            ((IProjectile) contact.maskFirstEntity()).hit(contact.getMask().second());
        }
    }

    @Override
    public void endContact(Contact contact, float delta) {
        if (contact.acceptMask(LEFT, BLOCK)) {
            contact.maskFirstBody().setIsNot(BodySense.TOUCHING_BLOCK_LEFT);
        } else if (contact.acceptMask(RIGHT, BLOCK)) {
            contact.maskFirstBody().setIsNot(BodySense.TOUCHING_BLOCK_RIGHT);
        } else if (contact.acceptMask(LEFT, WALL_SLIDE_SENSOR)) {
            contact.maskFirstBody().setIsNot(BodySense.TOUCHING_WALL_SLIDE_LEFT);
        } else if (contact.acceptMask(RIGHT, WALL_SLIDE_SENSOR)) {
            contact.maskFirstBody().setIsNot(BodySense.TOUCHING_WALL_SLIDE_RIGHT);
        } else if (contact.acceptMask(FEET, BLOCK)) {
            contact.maskFirstBody().setIsNot(BodySense.FEET_ON_GROUND);
            if (contact.maskFirstEntity() instanceof TestPlayer testPlayer) {
                testPlayer.setAButtonTask(TestPlayer.AButtonTask.AIR_DASH);
            }
        } else if (contact.acceptMask(HEAD, BLOCK)) {
            contact.maskFirstBody().setIsNot(BodySense.HEAD_TOUCHING_BLOCK);
        }
    }

}

