package com.game.sounds;

import static com.game.ConstVals.*;

public record SoundRequest(SoundAsset request, Boolean loop, Float volume) {}
