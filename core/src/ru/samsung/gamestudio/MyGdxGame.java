package ru.samsung.gamestudio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.samsung.gamestudio.managers.AudioManager;
import ru.samsung.gamestudio.objects.GameObject;
import ru.samsung.gamestudio.screens.GameScreen;
import ru.samsung.gamestudio.screens.MenuScreen;
import ru.samsung.gamestudio.screens.RecordsScreen;
import ru.samsung.gamestudio.screens.SettingsScreen;

import static ru.samsung.gamestudio.GameSettings.*;

public class MyGdxGame extends Game {

    public World world;

    public BitmapFont largeWhiteFont;
    public BitmapFont commonWhiteFont;
    public BitmapFont commonBlackFont;

    public Vector3 touch;
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;
    public AudioManager audioManager;

    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    public SettingsScreen settingsScreen;
    public RecordsScreen recordsScreen;

    float accumulator = 0;

    @Override
    public void create() {

        Box2D.init();
        world = new World(new Vector2(0, 0), true);

        largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);
        commonWhiteFont = FontBuilder.generate(24, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(24, Color.BLACK, GameResources.FONT_PATH);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(
                GameSettings.SCREEN_WIDTH,
                GameSettings.SCREEN_HEIGHT,
                camera
        );
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        audioManager = new AudioManager();

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        recordsScreen = new RecordsScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
        menuScreen.dispose();
        settingsScreen.dispose();
        recordsScreen.dispose();

        largeWhiteFont.dispose();
        commonWhiteFont.dispose();
        commonBlackFont.dispose();
        audioManager.dispose();
        GameObject.disposeTextures();
        world.dispose();
        batch.dispose();
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += Math.min(delta, 0.25f);

        while (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
}
