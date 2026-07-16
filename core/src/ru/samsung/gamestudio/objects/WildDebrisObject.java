package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

public class WildDebrisObject extends TrashObject {

    public WildDebrisObject(int width, int height, String texturePath, World world) {
        super(width, height, texturePath, world);
        float velocityX = MathUtils.randomBoolean()
                ? GameSettings.WILD_DEBRIS_HORIZONTAL_VELOCITY
                : -GameSettings.WILD_DEBRIS_HORIZONTAL_VELOCITY;
        body.setLinearVelocity(new Vector2(velocityX, -GameSettings.WILD_DEBRIS_VERTICAL_VELOCITY));
        setTint(0.55f, 0.55f, 0.55f, 1f);
    }

    public void updateDirection() {
        float velocityX = body.getLinearVelocity().x;
        if (getX() <= width / 2 && velocityX < 0) {
            setX(width / 2);
            body.setLinearVelocity(Math.abs(velocityX), body.getLinearVelocity().y);
        } else if (getX() >= GameSettings.SCREEN_WIDTH - width / 2 && velocityX > 0) {
            setX(GameSettings.SCREEN_WIDTH - width / 2);
            body.setLinearVelocity(-Math.abs(velocityX), body.getLinearVelocity().y);
        }
    }
}
