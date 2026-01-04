package ru.livins.aeroboost.view;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.view.GraphicsView;

public class MainActivity extends AppCompatActivity {

    /*@Override
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
    }*/

    static {
        System.loadLibrary("airplane");
    }

    private GridView gameGrid;
    private ru.livins.aeroboost.adapter.GameGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GridView
        gameGrid = findViewById(R.id.gameGrid);

        // Создаем адаптер (он сам создаст сетку 2x5)
        adapter = new ru.livins.aeroboost.adapter.GameGridAdapter(this);
        gameGrid.setAdapter(adapter);

        // Клик по ячейке
        gameGrid.setOnItemClickListener((parent, view, position, id) -> {
            int cols = adapter.getCols(); // 2
            int row = position / cols;
            int col = position % cols;

            // Меняем состояние
            adapter.toggleCell(row, col);

            // Показываем состояние
            boolean isOccupied = adapter.isOccupied(row, col);
            String state = isOccupied ? "ЗАНЯТА" : "СВОБОДНА";
            Toast.makeText(this,
                    String.format("Ячейка [%d,%d] теперь %s", row, col, state),
                    Toast.LENGTH_SHORT).show();
        });

    }

    public native String stringFromJNI();

}