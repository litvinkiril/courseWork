package ru.livins.aeroboost.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.model.RunningPlane;

public class GameBoardView extends View {

    private Map<Integer, Bitmap> planeBitmapById;
    private PlaneTrace planeTrace = null;
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
        // Загрузить изображения самолетов с фиксированным размером.
        planeBitmapById = new HashMap<>();

        // ФИКСИРОВАННЫЙ РАЗМЕР - ПОМЕНЯЙ НА СВОЙ!
        int fixedWidth = 250;   // Ширина 50 пикселей
        int fixedHeight = 250;  // Высота 50 пикселей

        for (int i = 0; i < 10; i++) {
            var d = AppCompatResources.getDrawable(context, planeResIds[i]);
            assert d != null;
            var originalBitmap = ((BitmapDrawable) d).getBitmap();

            // Масштабируем до фиксированного размера
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                    originalBitmap,
                    fixedWidth,
                    fixedHeight,
                    true  // фильтр для сглаживания
            );

            planeBitmapById.put(i, scaledBitmap);

            // Если оригинал больше не нужен - можно освободить память
            // (осторожно, если оригинал используется где-то еще!)
            // originalBitmap.recycle();
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

    private Bitmap rotateBitmap(Bitmap sourceBitmap, double angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)angle);
        return Bitmap.createBitmap(
                sourceBitmap,
                0, 0,
                sourceBitmap.getWidth(),
                sourceBitmap.getHeight(),
                matrix,
                true
        );
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (planeTrace == null) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int traceDimension = 400; //смещение ровно на клетку + 25 пикселей + пол самолета
            planeTrace = new PlaneTrace(viewWidth, viewHeight, traceDimension);

            Log.d("ViewDimensions", "Width: " + viewWidth + ", Height: " + viewHeight);
        }

        if (this.runningPlanes != null) {
            var runningPlanes = new ArrayList<>(this.runningPlanes);
            for (var runningPlane : runningPlanes) {
                var planeBitmap = planeBitmapById.get(runningPlane.getPlaneId());
                if (planeBitmap != null) {
                    var planePosition = planeTrace.getPosition(runningPlane.getOdometer());
                    var rotatePlaneBitmap = rotateBitmap(planeBitmap, planePosition.direction);

                    float drawX = (float) planePosition.x - (float) rotatePlaneBitmap.getWidth() / 2;
                    float drawY = (float) planePosition.y - (float) rotatePlaneBitmap.getHeight() / 2;
                    //circlePlane(runningPlane.getPlaneId());
                    canvas.drawBitmap(rotatePlaneBitmap, drawX, drawY, null);
                }
            }
        }
    }

}