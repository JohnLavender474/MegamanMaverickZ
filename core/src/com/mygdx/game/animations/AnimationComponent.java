package com.mygdx.game.animations;

import com.mygdx.game.Component;
import com.mygdx.game.utils.SoundAndAnimationState;
import com.mygdx.game.utils.TimedAnimation;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@Setter
public class AnimationComponent implements Component {
    private SoundAndAnimationState soundAndAnimationState = SoundAndAnimationState.PLAY;
    private final Map<String, TimedAnimation> animations = new HashMap<>();
    private Supplier<String> currentKeySupplier;
    private String priorKey;
}
