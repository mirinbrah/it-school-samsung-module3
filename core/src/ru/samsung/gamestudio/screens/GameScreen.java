package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.*;
import ru.samsung.gamestudio.components.*;
import ru.samsung.gamestudio.managers.ContactManager;
import ru.samsung.gamestudio.managers.MemoryManager;
import ru.samsung.gamestudio.objects.BulletObject;
import ru.samsung.gamestudio.objects.HeartObject;
import ru.samsung.gamestudio.objects.ShipObject;
import ru.samsung.gamestudio.objects.TrashObject;
import ru.samsung.gamestudio.objects.WildDebrisObject;

import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    ShipObject shipObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;
    ArrayList<HeartObject> heartArray;

    ContactManager contactManager;
    boolean resultSaved;
    boolean nameRequested;
    boolean doubleScoreActive;
    ExplosionView explosionView;

    // PLAY state UI
    MovingBackgroundView backgroundView;
    ImageView topBlackoutView;
    LiveView liveView;
    TextView scoreTextView;
    ButtonView pauseButton;

    // PAUSED state UI
    ImageView fullBlackoutView;
    TextView pauseTextView;
    ButtonView homeButton;
    ButtonView continueButton;

    // ENDED state UI
    TextView recordsTextView;
    RecordsListView recordsListView;
    ButtonView homeButton2;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();

        contactManager = new ContactManager(myGdxGame.world);

        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        heartArray = new ArrayList<>();

        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH,
                myGdxGame.world
        );

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1215);
        pauseButton = new ButtonView(
                605, 1200,
                46, 54,
                GameResources.PAUSE_IMG_PATH
        );
        explosionView = new ExplosionView(GameResources.EXPLOSION_IMG_PATH);

        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH);
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 282, 842, "Pause");
        homeButton = new ButtonView(
                138, 695,
                200, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "Home"
        );
        continueButton = new ButtonView(
                393, 695,
                200, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "Continue"
        );

        recordsListView = new RecordsListView(myGdxGame.commonWhiteFont, 690);
        recordsTextView = new TextView(myGdxGame.largeWhiteFont, 206, 842, "Last records");
        homeButton2 = new ButtonView(
                280, 365,
                160, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "Home"
        );

    }

    @Override
    public void show() {
        restartGame();
    }

    @Override
    public void render(float delta) {

        handleInput();

        if (gameSession.state == GameState.PLAYING && !shipObject.isAlive()) {
            explosionView.start(shipObject.getX(), shipObject.getY());
            gameSession.endGame();
        }

        if (gameSession.state == GameState.PLAYING) {
            doubleScoreActive = shipObject.getY() > GameSettings.SCREEN_HEIGHT / 3f;
            gameSession.updateScore(doubleScoreActive);
            scoreTextView.setColor(doubleScoreActive ? Color.RED : Color.WHITE);

            if (gameSession.shouldSpawnTrash()) {
                spawnFallingObject();
            }

            if (shipObject.needToShoot()) {
                BulletObject laserBullet = new BulletObject(
                        shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                        GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                        GameResources.BULLET_IMG_PATH,
                        myGdxGame.world
                );
                bulletArray.add(laserBullet);
                if (myGdxGame.audioManager.isSoundOn) myGdxGame.audioManager.shootSound.play();
            }

            updateTrash(doubleScoreActive);
            updateBullets();
            updateHearts();
            backgroundView.move();
            scoreTextView.setText("Score: " + gameSession.getScore());
            liveView.setLeftLives(shipObject.getLiveLeft());

            myGdxGame.stepWorld();
        }

        explosionView.update(delta);
        if (gameSession.state == GameState.ENDED && !explosionView.isActive() && !nameRequested) {
            requestPlayerName();
        }

        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            boolean justTouched = Gdx.input.justTouched();

            myGdxGame.touch = myGdxGame.camera.unproject(
                    new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0),
                    myGdxGame.viewport.getScreenX(),
                    myGdxGame.viewport.getScreenY(),
                    myGdxGame.viewport.getScreenWidth(),
                    myGdxGame.viewport.getScreenHeight()
            );

            switch (gameSession.state) {
                case PLAYING:
                    if (justTouched && pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.pauseGame();
                        break;
                    }
                    shipObject.move(myGdxGame.touch);
                    break;

                case PAUSED:
                    if (!justTouched) break;

                    if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    break;

                case ENDED:
                    if (!justTouched) break;

                    if (homeButton2.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    break;
            }

        }
    }

    private void draw() {

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        backgroundView.draw(myGdxGame.batch);
        for (TrashObject trash : trashArray) trash.draw(myGdxGame.batch);
        for (HeartObject heart : heartArray) heart.draw(myGdxGame.batch);
        if (shipObject.isAlive()) shipObject.draw(myGdxGame.batch);
        explosionView.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(myGdxGame.batch);
        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        liveView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);

        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        } else if (gameSession.state == GameState.ENDED && !explosionView.isActive()) {
            fullBlackoutView.draw(myGdxGame.batch);
            recordsTextView.draw(myGdxGame.batch);
            recordsListView.draw(myGdxGame.batch);
            homeButton2.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();

    }

    private void updateTrash(boolean doubleScore) {
        for (int i = 0; i < trashArray.size(); i++) {
            TrashObject trashObject = trashArray.get(i);
            if (trashObject instanceof WildDebrisObject) {
                ((WildDebrisObject) trashObject).updateDirection();
            }

            boolean isDestroyed = !trashObject.isAlive();
            boolean isMissed = !isDestroyed && !trashObject.isInFrame();
            boolean hasToBeDestroyed = isDestroyed || isMissed;

            if (isDestroyed) {
                if (trashObject.wasHitByBullet()) {
                    gameSession.destructionRegistration(doubleScore);
                }
                if (myGdxGame.audioManager.isSoundOn) myGdxGame.audioManager.explosionSound.play(0.2f);
            }

            if (isMissed) gameSession.missedTrashRegistration();

            if (hasToBeDestroyed) {
                myGdxGame.world.destroyBody(trashObject.body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                BulletObject bullet = bulletArray.get(i);
                myGdxGame.world.destroyBody(bullet.body);
                bulletArray.remove(i--);
            }
        }
    }

    private void updateHearts() {
        for (int i = 0; i < heartArray.size(); i++) {
            HeartObject heart = heartArray.get(i);
            if (heart.isCollected() || !heart.isInFrame()) {
                myGdxGame.world.destroyBody(heart.body);
                heartArray.remove(i--);
            }
        }
    }

    private void spawnFallingObject() {
        float spawnRoll = MathUtils.random();
        if (spawnRoll < GameSettings.HEART_SPAWN_CHANCE) {
            heartArray.add(new HeartObject(
                    GameSettings.HEART_WIDTH,
                    GameSettings.HEART_HEIGHT,
                    GameResources.LIVE_IMG_PATH,
                    myGdxGame.world
            ));
            return;
        }
        if (spawnRoll < GameSettings.HEART_SPAWN_CHANCE + GameSettings.WILD_DEBRIS_SPAWN_CHANCE) {
            trashArray.add(new WildDebrisObject(
                    GameSettings.TRASH_WIDTH,
                    GameSettings.TRASH_HEIGHT,
                    GameResources.TRASH_IMG_PATH,
                    myGdxGame.world
            ));
            return;
        }
        trashArray.add(new TrashObject(
                GameSettings.TRASH_WIDTH,
                GameSettings.TRASH_HEIGHT,
                GameResources.TRASH_IMG_PATH,
                myGdxGame.world
        ));
    }

    private void restartGame() {

        for (int i = 0; i < trashArray.size(); i++) {
            TrashObject trash = trashArray.get(i);
            myGdxGame.world.destroyBody(trash.body);
            trashArray.remove(i--);
        }

        if (shipObject != null) {
            myGdxGame.world.destroyBody(shipObject.body);
        }

        for (int i = 0; i < bulletArray.size(); i++) {
            BulletObject bullet = bulletArray.get(i);
            myGdxGame.world.destroyBody(bullet.body);
            bulletArray.remove(i--);
        }

        for (int i = 0; i < heartArray.size(); i++) {
            HeartObject heart = heartArray.get(i);
            myGdxGame.world.destroyBody(heart.body);
            heartArray.remove(i--);
        }

        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH,
                myGdxGame.world
        );

        resultSaved = false;
        nameRequested = false;
        doubleScoreActive = false;
        scoreTextView.setColor(Color.WHITE);
        gameSession.startGame();
    }

    private void requestPlayerName() {
        nameRequested = true;
        String currentName = MemoryManager.loadPlayerName();
        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                saveResult(text);
            }

            @Override
            public void canceled() {
                saveResult(currentName);
            }
        }, "Game over", currentName, "Player name");
    }

    private void saveResult(String text) {
        if (resultSaved) return;
        String playerName = MemoryManager.normalizePlayerName(text);
        MemoryManager.savePlayerName(playerName);
        gameSession.saveResult(playerName);
        recordsListView.setRecords(MemoryManager.loadRecordsTable());
        resultSaved = true;
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        topBlackoutView.dispose();
        liveView.dispose();
        scoreTextView.dispose();
        pauseButton.dispose();
        explosionView.dispose();
        fullBlackoutView.dispose();
        pauseTextView.dispose();
        homeButton.dispose();
        continueButton.dispose();
        recordsTextView.dispose();
        recordsListView.dispose();
        homeButton2.dispose();
    }

}
