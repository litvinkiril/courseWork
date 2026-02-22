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

    // Элементы на форме.
    private GridView gridView;
    private GameGridAdapter gridAdapter;
    private TextView userNameTextView;
    private TextView totalProfitRateTextView;
    private TextView totalCoinsTextView;
    private ImageButton btnBuyPlane;
    private ImageButton btnShop;
    private GameBoardView gameBoardView;

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

        gridView = findViewById(R.id.gridView);
        gridAdapter = new GameGridAdapter(this);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            gridAdapter.toggleCell(row, col);
            Toast.makeText(this, "Ячейка [" + row + "," + col + "]", Toast.LENGTH_SHORT).show();

            //var item = gridAdapter.getItem(position);
            var runningPlane = new RunningPlane();
            runningPlane.setPlaneId(1);
            runningPlane.setOdometer(0);
            runningPlane.setSpeed(0.2);
            viewModel.onPlaneAdded(runningPlane);
            gameBoardView.addRunningPlane(runningPlane);
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

        viewModel.getGameSpeed().observe(this, value -> {
            var roundedValue = Math.round(value);
            totalProfitRateTextView.setText(String.valueOf(roundedValue));
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