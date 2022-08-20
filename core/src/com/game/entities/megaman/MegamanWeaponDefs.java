package com.game.entities.megaman;

import com.badlogic.gdx.math.Vector2;
import com.game.core.Component;
import com.game.core.GameContext2d;
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

import static com.game.core.ConstVals.SoundAsset.*;
import static com.game.core.ConstVals.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.WALL_SLIDING;
import static com.game.entities.contracts.Facing.F_LEFT;
import static com.game.entities.megaman.MegamanWeapon.FLAME_TOSS;
import static com.game.entities.megaman.MegamanWeapon.MEGA_BUSTER;
import static com.game.world.BodySense.FEET_ON_GROUND;

public class MegamanWeaponDefs {

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
    
    private boolean isCharging() {
        return megaman.isCharging();
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
                spawnPos.y += PPM / 8f + bodyComponent.getPosDelta().y;
            }
            return spawnPos;
        };
        megamanWeaponDefs.put(MEGA_BUSTER, new WeaponDef(() -> {
            Vector2 trajectory = new Vector2(15f * (isFacing(F_LEFT) ? -PPM : PPM), 0f);
            if (isCharging()) {
                return List.of(new ChargedShot(gameContext, megaman, trajectory, spawn.get(), getFacing()));
            } else {
                return List.of(new Bullet(gameContext, megaman, trajectory, spawn.get()));
            }
        }, .1f, () -> {
            if (isCharging()) {
                getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_CHARGED_SHOT_SOUND);
            } else {
                getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_BULLET_SHOT_SOUND);
            }
        }));
        megamanWeaponDefs.put(FLAME_TOSS, new WeaponDef(() -> {
            Vector2 impulse = new Vector2(35f * (isFacing(F_LEFT) ? -PPM : PPM), 10f * PPM);
            /*
            if (isCharging()) {
                // TODO: return charging fireball
            } else {
                // TODO: return normal fireball
            }
             */
            return List.of(new Fireball(gameContext, megaman, impulse, spawn.get()));
        }, .75f, () -> getComponent(SoundComponent.class).requestSound(CRASH_BOMBER_SOUND)));
    }

}
