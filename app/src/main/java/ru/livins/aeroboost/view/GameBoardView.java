package ru.livins.aeroboost.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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
    private Map<String, Bitmap> rotatedPlaneCache; // Кэш для повернутых битманов самолетов
    private List<Bitmap> traceMarkupBitmaps; // Кэш для картинок разметки
    private PlaneTrace planeTrace = null;
    private List<RunningPlane> runningPlanes = new ArrayList<>();
    private Bitmap flagBitmap;

    // Кэш для точек трассы
    private List<PlaneTrace.TracePosition> cachedTracePositions;
    private boolean tracePositionsChanged = true;

    // Настраиваемые параметры
    private int planeSize = 250;
    private int markupWidth = 150;
    private int markupHeight = 17;
    private int flagSize = 140;
    private int traceDimension = 400;

    public GameBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameBoardView(Context context) {
        super(context);
        init(context, null);
    }

    private final int[] planeResIds = new int[]{
            R.drawable.plane1board, R.drawable.plane2board, R.drawable.plane3board,
            R.drawable.plane4, R.drawable.plane5board, R.drawable.plane6board, R.drawable.plane7board,
            R.drawable.plane8board, R.drawable.plane9board, R.drawable.plane10board
    };

    // Массив картинок для разметки трассы
    private final int[] traceMarkupResIds = new int[]{
            R.drawable.markeup
    };

    private void init(Context context, AttributeSet attrs) {
        planeBitmapById = new HashMap<>();
        rotatedPlaneCache = new HashMap<>();
        traceMarkupBitmaps = new ArrayList<>();
        loadBitmaps(context);
    }



    private void loadBitmaps(Context context) {
        // Загрузка картинок самолетов
        for (int i = 0; i < planeResIds.length; i++) {
            Drawable d = AppCompatResources.getDrawable(context, planeResIds[i]);
            if (d != null) {
                Bitmap originalBitmap = ((BitmapDrawable) d).getBitmap();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        originalBitmap,
                        planeSize,
                        planeSize,
                        true
                );
                planeBitmapById.put(i, scaledBitmap);
            }
        }

        // Загрузка картинок разметки
        for (int resId : traceMarkupResIds) {
            Drawable d = AppCompatResources.getDrawable(context, resId);
            if (d != null) {
                Bitmap originalBitmap = ((BitmapDrawable) d).getBitmap();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        originalBitmap,
                        markupWidth,
                        markupHeight,
                        true
                );
                traceMarkupBitmaps.add(scaledBitmap);
            }
        }

        // Загрузка флага
        flagBitmap = getBitmapFlag();
    }

    private Bitmap getBitmapFlag() {
        int flagId = R.drawable.finish_flag;
        Drawable d = AppCompatResources.getDrawable(getContext(), flagId);
        if (d != null) {
            Bitmap originalBitmap = ((BitmapDrawable) d).getBitmap();
            return Bitmap.createScaledBitmap(
                    originalBitmap,
                    flagSize,
                    flagSize,
                    true
            );
        }
        return null;
    }

    public void addRunningPlane(RunningPlane runningPlane) {
        List<RunningPlane> copy = new ArrayList<>(runningPlanes);
        copy.add(runningPlane);
        runningPlanes = copy;
        invalidate();
    }

    public void removeRunningPlane(RunningPlane runningPlane) {
        List<RunningPlane> copy = new ArrayList<>(runningPlanes);
        copy.remove(runningPlane);
        runningPlanes = copy;
        invalidate();
    }

    public void clearRunningPlanes() {
        runningPlanes = new ArrayList<>();
        invalidate();
    }

    private Bitmap getRotatedPlaneBitmap(int planeId, double direction) {
        String key = planeId + "_" + direction;

        if (rotatedPlaneCache.containsKey(key)) {
            return rotatedPlaneCache.get(key);
        }

        Bitmap original = planeBitmapById.get(planeId);
        if (original == null) return null;

        Matrix matrix = new Matrix();
        matrix.postRotate((float) direction);

        Bitmap rotated = Bitmap.createBitmap(
                original,
                0, 0,
                original.getWidth(),
                original.getHeight(),
                matrix,
                true
        );

        rotatedPlaneCache.put(key, rotated);
        return rotated;
    }

    // Выбор картинки разметки в зависимости от одометра
    private Bitmap getTraceMarkupBitmap(double odometer, int number) {
        if (traceMarkupBitmaps.isEmpty()) {
            return null;
        }

        // Здесь можно добавить логику выбора разных картинок в зависимости от number или odometer
        return traceMarkupBitmaps.get(0);
    }

    private void drawBitmapCentered(Canvas canvas, Bitmap bitmap, PlaneTrace.TracePosition pos) {
        int count = canvas.save();
        canvas.translate((float) pos.x, (float) pos.y);
        canvas.rotate((float) pos.direction);
        canvas.drawBitmap(bitmap,
                -bitmap.getWidth() / 2f,
                -bitmap.getHeight() / 2f,
                null);
        canvas.restoreToCount(count);
    }

    private void drawTraceMarkup(Canvas canvas) {
        if (planeTrace == null || traceMarkupBitmaps.isEmpty()) return;

        if (tracePositionsChanged) {
            cachedTracePositions = planeTrace.getAllPositions();
            tracePositionsChanged = false;
        }

        if (cachedTracePositions == null || cachedTracePositions.isEmpty()) return;

        int totalPositions = cachedTracePositions.size();

        // Рисуем разметку вдоль трассы
        for (int i = 0; i < totalPositions; i += 5) {
            PlaneTrace.TracePosition pos = cachedTracePositions.get(i);
            double odometer = (double) i / totalPositions;
            Bitmap markupBitmap = getTraceMarkupBitmap(odometer, 0);
            if (markupBitmap != null) {
                drawBitmapCentered(canvas, markupBitmap, pos);
            }
        }

        // Рисуем флаг в конце трассы
        if (flagBitmap != null && !flagBitmap.isRecycled()) {
            PlaneTrace.TracePosition lastPos = cachedTracePositions.get(totalPositions - 1);
            drawBitmapCentered(canvas, flagBitmap, lastPos);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (planeTrace == null) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            if (viewWidth > 0 && viewHeight > 0) {
                planeTrace = new PlaneTrace(viewWidth, viewHeight, traceDimension);
                tracePositionsChanged = true;
                Log.d("GameBoardView", "Trace created: width=" + viewWidth +
                        ", height=" + viewHeight + ", dimension=" + traceDimension);
            } else {
                return;
            }
        }

        drawTraceMarkup(canvas);

        // Рисуем самолеты
        if (!runningPlanes.isEmpty()) {
            List<RunningPlane> planesCopy = new ArrayList<>(runningPlanes);
            for (RunningPlane runningPlane : planesCopy) {
                PlaneTrace.TracePosition planePosition = planeTrace.getPosition(runningPlane.getOdometer());

                if (planePosition == null) continue;

                Bitmap rotatedBitmap = getRotatedPlaneBitmap(
                        runningPlane.getPlaneId(),
                        planePosition.direction
                );

                if (rotatedBitmap != null && !rotatedBitmap.isRecycled()) {
                    float drawX = (float) planePosition.x - (float) rotatedBitmap.getWidth() / 2;
                    float drawY = (float) planePosition.y - (float) rotatedBitmap.getHeight() / 2;
                    canvas.drawBitmap(rotatedBitmap, drawX, drawY, null);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            planeTrace = null;
            tracePositionsChanged = true;
            Log.d("GameBoardView", "Size changed: " + w + "x" + h);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearResources();
    }

    private void clearResources() {
        // Очищаем кэш повернутых битманов
        if (rotatedPlaneCache != null) {
            for (Bitmap bitmap : rotatedPlaneCache.values()) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            rotatedPlaneCache.clear();
        }

        // Очищаем флаг
        if (flagBitmap != null && !flagBitmap.isRecycled()) {
            flagBitmap.recycle();
            flagBitmap = null;
        }

        // Очищаем битманы разметки
        if (traceMarkupBitmaps != null) {
            for (Bitmap bitmap : traceMarkupBitmaps) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            traceMarkupBitmaps.clear();
        }

        // Очищаем битманы самолетов
        if (planeBitmapById != null) {
            for (Bitmap bitmap : planeBitmapById.values()) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            planeBitmapById.clear();
        }
    }

    // Публичные методы для управления трассой
    public void resetTrace() {
        planeTrace = null;
        tracePositionsChanged = true;
        invalidate();
    }

    public void setTraceDimension(int dimension) {
        if (traceDimension != dimension) {
            traceDimension = dimension;
            resetTrace();
        }
    }

    public int getTraceDimension() {
        return traceDimension;
    }

    // Публичные методы для получения информации
    public PlaneTrace.TracePosition getPlanePosition(double odometer) {
        if (planeTrace != null) {
            return planeTrace.getPosition(odometer);
        }
        return null;
    }

    public boolean isTraceInitialized() {
        return planeTrace != null;
    }
}