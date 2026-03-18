package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.widget.GridView;
import ru.livins.aeroboost.adapter.GameGridAdapter;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.model.RunningPlane;
import ru.livins.aeroboost.viewmodel.MainBoardViewModel;

public class MainBoardActivity extends AppCompatActivity {

    private GameGridAdapter gridAdapter;
    private TextView userNameTextView;
    private TextView totalProfitRateTextView;
    private TextView totalCoinsTextView;
    private ImageButton btnBuyPlane;
    private ImageButton btnShop;
    private GameBoardView gameBoardView;

    private static native double countCpsPerSecond(int planeId);

    private RunningPlane[][] gridPlanes = new RunningPlane[4][2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board_activity);

        var viewModelProvider = new ViewModelProvider(this);
        var viewModel = viewModelProvider.get(MainBoardViewModel.class);

        // Статус игры.
        userNameTextView = findViewById(R.id.userName);
        totalProfitRateTextView = findViewById(R.id.totalProfitRate);
        totalCoinsTextView = findViewById(R.id.totalCoins);

        gameBoardView = findViewById(R.id.gameBoardView);

        // Элементы на форме.
        GridView gridView = findViewById(R.id.gridView);
        gridAdapter = new GameGridAdapter(this);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            int levelPlaneOnCell = gridAdapter.getLevelPlane(position);

            if (levelPlaneOnCell > 0) {
                Toast.makeText(this, "Ячейка [" + row + "," + col + "]", Toast.LENGTH_SHORT).show();

                var runningPlane = new RunningPlane();
                runningPlane.setPlaneId(levelPlaneOnCell);
                runningPlane.setOdometer(0);
                runningPlane.setSpeed(0.2 * Math.pow(1.1, levelPlaneOnCell - 1));
                gridPlanes[row][col] = runningPlane;
                viewModel.onPlaneAdded(runningPlane);
                gameBoardView.addRunningPlane(runningPlane);
                gridAdapter.cellClicked(position);

                String text = totalProfitRateTextView.getText().toString();
                double value = Double.parseDouble(text);
                double nowProfit = countCpsPerSecond(levelPlaneOnCell - 1);
                double result = value + nowProfit;
                totalProfitRateTextView.setText(String.format("%.2f", result));  // ОКРУГЛЕНИЕ ДО СОТЫХ
            }

            if (levelPlaneOnCell < 0) {
                RunningPlane planeToRemove = gridPlanes[row][col];
                viewModel.onPlaneRemoved(planeToRemove);
                gameBoardView.removeRunningPlane(planeToRemove);
                gridAdapter.cellClicked(position);
                gridPlanes[row][col] = null;

                String text = totalProfitRateTextView.getText().toString();
                double value = Double.parseDouble(text);
                double nowProfit = countCpsPerSecond(Math.abs(levelPlaneOnCell) - 1);
                double result = value - nowProfit;
                result = Math.abs(result);
                totalProfitRateTextView.setText(String.format("%.2f", result));  // ОКРУГЛЕНИЕ ДО СОТЫХ
            }
        });



        // Кнопки.
        btnBuyPlane = findViewById(R.id.btnBuyPlane);

        btnShop = findViewById(R.id.btnShop);
        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainBoardActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        // Привязываем ViewModel.
        viewModel.getUserName().observe(this, value -> {
            userNameTextView.setText(value);
        });



        viewModel.getTotalCoins().observe(this, value -> {
            var roundedValue = Math.round(value);
            totalCoinsTextView.setText(String.valueOf(roundedValue));
        });

        viewModel.getGameBoardVersion().observe(this, value -> {
            gameBoardView.invalidate();
        });
    }

}