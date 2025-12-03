package com.example.aeroboost.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.aeroboost.R;

import org.jetbrains.annotations.NotNull;

public class GraphicsView extends View {

    private Bitmap planeBitmap;
    private boolean planeVisible = false;
    private float planePosition = 0.0f;

    public GraphicsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public GraphicsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public GraphicsView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context)
    {
        Drawable d = AppCompatResources.getDrawable(context, R.drawable.airplane001);
        if (d != null) {
            planeBitmap = ((BitmapDrawable)d).getBitmap();
        }
    }

    @Override
    protected void onDraw(@NotNull Canvas canvas) {
        super.onDraw(canvas);

        // Нарисовать самолет
        if (planeVisible) {
            var viewWidth = getWidth();
            var viewHeigth = getHeight();

            var planeMargin = 8;
            var planeWidth = planeBitmap.getWidth();
            var planeHeigth = planeBitmap.getHeight();

            var planeX = viewWidth - planeWidth - planeMargin;
            var yOffset = (viewHeigth - 2*planeMargin - planeHeigth) * planePosition;
            var planeY = viewHeigth - planeMargin - yOffset - planeHeigth;

            canvas.drawBitmap(planeBitmap, planeX, planeY, null);
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
