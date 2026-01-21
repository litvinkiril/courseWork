package ru.livins.aeroboost.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import ru.livins.aeroboost.R;

public class GameBoardView extends View {

    private Bitmap planeBitmap0;
    private Bitmap planeBitmap90;
    private boolean planeVisible = false;
    private float planePosition = 0.0f;

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

    private void init(Context context) {
        Drawable d = AppCompatResources.getDrawable(context, R.drawable.airplane001);
        if (d != null) {
            // Исходный вариант
            planeBitmap0 = ((BitmapDrawable) d).getBitmap();

            // Повернутый вариант 90
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            planeBitmap90 = Bitmap.createBitmap(
                    planeBitmap0,
                    0, 0,
                    planeBitmap0.getWidth(),
                    planeBitmap0.getHeight(),
                    matrix,
                    true
            );
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {  // Используем @NonNull вместо @NotNull
        super.onDraw(canvas);

        // Нарисовать самолет
        if (planeVisible && planeBitmap90 != null) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();

            int planeMargin = 8;
            int planeWidth = planeBitmap90.getWidth();
            int planeHeight = planeBitmap90.getHeight();

            float planeX = viewWidth - planeWidth - planeMargin;
            float yOffset = (viewHeight - 2 * planeMargin - planeHeight) * planePosition;
            float planeY = viewHeight - planeMargin - yOffset - planeHeight;

            canvas.drawBitmap(planeBitmap90, planeX, planeY, null);
        }
    }

    public void showPlane() {
        planeVisible = !planeVisible;
        invalidate();
    }

    public float getPlanePosition() {
        return planePosition;
    }

    public void setPlanePosition(float planePosition) {
        if (planePosition < 0.0f)
            planePosition = 0.0f;
        if (planePosition > 1.0f)
            planePosition = 1.0f;
        this.planePosition = planePosition;
        invalidate();
    }
}