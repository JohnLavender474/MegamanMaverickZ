package com.game.movement;

import com.game.core.Component;
import com.game.utils.interfaces.UpdatableConsumer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RotatingLineComponent extends Component {

    private RotatingLine rotatingLine;
    private UpdatableConsumer<RotatingLine> updatableConsumer;

}
