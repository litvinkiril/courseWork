package ru.livins.aeroboost.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.HashMap;
import java.util.Map;

public class PlaneBitmaps {

    private static class PlaneBitmapsRotated {
        public Bitmap sourceBitmap;
        public Map<Double, Bitmap> rotatedBitmapByDirection = new HashMap<>();
    }

    private Map<Integer, PlaneBitmapsRotated> planeBitmapsById;
    private int planeWidth;
    private int planeHeight;

    public PlaneBitmaps(int planeWidth, int planeHeight)  {
        planeBitmapsById = new HashMap<>();
        this.planeWidth = planeWidth;
        this.planeHeight = planeHeight;
    }

    public void addPlane(int planeId, Bitmap originalBitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                planeWidth,
                planeHeight,
                true  // фильтр для сглаживания
        );
        var planeBitmapsRotated = new PlaneBitmapsRotated();
        planeBitmapsRotated.sourceBitmap = scaledBitmap;
        planeBitmapsById.put(planeId, planeBitmapsRotated);
    }

    public void addRotatedBitmaps(Iterable<Double> directions) {
        for (var planeBitmapsRotated : planeBitmapsById.values()) {
            for (var direction : directions) {
                var rotatedBitmap = rotateBitmap(planeBitmapsRotated.sourceBitmap, direction);
                planeBitmapsRotated.rotatedBitmapByDirection.put(direction, rotatedBitmap);
            }
        }
    }

    public Bitmap getRotatedBitmap(int planeId, double direction) {
        var planeBitmapsRotated = planeBitmapsById.get(planeId);
        if (planeBitmapsRotated == null)
            return null;
        return planeBitmapsRotated.rotatedBitmapByDirection.get(direction);
    }

    private static Bitmap rotateBitmap(Bitmap sourceBitmap, double angle) {
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

}
