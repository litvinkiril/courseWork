package ru.livins.aeroboost.model;

import ru.livins.aeroboost.R;

public class GameCell {
    private int id;
    private int row;
    private int col;
    private int imageResId;      // ID ресурса изображения
    private boolean isOccupied;  // занята ли ячейка
    private String type;         // тип содержимого

    // Конструктор
    public GameCell(int id, int row, int col) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.imageResId = 0; // изображение по умолчанию
        this.isOccupied = false; // по умолчанию свободна
        this.type = "empty";     // по умолчанию пустая
    }

    // --- Геттеры (получить значения) ---
    public int getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public String getType() {
        return type;
    }

    // --- Сеттеры (установить значения) ---

    // Занять ячейку
    public void setOccupied(boolean occupied, int imageResId, String type) {
        this.isOccupied = occupied;
        this.imageResId = imageResId;
        this.type = type;
    }

    // Освободить ячейку (сделать пустой)
    public void setEmpty() {
        this.isOccupied = false;
        this.imageResId = 0;
        this.type = "empty";
    }

    // Установить изображение
    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    // Установить тип
    public void setType(String type) {
        this.type = type;
    }

    // Проверка, пустая ли ячейка
    public boolean isEmpty() {
        return !isOccupied && "empty".equals(type);
    }

    // Для отладки
    @Override
    public String toString() {
        return String.format("Cell[%d,%d] %s (%s)",
                row, col,
                isOccupied ? "OCCUPIED" : "EMPTY",
                type);
    }
}