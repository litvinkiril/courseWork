package ru.livins.aeroboost.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ru.livins.aeroboost.R;

public class MainBoardActivity extends AppCompatActivity {

    static {
        System.loadLibrary("aeroboost-core");
    }

    private ImageView[][] cells = new ImageView[5][2];
    private boolean[][] occupied = new boolean[5][2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board_activity);

        // Находим ВСЕ 10 ячеек
        cells[0][0] = findViewById(R.id.cell_0_0);
        cells[0][1] = findViewById(R.id.cell_0_1);
        cells[1][0] = findViewById(R.id.cell_1_0);
        cells[1][1] = findViewById(R.id.cell_1_1);
        cells[2][0] = findViewById(R.id.cell_2_0);
        cells[2][1] = findViewById(R.id.cell_2_1);
        cells[3][0] = findViewById(R.id.cell_3_0);
        cells[3][1] = findViewById(R.id.cell_3_1);
        cells[4][0] = findViewById(R.id.cell_4_0);
        cells[4][1] = findViewById(R.id.cell_4_1);

        // Вешаем клики на каждую ячейку
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                final int r = row;
                final int c = col;
                cells[row][col].setOnClickListener(v -> {
                    toggleCell(r, c);
                    Toast.makeText(MainBoardActivity.this,
                            String.format("Ячейка [%d,%d] кликнута", r, c),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void toggleCell(int row, int col) {
        occupied[row][col] = !occupied[row][col];
        if (occupied[row][col]) {
            cells[row][col].setBackgroundResource(R.drawable.empty_space);
        } else {
            cells[row][col].setBackgroundResource(R.drawable.empty_space);
        }
    }
}