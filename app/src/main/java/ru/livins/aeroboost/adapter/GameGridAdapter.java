package ru.livins.aeroboost.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.livins.aeroboost.R;

public class GameGridAdapter extends BaseAdapter {
    private Context context;
    private boolean[][] grid = new boolean[5][2]; // 5 строк, 2 колонки

    public GameGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10; // 5×2 = 10 ячеек
    }

    @Override
    public Object getItem(int position) {
        int row = position / 2; // делим на 2 колонки
        int col = position % 2; // остаток от деления
        return grid[row][col];
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
            iv.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv = (ImageView) convertView;
        }

        int row = position / 2;
        int col = position % 2;

        // Красный если занято, зеленый если свободно
        if (grid[row][col]) {
            iv.setImageResource(R.drawable.empty_space);
        } else {
            iv.setImageResource(R.drawable.empty_space);
        }

        return iv;
    }

    public void toggleCell(int row, int col) {
        grid[row][col] = !grid[row][col];
        notifyDataSetChanged();
    }

    public boolean isOccupied(int row, int col) {
        return grid[row][col];
    }

    public int getCols() {
        return 2; // всегда 2 колонки
    }
}