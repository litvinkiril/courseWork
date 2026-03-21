package ru.livins.aeroboost.view.drag;

import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import ru.livins.aeroboost.R;

public class RubbishHandler {

    private final ImageView rubbish;
    private final GridView gridView;

    public RubbishHandler(ImageView rubbish, GridView gridView) {
        this.rubbish = rubbish;
        this.gridView = gridView;
    }

    public boolean isOverRubbish(float x, float y) {
        if (rubbish == null) return false;

        int[] rubbishLocation = new int[2];
        rubbish.getLocationOnScreen(rubbishLocation);

        int[] gridLocation = new int[2];
        gridView.getLocationOnScreen(gridLocation);

        float rubbishLeft = rubbishLocation[0] - gridLocation[0];
        float rubbishTop = rubbishLocation[1] - gridLocation[1];
        float rubbishRight = rubbishLeft + rubbish.getWidth();
        float rubbishBottom = rubbishTop + rubbish.getHeight();

        return (x >= rubbishLeft && x <= rubbishRight &&
                y >= rubbishTop && y <= rubbishBottom);
    }

    public void highlight(boolean highlight) {
        if (rubbish != null) {
            if (highlight) {
                rubbish.setImageResource(R.drawable.rubbishopen);
                rubbish.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            rubbish.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(100)
                                    .start();
                        })
                        .start();
            } else {
                rubbish.setImageResource(R.drawable.rubbishclose);
                rubbish.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start();
            }
        }
    }
}