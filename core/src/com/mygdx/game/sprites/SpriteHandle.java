package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SpriteHandle implements Comparable<SpriteHandle> {

    private Sprite sprite = new Sprite();
    private Integer zpos = 0;

    @Override
    public int compareTo(SpriteHandle o) {
        return zpos.compareTo(o.getZpos());
    }

}
