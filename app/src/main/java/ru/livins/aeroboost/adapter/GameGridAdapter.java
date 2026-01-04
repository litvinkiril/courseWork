package ru.livins.aeroboost.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.livins.aeroboost.R;

public class GameGridAdapter extends BaseAdapter {
    private Context context;
    private int cols = 2;  // 2 колонки
    private int rows = 5;  // 5 строк
    private boolean[][] occupied;  // массив состояний [строка][колонка]

    public GameGridAdapter(Context context) {
        this.context = context;
        this.occupied = new boolean[rows][cols];

        // Инициализация - все ячейки свободны
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                occupied[row][col] = false;
            }
        }
    }

    @Override
    public int getCount() {
        return cols * rows;  // 2 * 5 = 10 ячеек
    }

    @Override
    public Object getItem(int position) {
        int row = position / cols;
        int col = position % cols;
        return occupied[row][col];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);

            // УМЕНЬШАЕМ РАЗМЕР: 80dp вместо расчета по ширине экрана
            int cellSizeInDp = 80; // желаемый размер в dp
            int cellSizeInPx = (int) (cellSizeInDp * context.getResources().getDisplayMetrics().density);

            imageView.setLayoutParams(new ViewGroup.LayoutParams(cellSizeInPx, cellSizeInPx));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);

            // Отступы внутри ячейки
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

        // Всегда empty_place
        imageView.setImageResource(R.drawable.empty_space);

        // Убираем альфа-эффект для чистоты
        imageView.setAlpha(1.0f);

        return imageView;
    }
    // Методы для управления состоянием
    public void setOccupied(int row, int col, boolean isOccupied) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            occupied[row][col] = isOccupied;
            notifyDataSetChanged();
        }
    }

    public boolean isOccupied(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return occupied[row][col];
        }
        return false;
    }

    public void toggleCell(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            occupied[row][col] = !occupied[row][col];
            notifyDataSetChanged();
        }
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
}