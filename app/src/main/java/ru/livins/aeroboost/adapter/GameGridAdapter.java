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

    private static final int[] PLANE_RESOURCES = {
            R.drawable.plane1,
            R.drawable.plane2,
            R.drawable.plane3,
            R.drawable.plane4,
            R.drawable.plane5,
            R.drawable.plane6,
            R.drawable.plane7,
            R.drawable.plane8,
            R.drawable.plane9,
            R.drawable.plane10
    };
    private int getImageResource(int position) {
        int level = grid[position / 2][position % 2];
        int absLevel = Math.abs(level); // Берем абсолютное значение
        if (absLevel > 0 && absLevel < 11) {
            return PLANE_RESOURCES[absLevel - 1];
        }
        else if (absLevel > 10) {
            return R.drawable.giftbox;
        }
        else {
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
        int prev = grid[toRow][toCol];
        grid[toRow][toCol] = grid[fromRow][fromCol];
        grid[fromRow][fromCol] = prev;
        notifyDataSetChanged();
    }
    public void throwOutPlane(int fromPosition) {
        int fromRow = fromPosition / 2;
        int fromCol = fromPosition % 2;
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
    public void setLevelOnCell(int pos, int level) {
        grid[pos / 2][pos % 2] = level;
        notifyDataSetChanged();
    }
    public void deleteAllPlane() {
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 2; ++col) {
                grid[row][col] = 0;
            }
        }
    }
    public void openGiftBox(int position) {
        grid[position / 2][position%2] -= 10;
        notifyDataSetChanged();
    }
}