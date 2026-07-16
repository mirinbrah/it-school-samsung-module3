package ru.samsung.gamestudio;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.managers.MemoryManager;

import java.util.ArrayList;


public class GameSession {

    public GameState state;
    long nextTrashSpawnTime;
    long sessionStartTime;
    long pauseStartTime;
    long lastScoreUpdateTime;
    long scoreTimeRemainder;
    private int score;

    public GameSession() {
    }

    public void startGame() {
        state = GameState.PLAYING;
        score = 0;
        scoreTimeRemainder = 0;
        sessionStartTime = TimeUtils.millis();
        lastScoreUpdateTime = sessionStartTime;
        nextTrashSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                * getTrashPeriodCoolDown());
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        long pauseDuration = TimeUtils.millis() - pauseStartTime;
        sessionStartTime += pauseDuration;
        nextTrashSpawnTime += pauseDuration;
        lastScoreUpdateTime = TimeUtils.millis();
    }

    public void endGame() {
        state = GameState.ENDED;
    }

    public void saveResult(String playerName) {
        ArrayList<GameRecord> recordsTable = MemoryManager.loadRecordsTable();
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx).score < getScore()) break;
        }
        recordsTable.add(foundIdx, new GameRecord(playerName, getScore()));
        MemoryManager.saveTableOfRecords(recordsTable);
    }

    public void destructionRegistration(boolean doubleScore) {
        score += doubleScore ? 200 : 100;
    }

    public void missedTrashRegistration() {
        score = Math.max(0, score - 100);
    }

    public void updateScore(boolean doubleScore) {
        long currentTime = TimeUtils.millis();
        scoreTimeRemainder += (currentTime - lastScoreUpdateTime) * (doubleScore ? 2 : 1);
        score += (int) (scoreTimeRemainder / 100);
        scoreTimeRemainder %= 100;
        lastScoreUpdateTime = currentTime;
    }

    public int getScore() {
        return score;
    }

    public boolean shouldSpawnTrash() {
        if (nextTrashSpawnTime <= TimeUtils.millis()) {
            nextTrashSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                    * getTrashPeriodCoolDown());
            return true;
        }
        return false;
    }

    private float getTrashPeriodCoolDown() {
        return (float) Math.exp(-0.001 * (TimeUtils.millis() - sessionStartTime + 1) / 1000);
    }
}
