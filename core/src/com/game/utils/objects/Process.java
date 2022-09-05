package com.game.utils.objects;

import com.game.utils.interfaces.Updatable;

public record Process(Runnable initRunnable, Updatable actUpdatable, Runnable endRunnable) {}
