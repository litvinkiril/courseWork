package ru.livins.aeroboost.view.drag;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    private final Context context;

    public ToastHelper(Context context) {
        this.context = context;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
}