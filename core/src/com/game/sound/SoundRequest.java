package com.game.sound;

import com.game.utils.Percentage;

public record SoundRequest(String key, boolean looping, Percentage volume) {
}
