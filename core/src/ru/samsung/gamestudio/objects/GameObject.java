package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

import java.util.HashMap;
import java.util.Map;

import static ru.samsung.gamestudio.GameSettings.SCALE;

public class GameObject {

    private static final Map<String, Texture> TEXTURES = new HashMap<>();

    public short cBits;

    public int width;
    public int height;

    public Body body;
    Texture texture;
    private final Color tint = new Color(Color.WHITE);

    GameObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;

        texture = TEXTURES.computeIfAbsent(texturePath, Texture::new);
        body = createBody(x, y, world);
    }

    public void draw(SpriteBatch batch) {
        Color batchColor = batch.getColor();
        float red = batchColor.r;
        float green = batchColor.g;
        float blue = batchColor.b;
        float alpha = batchColor.a;
        batch.setColor(tint);
        batch.draw(texture,
                getX() - (width / 2f),
                getY() - (height / 2f),
                width,
                height);
        batch.setColor(red, green, blue, alpha);
    }

    protected void setTint(float red, float green, float blue, float alpha) {
        tint.set(red, green, blue, alpha);
    }

    public void hit() {
    }

    public static void disposeTextures() {
        for (Texture texture : TEXTURES.values()) texture.dispose();
        TEXTURES.clear();
    }

    public int getX() {
        return (int) (body.getPosition().x / SCALE);
    }

    public int getY() {
        return (int) (body.getPosition().y / SCALE);
    }

    public void setX(int x) {
        body.setTransform(x * SCALE, body.getPosition().y, 0);
    }

    public void setY(int y) {
        body.setTransform(body.getPosition().x, y * SCALE, 0);
    }

    private Body createBody(float x, float y, World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        Body body = world.createBody(def);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(Math.max(width, height) * SCALE / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = getCollisionMask();

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        circleShape.dispose();

        body.setTransform(x * SCALE, y * SCALE, 0);
        return body;
    }

    private short getCollisionMask() {
        if (cBits == GameSettings.TRASH_BIT) {
            return (short) (GameSettings.SHIP_BIT | GameSettings.BULLET_BIT);
        }
        if (cBits == GameSettings.SHIP_BIT) {
            return (short) (GameSettings.TRASH_BIT | GameSettings.HEART_BIT);
        }
        if (cBits == GameSettings.BULLET_BIT) {
            return GameSettings.TRASH_BIT;
        }
        if (cBits == GameSettings.HEART_BIT) return GameSettings.SHIP_BIT;
        return 0;
    }

}
