package ru.livins.aeroboost.model;

public class PlaneItem {
    private final int id;
    private final String name;
    private final String imageName;
    private int currentPrice;
    private int currentPurchased;
    private final int cpsPerUnit;
    private int totalCps;
    private final String  blockImageName;

    // Конструктор (уже есть)
    public PlaneItem(int id, String name, String imageName,
                     int currentPrice,
                     int currentPurchased, int cpsPerUnit, int totalCps, String blockImageName) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.currentPrice = currentPrice;
        this.currentPurchased = currentPurchased;
        this.cpsPerUnit = cpsPerUnit;
        this.totalCps = totalCps;
        this.blockImageName = blockImageName;
    }

    // === ГЕТТЕРЫ (должны быть) ===
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageName() { return imageName; }

    public String getBlockImageName() {
        return blockImageName;
    }

    public int getCurrentPrice() { return currentPrice; }
    public int getCurrentPurchased() { return currentPurchased; }
    public int getCpsPerUnit() { return cpsPerUnit; }
    public int getTotalCps() { return totalCps; }

    // === СЕТТЕРЫ (ДОБАВЬ ЭТИ МЕТОДЫ!) ===
    public void setCurrentPrice(int price) {
        this.currentPrice = price;
    }

    public void setCurrentPurchased(int purchased) {
        this.currentPurchased = purchased;
    }

    public void setTotalCps(int cps) {
        this.totalCps = cps;
    }

}