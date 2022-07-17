package com.game.utils.objects;

import com.game.updatables.Updatable;

public record Process(Runnable initRunnable, Updatable actUpdatable, Runnable endRunnable) {}
