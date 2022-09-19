package com.game.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.megaman.Megaman;
import com.game.world.BodyComponent;

import java.util.Map;
import java.util.function.Supplier;

public class SpringHead extends AbstractEnemy {

    private static final float DAMAGE_DURATION = .5f;

    public SpringHead(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, DAMAGE_DURATION);
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return Map.of();
    }

    private BodyComponent defineBodyComponent() {
        return null;
    }

}
