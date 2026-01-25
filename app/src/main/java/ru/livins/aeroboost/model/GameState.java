package ru.livins.aeroboost.model;

import java.io.Serializable;

public class GameState implements Serializable {

    private String userName;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String value) { userName = value; }

    private double totalCoins;
    public double getTotalCoins() { return totalCoins; }
    public void setTotalCoins(double value) { totalCoins = value; }

    private double totalProfitRate;
    public double getTotalProfitRate() { return totalProfitRate; }
    public void setTotalProfitRate(double value) { totalProfitRate = value; }

}
