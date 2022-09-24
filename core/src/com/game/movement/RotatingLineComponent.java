package com.game.movement;

import com.game.Component;
import com.game.utils.interfaces.UpdatableConsumer;
import com.game.utils.objects.RotatingLine;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RotatingLineComponent extends Component {

    private RotatingLine rotatingLine;
    private UpdatableConsumer<RotatingLine> updatableConsumer;

}
