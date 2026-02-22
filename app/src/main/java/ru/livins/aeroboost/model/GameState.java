package ru.livins.aeroboost.model;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {

    private String userName;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String value) { userName = value; }

    private double totalCoins = 100000;
    public double getTotalCoins() { return totalCoins; }
    public void setTotalCoins(double value) { totalCoins = value; }

    private double gameSpeed;
    public double getGameSpeed() { return gameSpeed; }
    public void setGameSpeed(double value) { gameSpeed = value; }

    private List<RunningPlane> runningPlanes;
    public List<RunningPlane> getRunningPlanes() { return runningPlanes; }
    public void setRunningPlanes(List<RunningPlane> runningPlanes) { this.runningPlanes = runningPlanes; }

}
