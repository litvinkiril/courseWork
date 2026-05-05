package ru.livins.aeroboost.view;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

public class SaveManager {
    private static final String PREFS_NAME = "PlaneGameSave";
    private static final String KEY_TOTAL_COINS = "total_coins";
    private static final String KEY_GRID = "grid";
    private static final String KEY_COUNTS = "purchased_counts";
    private static final String KEY_NAME = "player_name";
    private static final String KEY_HAS_SAVE = "has_save";
    private static final String KEY_OPENED = "was_opened";

    private SharedPreferences prefs;

    public SaveManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveGame(int[][] grid, int[] purchasedCounts, String playerName, double totalCoin, boolean[] isOpened) {
        SharedPreferences.Editor editor = prefs.edit();

        // Отмечаем что сохранение существует
        editor.putBoolean(KEY_HAS_SAVE, true);

        // Сохраняем totalCoin
        editor.putLong(KEY_TOTAL_COINS, Double.doubleToRawLongBits(totalCoin));

        // Сохраняем grid (8 значений)
        JSONArray gridArray = new JSONArray();
        for (int pos = 0; pos < 8; ++pos) {
            gridArray.put(Math.abs(grid[pos / 2][pos % 2]));
        }
        editor.putString(KEY_GRID, gridArray.toString());

        // Сохраняем purchasedCounts (10 значений)
        JSONArray countsArray = new JSONArray();
        for (int val : purchasedCounts) countsArray.put(val);
        editor.putString(KEY_COUNTS, countsArray.toString());

        // Сохраняем имя
        editor.putString(KEY_NAME, playerName);

        // Сохраняем открытие самолетиков как JSON
        JSONArray openedArray = new JSONArray();
        for (boolean b : isOpened) openedArray.put(b);
        editor.putString(KEY_OPENED, openedArray.toString());

        editor.apply();
        Log.d("SaveManager", "Saved: coins=" + totalCoin + ", player=" + playerName);
    }

    public GameSaveData loadGame() {
        int[][] grid = new int[4][2];
        int[] purchasedCounts = new int[10];
        boolean[] isOpened = new boolean[10];
        String playerName = "Player";
        double totalCoin;
        boolean isFirstLaunch;

        // Проверяем, есть ли сохранение
        isFirstLaunch = !prefs.getBoolean(KEY_HAS_SAVE, false);

        if (isFirstLaunch) {
            // ПЕРВЫЙ ЗАПУСК
            totalCoin = 1000.0;
            purchasedCounts[0] = 1;  // Первый самолет доступен
            isOpened[0] = true;
            Log.d("SaveManager", "FIRST LAUNCH: coins=1000, plane0=1");
        } else {
            // ЗАГРУЗКА СУЩЕСТВУЮЩЕГО
            totalCoin = Double.longBitsToDouble(prefs.getLong(KEY_TOTAL_COINS, 0));
        }

        try {
            // Загружаем grid
            String gridStr = prefs.getString(KEY_GRID, "");
            if (!gridStr.isEmpty()) {
                JSONArray gridArray = new JSONArray(gridStr);
                for (int i = 0; i < 8 && i < gridArray.length(); i++) {
                    grid[i / 2][i % 2] = gridArray.getInt(i);
                }
            }

            // Загружаем purchasedCounts (только если не первый запуск)
            if (!isFirstLaunch) {
                String countsStr = prefs.getString(KEY_COUNTS, "");
                if (!countsStr.isEmpty()) {
                    JSONArray countsArray = new JSONArray(countsStr);
                    for (int i = 0; i < 10 && i < countsArray.length(); i++) {
                        purchasedCounts[i] = countsArray.getInt(i);
                    }
                }
            }

            // Загружаем isOpened
            String openedStr = prefs.getString(KEY_OPENED, "");
            if (!openedStr.isEmpty()) {
                JSONArray openedArray = new JSONArray(openedStr);
                for (int i = 0; i < openedArray.length() && i < isOpened.length; i++) {
                    isOpened[i] = openedArray.getBoolean(i);
                }
            }

            // Загружаем имя
            if (!isFirstLaunch) {
                playerName = prefs.getString(KEY_NAME, "Player");
            }

            Log.d("SaveManager", "Loaded: coins=" + totalCoin + ", player=" + playerName + ", firstLaunch=" + isFirstLaunch);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new GameSaveData(grid, purchasedCounts, playerName, totalCoin, isFirstLaunch, isOpened);
    }

    public void clearSave() {
        prefs.edit().clear().apply();
        Log.d("SaveManager", "Save cleared - next launch will be FIRST LAUNCH");
    }

    public static class GameSaveData {
        public int[][] grid;
        public int[] purchasedCounts;
        public String playerName;
        public double totalCoin;
        public boolean isFirstLaunch;
        public boolean[] isOpened;

        public GameSaveData(int[][] grid, int[] purchasedCounts, String playerName, double totalCoin, boolean isFirstLaunch, boolean[] isOpened) {
            this.grid = grid;
            this.purchasedCounts = purchasedCounts;
            this.playerName = playerName;
            this.totalCoin = totalCoin;
            this.isFirstLaunch = isFirstLaunch;
            this.isOpened = isOpened;
        }
    }
}