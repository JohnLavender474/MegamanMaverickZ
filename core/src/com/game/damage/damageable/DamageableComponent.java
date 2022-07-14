package com.game.damage.damageable;

import com.game.Component;
import com.game.utils.Process;
import com.game.utils.Timer;

import java.util.function.Supplier;

public record DamageableComponent(Timer damageTimer, Process damageProcess,
                                  Supplier<Boolean> invincibilitySupplier) implements Component {}
