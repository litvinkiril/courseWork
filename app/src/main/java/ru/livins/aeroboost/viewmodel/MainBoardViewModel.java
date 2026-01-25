package ru.livins.aeroboost.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.livins.aeroboost.model.GameModel;
import ru.livins.aeroboost.model.GameState;
import ru.livins.aeroboost.model.GameStateObserver;
import ru.livins.aeroboost.model.PlaneItem;

public class MainBoardViewModel extends ViewModel implements GameStateObserver {

    private GameModel model = null;

    private final MutableLiveData<String> userName = new MutableLiveData<>();
    public LiveData<String> getUserName() { ensureModelLoaded(); return userName; }

    private final MutableLiveData<Double> totalProfitRate = new MutableLiveData<>();
    public LiveData<Double> getTotalProfitRate() { ensureModelLoaded(); return totalProfitRate; }

    private final MutableLiveData<Double> totalCoins = new MutableLiveData<>();
    public LiveData<Double> getTotalCoins() { ensureModelLoaded(); return totalCoins; }

    private void ensureModelLoaded() {
        if (model != null)
            return;

        model = GameModel.getInstance();

        model.gameStateObservable.registerObserver(this);
    }

    public void onPlaneAdded(PlaneItem plane) {

    }

    public void onPlaneRemoved(PlaneItem plane) {

    }

    @Override
    public void onGameStateUpdated(GameState newState) {
        userName.postValue(newState.getUserName());
        totalProfitRate.postValue(newState.getTotalProfitRate());
        totalCoins.postValue(newState.getTotalCoins());
    }
}
