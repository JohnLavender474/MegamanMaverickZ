package com.game.sounds;

import com.game.constants.SoundAsset;

public record SoundRequest(SoundAsset request, Boolean loop, Float volume) {}
