package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextView extends View{

    protected BitmapFont font;
    protected String text;
    private final Color defaultColor;
    private Color color;

    public TextView(BitmapFont font, float x, float y) {
        super(x, y);
        this.font = font;
        defaultColor = new Color(font.getColor());
        color = new Color(defaultColor);
    }

    public TextView(BitmapFont font, float x, float y, String text) {
        this(font, x, y);
        this.text = text;

        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
    }

    public void setText(String text) {
        this.text = text;
        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    @Override
    public void draw(SpriteBatch batch) {
        font.setColor(color);
        font.draw(batch, text, x, y + height);
        font.setColor(defaultColor);
    }

    @Override
    public void dispose() {
    }

}
