package com.game.sounds;

import com.game.core.constants.SoundAsset;

public record SoundRequest(SoundAsset request, Boolean loop, Float volume) {}
