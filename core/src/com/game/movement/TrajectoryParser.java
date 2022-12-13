package com.game.movement;

import com.badlogic.gdx.math.Vector2;
import com.game.utils.objects.KeyValuePair;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryParser {

    public static List<KeyValuePair<Vector2, Float>> parse(String trajectory) {
        return parse(trajectory, 1f);
    }

    public static List<KeyValuePair<Vector2, Float>> parse(String[] tokens) {
        return parse(tokens, 1f);
    }

    public static List<KeyValuePair<Vector2, Float>> parse(String trajectory, float scale) {
        String[] tokens = trajectory.split(";");
        return parse(tokens, scale);
    }

    public static List<KeyValuePair<Vector2, Float>> parse(String[] tokens, float scale) {
        List<KeyValuePair<Vector2, Float>> ans = new ArrayList<>();
        for (String s : tokens) {
            String[] params = s.split(",");
            float x = Float.parseFloat(params[0]);
            float y = Float.parseFloat(params[1]);
            float time = Float.parseFloat(params[2]);
            Vector2 v = new Vector2(x, y).scl(scale);
            ans.add(KeyValuePair.of(v, time));
        }
        return ans;
    }

}
