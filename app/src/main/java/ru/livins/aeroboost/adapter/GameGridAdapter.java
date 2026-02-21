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
    private int[][] grid = new int[5][2];

    private int getImageResource(int position) {
        int level = grid[position / 2][position % 2];

        switch (level) {
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
        iv.setBackgroundResource(R.drawable.empty_space);
        iv.setImageResource(getImageResource(position));
        return iv;
    }

    public void toggleCell(int row, int col) {

    }

    public int foundEmptyCell() {
        for (int i = 0; i < 10; ++i) {
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