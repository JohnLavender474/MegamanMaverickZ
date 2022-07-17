package com.game.utils.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.interfaces.Drawable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FontHandle implements Drawable {

    private final BitmapFont font;
    private final Vector2 position = new Vector2();

    private String text = "";

    public FontHandle(String ttfSrc, int fontSize) {
        this(ttfSrc, fontSize, new Vector2());
    }

    public FontHandle(String ttfSrc, int fontSize, Vector2 position) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(ttfSrc));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = fontSize;
        font = generator.generateFont(parameter);
        generator.dispose();
        setPosition(position);
    }

    public void setColor(Color color) {
        font.setColor(color);
    }

    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        boolean isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
        font.draw(spriteBatch, text, position.x, position.y);
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

}