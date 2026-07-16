package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

import java.util.Random;

public class HeartObject extends GameObject {

    private static final int PADDING_HORIZONTAL = 30;
    private boolean collected;

    public HeartObject(int width, int height, String texturePath, World world) {
        super(
                texturePath,
                width / 2 + PADDING_HORIZONTAL + new Random().nextInt(
                        GameSettings.SCREEN_WIDTH - 2 * PADDING_HORIZONTAL - width
                ),
                GameSettings.SCREEN_HEIGHT + height / 2,
                width,
                height,
                GameSettings.HEART_BIT,
                world
        );
        body.setLinearVelocity(new Vector2(0, -GameSettings.TRASH_VELOCITY));
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isInFrame() {
        return getY() + height / 2 > 0;
    }

    @Override
    public void hit() {
        collected = true;
    }
}
