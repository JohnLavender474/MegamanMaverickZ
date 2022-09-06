package com.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.interfaces.Drawable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

import static com.game.core.ConstVals.ViewVals.PPM;
import static java.lang.Math.round;

@Getter
@Setter
public class MegaFontHandle implements Drawable {

    public static final String DEFAULT_TEXT = "Megaman10Font.ttf";
    public static final int DEFAULT_FONT_SIZE = round(PPM / 2f);

    private final BitmapFont font;
    private final Vector2 position = new Vector2();

    private Supplier<String> text;

    public MegaFontHandle(Vector2 position, Supplier<String> text) {
        this(DEFAULT_TEXT, DEFAULT_FONT_SIZE, position, text);
    }

    public MegaFontHandle(Vector2 position, String text) {
        this(DEFAULT_FONT_SIZE, position, text);
    }

    public MegaFontHandle(int fontSize, Vector2 position) {
        this(DEFAULT_TEXT, fontSize, position);
    }

    public MegaFontHandle(int fontSize, Vector2 position, String text) {
        this(DEFAULT_TEXT, fontSize, position, text);
    }

    public MegaFontHandle(String ttfSrc, int fontSize, Vector2 position) {
        this(ttfSrc, fontSize, position, "");
    }

    public MegaFontHandle(String ttfSrc, int fontSize, Vector2 position, String text) {
        this(ttfSrc, fontSize, position, () -> text);
    }

    public MegaFontHandle(String ttfSrc, int fontSize, Vector2 position, Supplier<String> text) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(ttfSrc));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = fontSize;
        font = generator.generateFont(parameter);
        generator.dispose();
        setPosition(position);
        this.text = text;
    }

    public void clearText() {
        setText(() -> "");
    }

    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setText(String text) {
        setText(() -> text);
    }

    public void setText(Supplier<String> text) {
        this.text = text;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        boolean isDrawing = spriteBatch.isDrawing();
        if (!isDrawing) {
            spriteBatch.begin();
        }
        font.draw(spriteBatch, text.get(), position.x, position.y);
        if (!isDrawing) {
            spriteBatch.end();
        }
    }

}