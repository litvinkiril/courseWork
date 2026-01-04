package ru.livins.aeroboost.viewmodel;

import java.io.Serializable;

public class StartGameData implements Serializable {
    private String userName;

    public StartGameData(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
