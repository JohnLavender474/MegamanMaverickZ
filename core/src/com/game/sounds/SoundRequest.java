package com.game.sounds;

import com.game.assets.SoundAsset;

public record SoundRequest(SoundAsset request, Boolean loop, Float volume) {}
