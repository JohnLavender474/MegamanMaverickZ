package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.ConstVals.TextureAssets;
import com.game.ConstVals.VolumeVals;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.contracts.Damager;
import com.game.entities.decorations.Disintegration;
import com.game.screens.levels.CullOnLevelCamTrans;
import com.game.screens.levels.CullOnOutOfGameCamBounds;
import com.game.sound.SoundComponent;
import com.game.sound.SoundRequest;
import com.game.sprites.SpriteComponent;
import com.game.utils.Percentage;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.SoundAssets.THUMP_SOUND;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
@Setter
public class Bullet implements Entity, Damager, CullOnOutOfGameCamBounds, CullOnLevelCamTrans {

    private final GameContext2d gameContext;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Vector2 trajectory = new Vector2();
    private final Timer cullTimer = new Timer(.15f);
    private boolean dead;
    private int damage;
    private Entity owner;

    public Bullet(GameContext2d gameContext, Entity owner, Vector2 trajectory, Vector2 spawn) {
        this.gameContext = gameContext;
        this.owner = owner;
        this.trajectory.set(trajectory);
        addComponent(defineSpriteComponent(gameContext.getAsset(
                TextureAssets.OBJECTS_TEXTURE_ATLAS, TextureAtlas.class).findRegion("YellowBullet")));
        addComponent(defineBodyComponent(spawn));
        addComponent(new SoundComponent());
    }

    @Override
    public Rectangle getBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    @Override
    public void onDeath() {
        SoundComponent soundComponent = getComponent(SoundComponent.class);
        soundComponent.request(new SoundRequest(THUMP_SOUND, false, Percentage.of(VolumeVals.HIGH_VOLUME)));
        Disintegration disintegration = new Disintegration(gameContext, getComponent(BodyComponent.class).getCenter());
        gameContext.addEntity(disintegration);
    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion) {
        SpriteComponent spriteComponent = new SpriteComponent();
        Sprite sprite = spriteComponent.getSprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        spriteComponent.setSpriteUpdater(delta -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            sprite.setCenter(bodyComponent.getCenter().x, bodyComponent.getCenter().y);
        });
        return spriteComponent;
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        bodyComponent.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, FixtureType.PROJECTILE);
        projectile.setSize(.1f * PPM, .1f * PPM);
        projectile.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGE_BOX);
        damageBox.setSize(.1f * PPM, .1f * PPM);
        damageBox.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

}
