package ru.samsung.gamestudio.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import ru.samsung.gamestudio.GameRecord;

import java.util.ArrayList;
import java.util.Locale;

public class MemoryManager {

    private static final Preferences preferences = Gdx.app.getPreferences("User saves");

    public static void saveSoundSettings(boolean isOn) {
        preferences.putBoolean("isSoundOn", isOn);
        preferences.flush();
    }

    public static boolean loadIsSoundOn() {
        return preferences.getBoolean("isSoundOn", true);
    }

    public static void saveMusicSettings(boolean isOn) {
        preferences.putBoolean("isMusicOn", isOn);
        preferences.flush();
    }

    public static boolean loadIsMusicOn() {
        return preferences.getBoolean("isMusicOn", true);
    }

    public static void saveTableOfRecords(ArrayList<GameRecord> table) {
        if (table.size() > 5) table = new ArrayList<>(table.subList(0, 5));
        Json json = new Json();
        String tableInString = json.toJson(table);
        preferences.putString("recordTableV2", tableInString);
        preferences.flush();
    }

    public static ArrayList<GameRecord> loadRecordsTable() {
        Json json = new Json();

        if (preferences.contains("recordTableV2")) {
            return json.fromJson(
                    ArrayList.class,
                    GameRecord.class,
                    preferences.getString("recordTableV2")
            );
        }

        ArrayList<GameRecord> records = new ArrayList<>();
        if (!preferences.contains("recordTable")) return records;

        ArrayList<Integer> oldRecords = json.fromJson(
                ArrayList.class,
                Integer.class,
                preferences.getString("recordTable")
        );
        for (Integer score : oldRecords) records.add(new GameRecord("AAA", score));
        saveTableOfRecords(records);
        return records;
    }

    public static void savePlayerName(String playerName) {
        preferences.putString("playerName", normalizePlayerName(playerName));
        preferences.flush();
    }

    public static String loadPlayerName() {
        return preferences.getString("playerName", "AAA");
    }

    public static String normalizePlayerName(String playerName) {
        if (playerName == null) return "AAA";
        String normalized = playerName.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) return "AAA";
        return normalized.substring(0, Math.min(normalized.length(), 8));
    }

}
