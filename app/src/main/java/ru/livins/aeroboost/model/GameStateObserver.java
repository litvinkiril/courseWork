package ru.livins.aeroboost.model;

public interface GameStateObserver {
    void onGameStateUpdated(GameState gameState);
}
