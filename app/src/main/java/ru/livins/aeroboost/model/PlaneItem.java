package ru.livins.aeroboost.model;

public class PlaneItem {
    private int id;
    private String name;
    private String imageName;
    private int currentPrice;
    private int currentPurchased;
    private int cpsPerUnit;
    private int totalCps;

    // Конструктор (уже есть)
    public PlaneItem(int id, String name, String imageName,
                     int currentPrice,
                     int currentPurchased, int cpsPerUnit, int totalCps) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.currentPrice = currentPrice;
        this.currentPurchased = currentPurchased;
        this.cpsPerUnit = cpsPerUnit;
        this.totalCps = totalCps;
    }

    // === ГЕТТЕРЫ (должны быть) ===
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageName() { return imageName; }
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

    // Другие методы
    public boolean canBuyMore() {
        return true;
    }


    public int getImageResId(android.content.Context context) {
        return context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName()
        );
    }
}