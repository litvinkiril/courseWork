package ru.livins.aeroboost.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.livins.aeroboost.R;

public class GameGridAdapter extends BaseAdapter {
    private static GameGridAdapter instance;
    private final Context context;
    private final int[][] grid = new int[4][2];

    private int getImageResource(int position) {
        int level = grid[position / 2][position % 2];
        int absLevel = Math.abs(level); // Берем абсолютное значение

        switch (absLevel) {
            case 1:
                return R.drawable.plane1;
            case 2:
                return R.drawable.plane2;
            case 3:
                return R.drawable.plane3;
            case 4:
                return R.drawable.plane4;
            case 5:
                return R.drawable.plane5;
            case 6:
                return R.drawable.plane6;
            case 7:
                return R.drawable.plane7;
            case 8:
                return R.drawable.plane8;
            case 9:
                return R.drawable.plane9;
            case 10:
                return R.drawable.plane10;
            default:
                return android.R.color.transparent;
        }
    }

    public GameGridAdapter(Context context) {
        this.context = context;
        instance = this;
    }

    public static GameGridAdapter getInstance() {
        return instance;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Object getItem(int position) {
        int row = position / 2;
        int col = position % 2;
        return grid[row][col];
    }
    public int getLevelPlane(int position) {
        int row = position / 2;
        int col = position % 2;
        return grid[row][col];
    }

    public void cellClicked(int position) {
        int row = position / 2;
        int col = position % 2;
        grid[row][col] *= -1;
        notifyDataSetChanged();
    }

    public void upgradePlane(int fromPosition, int toPosition) {
        int fromRow = fromPosition / 2;
        int fromCol = fromPosition % 2;
        int toRow = toPosition / 2;
        int toCol = toPosition % 2;
        grid[fromRow][fromCol] = 0;
        grid[toRow][toCol]++;
        notifyDataSetChanged();
    }

    public void movePlane(int fromPosition, int toPosition) {
        int fromRow = fromPosition / 2;
        int fromCol = fromPosition % 2;
        int toRow = toPosition / 2;
        int toCol = toPosition % 2;
        grid[toRow][toCol] = grid[fromRow][fromCol];
        grid[fromRow][fromCol] = 0;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv;
        if (convertView == null) {
            iv = new ImageView(context);
            iv.setLayoutParams(new ViewGroup.LayoutParams(250, 250));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv = (ImageView) convertView;
        }

        iv.setBackgroundResource(R.drawable.empty_space);

        int level = grid[position / 2][position % 2];
        iv.setImageResource(getImageResource(position));

        // Если level отрицательный, делаем изображение полупрозрачным
        if (level < 0) {
            iv.setAlpha(0.5f); // 0.0 - полностью прозрачный, 1.0 - полностью непрозрачный
        } else {
            iv.setAlpha(1.0f); // Возвращаем полную непрозрачность для положительных значений
        }

        return iv;
    }


    public int foundEmptyCell() {
        for (int i = 0; i < 8; ++i) {
            if (!isOccupied(i / 2, i % 2)) {
                return i;
            }
        }
        return -1;
    }

    public int[][] getGrid() {
        return grid;
    }

    public boolean isOccupied(int row, int col) {
        return grid[row][col] != 0;
    }
}