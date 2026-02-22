package ru.livins.aeroboost.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.livins.aeroboost.model.GameModel;
import ru.livins.aeroboost.model.GameState;
import ru.livins.aeroboost.model.GameStateObserver;
import ru.livins.aeroboost.model.PlaneItem;
import ru.livins.aeroboost.model.RunningPlane;

public class MainBoardViewModel extends ViewModel implements GameStateObserver {

    private GameModel model = null;

    private final MutableLiveData<String> userName = new MutableLiveData<>();
    public LiveData<String> getUserName() { ensureModelLoaded(); return userName; }

    private final MutableLiveData<Double> gameSpeed = new MutableLiveData<>();
    public LiveData<Double> getGameSpeed() { ensureModelLoaded(); return gameSpeed; }

    private final MutableLiveData<Double> totalCoins = new MutableLiveData<>();
    public LiveData<Double> getTotalCoins() { ensureModelLoaded(); return totalCoins; }

    private int gameBoardVersionCounter = 0;
    private final MutableLiveData<Integer> gameBoardVersion = new MutableLiveData<>();
    public LiveData<Integer> getGameBoardVersion() { ensureModelLoaded(); return gameBoardVersion; }

    private void ensureModelLoaded() {
        if (model != null)
            return;

        model = GameModel.getInstance();

        model.gameStateObservable.registerObserver(this);
    }

    public void onPlaneAdded(RunningPlane runningPlane) {
        model.addRunningPlane(runningPlane);
    }

    public void onPlaneRemoved(PlaneItem plane) {

    }

    @Override
    public void onGameStateUpdated(GameState newState) {
        userName.postValue(newState.getUserName());
        gameSpeed.postValue(newState.getGameSpeed());
        totalCoins.postValue(newState.getTotalCoins());
        gameBoardVersion.postValue(gameBoardVersionCounter++);
    }
}
