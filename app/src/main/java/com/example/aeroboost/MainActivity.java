package com.example.aeroboost;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aeroboost.view.GraphicsView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onButtonShow(View view) {
        GraphicsView planesView = this.findViewById(R.id.planesView);
        planesView.showPlane();
    }

    public void onButtonMove(View view) {
        GraphicsView planesView = this.findViewById(R.id.planesView);
        var planePosition = planesView.getPlanePosition();
        planesView.setPlanePosition(planePosition + 0.05f);
    }
}