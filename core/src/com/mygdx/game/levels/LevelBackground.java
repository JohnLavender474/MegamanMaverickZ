package com.mygdx.game.levels;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LevelBackground {

    private final List<Sprite> backgroundSprites = new ArrayList<>();

    public LevelBackground(TextureRegion textureRegion, float startX, float startY,
                           float width, float height, int rows, int cols) {
        Sprite backgroundModel = new Sprite(textureRegion);
        backgroundModel.setBounds(startX, startY, width, height);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Sprite backgroundSprite = new Sprite(backgroundModel);
                backgroundSprite.setPosition(startX + width * cols, startY + height * rows);
                backgroundSprites.add(backgroundSprite);
            }
        }
    }

}
