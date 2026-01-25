package ru.livins.aeroboost.model;

public class GameStateObservable {

    private GameStateObserver gameStateObserver = null;
    private GameState lastState = null;

    public void registerObserver(GameStateObserver observer) {
        gameStateObserver = observer;

        if (gameStateObserver != null) {
            gameStateObserver.onGameStateUpdated(lastState);
        }
    }

    public void updateState(GameState newState) {

        lastState = newState;

        if (gameStateObserver == null)
            return;

        gameStateObserver.onGameStateUpdated(newState);
    }

    public GameState getState() {
        return lastState;
    }
}
