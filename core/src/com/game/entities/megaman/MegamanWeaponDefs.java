package com.game.entities.megaman;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.GameContext2d;
import com.game.behaviors.BehaviorComponent;
import com.game.entities.contracts.Facing;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.Fireball;
import com.game.sounds.SoundComponent;
import com.game.weapons.WeaponDef;
import com.game.world.BodyComponent;
import lombok.Setter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.GlobalKeys.CHARGE_STATUS;
import static com.game.assets.SoundAsset.*;
import static com.game.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.WALL_SLIDING;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.entities.megaman.MegamanWeapon.FLAME_TOSS;
import static com.game.entities.megaman.MegamanWeapon.MEGA_BUSTER;
import static com.game.world.BodySense.FEET_ON_GROUND;

public class MegamanWeaponDefs {

    private static final float BULLET_VEL = 10f;
    private static final float FLAME_TOSS_VEL_X = 35f;
    private static final float FLAME_TOSS_VEL_Y = 10f;

    private final GameContext2d gameContext;
    private final Map<MegamanWeapon, WeaponDef> megamanWeaponDefs = new EnumMap<>(MegamanWeapon.class);

    @Setter
    private Megaman megaman;

    public MegamanWeaponDefs(GameContext2d gameContext) {
        this.gameContext = gameContext;
        defineWeapons();
    }
    
    public WeaponDef get(MegamanWeapon megamanWeapon) {
        return megamanWeaponDefs.get(megamanWeapon);
    }
    
    private <C extends Component> C getComponent(Class<C> cClass) {
        return megaman.getComponent(cClass);
    }
    
    private boolean isFacing(Facing facing) {
        return megaman.isFacing(facing);
    }
    
    private Facing getFacing() {
        return megaman.getFacing();
    }

    private void defineWeapons() {
        Supplier<Vector2> spawn = () -> {
            Vector2 spawnPos = getComponent(BodyComponent.class).getCenter().add(.75f * PPM *
                    (isFacing(F_LEFT) ? -1f : 1f), PPM / 16f);
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            if (behaviorComponent.is(WALL_SLIDING)) {
                spawnPos.y += .15f * PPM;
            } else if (!bodyComponent.is(FEET_ON_GROUND)) {
                spawnPos.y += PPM / 4f;
            }
            return spawnPos;
        };
        megamanWeaponDefs.put(MEGA_BUSTER, new WeaponDef(m -> {
            Integer chargeStatus = (Integer) m.get(CHARGE_STATUS);
            Vector2 trajectory = new Vector2(BULLET_VEL * (isFacing(F_LEFT) ? -PPM : PPM), 0f);
            if (2 == chargeStatus || 1 == chargeStatus) {
                return List.of(new ChargedShot(gameContext, megaman, trajectory,
                        spawn.get(), getFacing(), chargeStatus == 2));
            }
            if (0 == chargeStatus) {
                return List.of(new Bullet(gameContext, megaman, trajectory, spawn.get()));
            }
            throw new IllegalStateException();
        }, .1f, m -> {
            Integer chargeStatus = (Integer) m.get(CHARGE_STATUS);
            if (2 == chargeStatus || 1 == chargeStatus) {
                getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_CHARGED_SHOT_SOUND);
            } else {
                getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_BULLET_SHOT_SOUND);
            }
        }));
        megamanWeaponDefs.put(FLAME_TOSS, new WeaponDef(key -> {
            Vector2 impulse = new Vector2(FLAME_TOSS_VEL_X * (isFacing(F_LEFT) ? -PPM : PPM), FLAME_TOSS_VEL_Y * PPM);
            /*
            if (isCharging()) {
                // TODO: return charging fireball
            } else {
                // TODO: return normal fireball
            }
             */
            return List.of(new Fireball(gameContext, megaman, impulse, spawn.get()));
        }, .75f, m -> getComponent(SoundComponent.class).requestSound(CRASH_BOMBER_SOUND)));
    }

}
