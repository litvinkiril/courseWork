package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ru.livins.aeroboost.R;

public class MainBoardActivity extends AppCompatActivity {

    static {
        System.loadLibrary("aeroboost-core");
    }

    private final ImageView[][] cells = new ImageView[5][2];
    private final boolean[][] occupied = new boolean[5][2];
    private ImageButton btnBuyPlane;

    private ImageButton btnShop;

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
        btnBuyPlane = findViewById(R.id.btnBuyPlane);
        btnShop = findViewById(R.id.btnShop);
        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainBoardActivity.this, ShopActivity.class);
            startActivity(intent);
        });
    }


    private void toggleCell(int row, int col) {
        occupied[row][col] = !occupied[row][col];
        if (row == 0 && col == 0) {
            onButtonShow();
        }
        else if (row == 0 && col == 1) {
            onButtonMove();
        }
        else if (occupied[row][col]) {
            cells[row][col].setBackgroundResource(R.drawable.empty_space);
        } else {
            cells[row][col].setBackgroundResource(R.drawable.empty_space);
        }
    }

    private void onButtonShow() {
        GameBoardView planesView = this.findViewById(R.id.gameBoardView);
        planesView.showPlane();
    }

    private void onButtonMove() {
        GameBoardView planesView = this.findViewById(R.id.gameBoardView);
        var planePosition = planesView.getPlanePosition();
        planesView.setPlanePosition(planePosition + 0.05f);
    }
}