package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.ConstVals.TextureAssets;
import com.game.GameContext2d;
import com.game.core.IEntity;
import com.game.entities.contracts.Damager;
import com.game.entities.contracts.Hitter;
import com.game.levels.CullOnLevelCamTrans;
import com.game.levels.CullOnOutOfCamBounds;
import com.game.sprites.SpriteComponent;
import com.game.utils.Position;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.world.FixtureType.BLOCK;

@Getter
@Setter
public class Bullet implements IEntity, Hitter, Damager, CullOnOutOfCamBounds, CullOnLevelCamTrans {

    private final GameContext2d gameContext;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Vector2 trajectory = new Vector2();
    private final Timer cullTimer = new Timer(.15f);
    private boolean dead;
    private int damage;
    private IEntity owner;

    public Bullet(GameContext2d gameContext, IEntity owner, Vector2 trajectory, Vector2 spawn) {
        this.gameContext = gameContext;
        this.owner = owner;
        this.trajectory.set(trajectory);
        addComponent(defineSpriteComponent(gameContext.getAsset(TextureAssets.OBJECTS_TEXTURE_ATLAS,
                TextureAtlas.class).findRegion("YellowBullet")));
        addComponent(defineBodyComponent(spawn));
    }

    @Override
    public Rectangle getCullBoundingBox() {
        return getComponent(BodyComponent.class).getCollisionBox();
    }

    public void disintegrate() {

    }

    private SpriteComponent defineSpriteComponent(TextureRegion textureRegion) {
        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setSize(PPM * 1.25f, PPM * 1.25f);
        return new SpriteComponent(sprite, (bounds, position) -> {
            bounds.setData(getCullBoundingBox());
            position.setData(Position.CENTER);
            return true;
        });
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> bodyComponent.setVelocity(trajectory));
        bodyComponent.setSize(.1f * PPM, .1f * PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, FixtureType.HITTER_BOX);
        projectile.setSize(.1f * PPM, .1f * PPM);
        projectile.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGER_BOX);
        damageBox.setSize(.1f * PPM, .1f * PPM);
        damageBox.setCenter(spawn.x, spawn.y);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.getFixtureType() == BLOCK) {
            setDead(true);
            disintegrate();
        }
    }

}
