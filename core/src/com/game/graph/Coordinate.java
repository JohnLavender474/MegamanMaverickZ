package com.game.graph;

import com.badlogic.gdx.math.Vector2;
import com.game.utils.enums.FloatToInt;
import lombok.*;

import static com.game.utils.enums.FloatToInt.*;
import static java.lang.Math.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate implements Comparable<Coordinate> {

    public int x;
    public int y;

    public Coordinate set(Vector2 v, FloatToInt xFTI, FloatToInt yFTI) {
        if (xFTI == FLOOR) {
            x = (int) floor(v.x);
        } else if (xFTI == CEIL) {
            x = (int) ceil(v.x);
        } else {
            x = (int) v.x;
        }
        if (yFTI == FLOOR) {
            y = (int) floor(v.y);
        } else if (yFTI == CEIL) {
            y = (int) ceil(v.y);
        } else {
            y = (int) v.y;
        }
        return this;
    }

    @Override
    public int compareTo(Coordinate o) {
        int comp = x - o.getX();
        return comp != 0 ? comp : y - o.getY();
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

}
