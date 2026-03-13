package ru.livins.aeroboost.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameModel {

    private static native GameState doGameStep(GameState prevState);

    private static GameModel theInstance = null;

    public static GameModel getInstance() {
        if (theInstance == null) {
            System.loadLibrary("aeroboost-core");

            var initialState = new GameState();
            initialState.setUserName("Andrej");
            initialState.setGameSpeed(25);
            initialState.setTotalCoins(111110.0);
            initialState.setRunningPlanes(new ArrayList<>());

            theInstance = new GameModel(initialState);
        }
        return theInstance;
    }

    public GameStateObservable gameStateObservable = new GameStateObservable();

    private final Thread gameStepsThread;
    private volatile boolean gameRunning = true; // volatile для видимости между потоками
    private GameState state;

    private GameModel(GameState initialState) {
        state = initialState;
        gameStateObservable.updateState(initialState);

        // Используем лямбду вместо анонимного класса
        gameStepsThread = new Thread(() -> {
            while (gameRunning) {
                try {
                    GameState currentState = gameStateObservable.getState();
                    if (currentState != null) {
                        GameState newState = doGameStep(currentState);
                        gameStateObservable.updateState(newState);
                        this.state = newState; // обновляем ссылку
                    }
                    Thread.sleep(40); // 10 FPS (1000ms/10 = 100ms)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // восстанавливаем статус прерывания
                    break;
                }
            }
        });

        gameStepsThread.setName("Game-Loop-Thread"); // для отладки
        gameStepsThread.setDaemon(true); // чтобы не блокировать завершение приложения
        gameStepsThread.start();
    }

    public void stopGame() {
        gameRunning = false;
        gameStepsThread.interrupt(); // прерываем поток, если он в sleep
    }

    public void addRunningPlane(RunningPlane runningPlane) {
        // Ваша оригинальная логика, которая работает!
        var runningPlanes = state.getRunningPlanes();
        runningPlanes.add(runningPlane);

        // Уведомляем наблюдателей об изменении
        gameStateObservable.updateState(state);
    }

    // Добавим полезные методы

    public List<RunningPlane> getRunningPlanes() {
        return state.getRunningPlanes(); // просто проксируем вызов к state
    }

    public void removeRunningPlane(RunningPlane runningPlane) {
        var runningPlanes = state.getRunningPlanes();
        runningPlanes.remove(runningPlane);
        gameStateObservable.updateState(state);
    }

    public boolean isGameRunning() {
        return gameRunning;
    }
}