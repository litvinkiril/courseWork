package ru.livins.aeroboost.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GameModel {

    // Получает на вход текущее состояние игры (набор самолетов + их характеристики + их положение на поле)
    // Вызывает пересчет в ядре
    // Возвращает новое состояние, которое передается во ViewModel
    private static native GameState doGameStep(GameState prevState);


    // Singleton.
    private static GameModel theInstance = null;
    public static GameModel getInstance() {
        if (theInstance != null)
            return theInstance;

        // то синглтон, поэтому грузим ядро только один раз.
        // Загрузка ядра с логикой расчетов.
        System.loadLibrary("aeroboost-core");

        // todo - грузить из файла
        // но сейчас просто вот так
        var initialState = new GameState();
        initialState.setUserName("Andrej");
        initialState.setGameSpeed(10);
        initialState.setTotalCoins(0.0);
        initialState.setRunningPlanes(new ArrayList<>());

        theInstance = new GameModel(initialState);
        return theInstance;
    }

    // Храним состояние игры.
    public GameStateObservable gameStateObservable = new GameStateObservable();

    //
    private final Thread gameStepsThread;
    private boolean gameRunning = true;
    private GameState state;

    private GameModel(GameState initialState) {

        state = initialState;
        gameStateObservable.updateState(initialState);

        var runnable = new Runnable() {
            @Override
            public void run() {
                while (gameRunning) {
                    try {
                        var currentState = gameStateObservable.getState();
                        if (currentState != null) {
                            var newState = doGameStep(currentState);
                            gameStateObservable.updateState(newState);
                        }
                        // 10 кадров в секунду.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        gameStepsThread = new Thread(runnable);
        gameStepsThread.start();
    }

    public void stopGame() {
        gameRunning = false;
    }

    public void addRunningPlane(RunningPlane runningPlane) {
        var runningPlanes = theInstance.state.getRunningPlanes();
        runningPlanes.add(runningPlane);
    }
}
