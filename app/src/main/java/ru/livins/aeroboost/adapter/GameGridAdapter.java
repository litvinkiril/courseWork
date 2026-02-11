package ru.livins.aeroboost.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.livins.aeroboost.R;

public class GameGridAdapter extends BaseAdapter {
    private static GameGridAdapter instance;
    private Context context;
    private boolean[][] grid = new boolean[5][2];

    public GameGridAdapter(Context context) {
        this.context = context;
        instance = this;
    }

    public static GameGridAdapter getInstance() {
        return instance;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        int row = position / 2;
        int col = position % 2;
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
            iv.setLayoutParams(new ViewGroup.LayoutParams(250, 250));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv = (ImageView) convertView;
        }

        int row = position / 2;
        int col = position % 2;

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

    public int foundEmptyCell() {
        for (int i = 0; i < 10; ++i) {
            if (!isOccupied(i / 2, i % 2)) {
                return i;
            }
        }
        return -1;
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public boolean isOccupied(int row, int col) {
        return grid[row][col];
    }
}