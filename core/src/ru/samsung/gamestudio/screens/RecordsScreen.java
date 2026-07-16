package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.MyGdxGame;
import ru.samsung.gamestudio.components.ButtonView;
import ru.samsung.gamestudio.components.ImageView;
import ru.samsung.gamestudio.components.MovingBackgroundView;
import ru.samsung.gamestudio.components.RecordsListView;
import ru.samsung.gamestudio.components.TextView;
import ru.samsung.gamestudio.managers.MemoryManager;

public class RecordsScreen extends ScreenAdapter {

    private final MyGdxGame myGdxGame;
    private final MovingBackgroundView backgroundView;
    private final ImageView blackoutView;
    private final TextView titleView;
    private final RecordsListView recordsListView;
    private final ButtonView returnButton;

    public RecordsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        blackoutView = new ImageView(85, 365, GameResources.BLACKOUT_MIDDLE_IMG_PATH);
        titleView = new TextView(myGdxGame.largeWhiteFont, 260, 956, "Records");
        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        returnButton = new ButtonView(
                280, 447, 160, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "return"
        );
    }

    @Override
    public void show() {
        recordsListView.setRecords(MemoryManager.loadRecordsTable());
    }

    @Override
    public void render(float delta) {
        handleInput();
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        blackoutView.draw(myGdxGame.batch);
        titleView.draw(myGdxGame.batch);
        recordsListView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        myGdxGame.touch = myGdxGame.camera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0),
                myGdxGame.viewport.getScreenX(),
                myGdxGame.viewport.getScreenY(),
                myGdxGame.viewport.getScreenWidth(),
                myGdxGame.viewport.getScreenHeight()
        );
        if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
            myGdxGame.setScreen(myGdxGame.menuScreen);
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        blackoutView.dispose();
        titleView.dispose();
        recordsListView.dispose();
        returnButton.dispose();
    }
}
