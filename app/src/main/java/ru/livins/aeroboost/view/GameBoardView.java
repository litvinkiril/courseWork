package ru.livins.aeroboost.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.List;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.model.RunningPlane;

public class GameBoardView extends View {

    private PlaneTrace planeTrace = null;
    private PlaneBitmaps planeBitmaps;
    private List<RunningPlane> runningPlanes = new ArrayList<>();

    public GameBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameBoardView(Context context) {
        super(context);
        init(context);
    }

    private final int[] planeResIds = new int[] {
            R.drawable.plane1board, R.drawable.plane2board, R.drawable.plane3board, R.drawable.plane4, R.drawable.plane5,
            R.drawable.plane6, R.drawable.plane7, R.drawable.plane8, R.drawable.plane9, R.drawable.plane10
    };

    private void init(Context context) {
        planeBitmaps = new PlaneBitmaps(250, 250);

        for (int i = 0; i < 10; i++) {
            var d = AppCompatResources.getDrawable(context, planeResIds[i]);
            assert d != null;
            var originalBitmap = ((BitmapDrawable) d).getBitmap();

            planeBitmaps.addPlane(i, originalBitmap);
        }
    }

    public void addRunningPlane(RunningPlane runningPlane) {
        var copy = new ArrayList<>(runningPlanes);
        copy.add(runningPlane);
        runningPlanes = copy;
    }
    public void removeRunningPlane(RunningPlane runningPlane) {
        var copy = new ArrayList<>(runningPlanes);
        copy.remove(runningPlane);
        runningPlanes = copy;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Ленивая инициализация planeTrace
        if (planeTrace == null) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int traceDimension = 400; //смещение ровно на клетку + 25 пикселей + пол самолета
            planeTrace = new PlaneTrace(viewWidth, viewHeight, traceDimension);

            planeBitmaps.addRotatedBitmaps(planeTrace.getAllDirections());

            Log.d("ViewDimensions", "Width: " + viewWidth + ", Height: " + viewHeight);
        }

        if (this.runningPlanes != null) {
            var runningPlanes = new ArrayList<>(this.runningPlanes);
            for (var runningPlane : runningPlanes) {
                var planeId = runningPlane.getPlaneId();
                var planePosition = planeTrace.getPosition(runningPlane.getOdometer());
                var planeBitmap = planeBitmaps.getRotatedBitmap(planeId, planePosition.direction);
                if (planeBitmap != null) {
                    float drawX = (float) planePosition.x - (float) planeBitmap.getWidth() / 2;
                    float drawY = (float) planePosition.y - (float) planeBitmap.getHeight() / 2;
                    canvas.drawBitmap(planeBitmap, drawX, drawY, null);
                }
            }
        }
    }

}