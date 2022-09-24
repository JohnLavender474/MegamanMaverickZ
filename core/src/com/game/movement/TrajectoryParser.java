package com.game.movement;

import com.badlogic.gdx.math.Vector2;
import com.game.utils.objects.KeyValuePair;

import java.util.ArrayList;
import java.util.Collection;

public class TrajectoryParser {

    /**
     * The trajectory string is in the exact form of "x1,y1,t1;x2,y2,t2;...;xi,yi,ti". The string is initially split by
     * semicolon into tokens in the form ["x1,y1,t1", ..., "xi,yi,ti"], and each token is then split by comma so that
     * each token can be parsed as thus:
     *     trajectory x = xi
     *     trajectory y = yi
     *     trajectory time = ti
     *
     * @param trajectory the unparsed trajectory string
     * @return the collection of trajectory definitions
     */
    public static Collection<KeyValuePair<Vector2, Float>> parse(String trajectory) {
        String[] tokens = trajectory.split(";");
        return parse(tokens);
    }

    /**
     * See {@link #parse(String)}.
     *
     * @param tokens the tokens
     * @return the collection of trajectory definitions
     */
    public static Collection<KeyValuePair<Vector2, Float>> parse(String[] tokens) {
        Collection<KeyValuePair<Vector2, Float>> ans = new ArrayList<>();
        for (String s : tokens) {
            String[] params = s.split(",");
            float x = Float.parseFloat(params[0]);
            float y = Float.parseFloat(params[1]);
            float time = Float.parseFloat(params[2]);
            ans.add(KeyValuePair.of(new Vector2(x, y), time));
        }
        return ans;
    }

}
