package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ExplosionView extends View {

    private static final float DURATION = 0.8f;
    private static final float MAX_SIZE = 300f;

    private final Texture texture;
    private float elapsed;
    private boolean active;

    public ExplosionView(String texturePath) {
        super(0, 0);
        texture = new Texture(texturePath);
    }

    public void start(float x, float y) {
        this.x = x;
        this.y = y;
        elapsed = 0;
        active = true;
    }

    public void update(float delta) {
        if (!active) return;
        elapsed += delta;
        if (elapsed >= DURATION) active = false;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!active) return;
        float progress = elapsed / DURATION;
        float size = (float) Math.sin(Math.PI * progress) * MAX_SIZE;
        batch.draw(texture, x - size / 2, y - size / 2, size, size);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
