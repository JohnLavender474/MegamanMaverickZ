package com.game.acting;

import com.game.utils.Updatable;

import java.util.List;
import java.util.function.Supplier;

public record ActorAction(ActorState state, List<Supplier<Boolean>> overrides, Updatable action) {}
