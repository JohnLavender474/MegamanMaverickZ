package com.game.sounds;

import static com.game.core.ConstVals.*;

public record SoundRequest(SoundAsset request, Boolean loop, Float volume) {}
