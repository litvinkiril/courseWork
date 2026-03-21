package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.view.View;
import ru.livins.aeroboost.R;
import ru.livins.aeroboost.adapter.GameGridAdapter;
import ru.livins.aeroboost.model.RunningPlane;
import ru.livins.aeroboost.view.drag.DragHelper;
import ru.livins.aeroboost.view.drag.RubbishHandler;
import ru.livins.aeroboost.view.drag.ToastHelper;
import ru.livins.aeroboost.viewmodel.MainBoardViewModel;

public class MainBoardActivity extends AppCompatActivity {

    // UI Components
    private TextView userNameTextView;
    private TextView totalProfitRateTextView;
    private TextView totalCoinsTextView;
    private GameBoardView gameBoardView;
    private GridView gridView;
    private ImageView rubbish;
    private ImageButton btnBuyPlane;
    private ImageButton btnShop;

    // Adapters and Models
    private GameGridAdapter gridAdapter;
    private RunningPlane[][] gridPlanes = new RunningPlane[4][2];
    private MainBoardViewModel viewModel;

    // Drag and Drop Helpers
    private DragHelper dragHelper;
    private RubbishHandler rubbishHandler;
    private ToastHelper toastHelper;

    private static native double countCpsPerSecond(int planeId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board_activity);

        initViews();
        initViewModel();
        initDragAndDrop();
        setupClickListeners();
        setupViewModelObservers();
    }

    private void initViews() {
        // Инициализация всех View
        userNameTextView = findViewById(R.id.userName);
        totalProfitRateTextView = findViewById(R.id.totalProfitRate);
        totalCoinsTextView = findViewById(R.id.totalCoins);
        gameBoardView = findViewById(R.id.gameBoardView);
        gridView = findViewById(R.id.gridView);
        rubbish = findViewById(R.id.rubbishImage);
        btnBuyPlane = findViewById(R.id.btnBuyPlane);
        btnShop = findViewById(R.id.btnShop);

        // Настройка адаптера
        gridAdapter = new GameGridAdapter(this);
        gridView.setAdapter(gridAdapter);

        // Инициализация помощников
        toastHelper = new ToastHelper(this);
        rubbishHandler = new RubbishHandler(rubbish, gridView);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainBoardViewModel.class);
    }

    private void initDragAndDrop() {
        dragHelper = new DragHelper(
                gridView,
                gridAdapter,
                gridPlanes,
                viewModel,
                gameBoardView,
                rubbishHandler,
                toastHelper
        );

        gridView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    showRubbish();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    dragHelper.updateHighlight(event);
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    dragHelper.updateHighlight(event);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    dragHelper.clearHighlights();
                    return true;
                case DragEvent.ACTION_DROP:
                    dragHelper.clearHighlights();
                    dragHelper.handleDrop(event);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    dragHelper.clearHighlights();
                    hideRubbish();
                    return true;
            }
            return false;
        });
        rubbish.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:

                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    rubbishHandler.highlight(true);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    rubbishHandler.highlight(false);
                    return true;

                case DragEvent.ACTION_DROP:
                    toastHelper.showToast("Самолет удален!");

                    ClipData clipData = event.getClipData();
                    if (clipData != null) {
                        String data = clipData.getItemAt(0).getText().toString();
                        String[] parts = data.split(",");
                        int startRow = Integer.parseInt(parts[0]);
                        int startCol = Integer.parseInt(parts[1]);
                        gridAdapter.throwOutPlane(startRow * 2 + startCol);
                    }

                    rubbishHandler.highlight(false);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    rubbishHandler.highlight(false);
                    return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        // Короткое нажатие на ячейку
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            int levelPlaneOnCell = gridAdapter.getLevelPlane(position);

            if (levelPlaneOnCell > 0) {
                // Добавление самолета
                Toast.makeText(this, "Ячейка [" + row + "," + col + "]", Toast.LENGTH_SHORT).show();

                var runningPlane = new RunningPlane();
                runningPlane.setPlaneId(levelPlaneOnCell);
                runningPlane.setOdometer(0);
                runningPlane.setSpeed(0.2 * Math.pow(1.1, levelPlaneOnCell - 1));
                gridPlanes[row][col] = runningPlane;
                viewModel.onPlaneAdded(runningPlane);
                gameBoardView.addRunningPlane(runningPlane);
                gridAdapter.cellClicked(position);

                updateProfit(levelPlaneOnCell, true);
            }

            if (levelPlaneOnCell < 0) {
                // Удаление самолета
                RunningPlane planeToRemove = gridPlanes[row][col];
                if (planeToRemove != null) {
                    viewModel.onPlaneRemoved(planeToRemove);
                    gameBoardView.removeRunningPlane(planeToRemove);
                    gridAdapter.cellClicked(position);
                    gridPlanes[row][col] = null;

                    updateProfit(Math.abs(levelPlaneOnCell), false);
                }
            }
        });

        // Долгое нажатие на ячейку
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            int levelPlaneOnCell = gridAdapter.getLevelPlane(position);

            if (levelPlaneOnCell > 0) {
                dragHelper.startDraggableImage(row, col, levelPlaneOnCell, this);
                return true;
            }
            return false;
        });

        // Кнопка магазина
        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainBoardActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        // Кнопка покупки самолета
        btnBuyPlane.setOnClickListener(v -> {
            // TODO: Реализовать покупку самолета
            Toast.makeText(this, "Купить самолет", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupViewModelObservers() {
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

    private void updateProfit(int level, boolean isAdding) {
        String text = totalProfitRateTextView.getText().toString();
        double value = Double.parseDouble(text);
        double nowProfit = countCpsPerSecond(level - 1);

        double result;
        if (isAdding) {
            result = value + nowProfit;
        } else {
            result = Math.abs(value - nowProfit);
        }

        totalProfitRateTextView.setText(String.format("%.2f", result));
    }

    // Добавьте эти методы в MainBoardActivity

    private void showRubbish() {
        if (rubbish != null && rubbish.getVisibility() != View.VISIBLE) {
            rubbish.setVisibility(View.VISIBLE);
            rubbish.setAlpha(0f);
            rubbish.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
        }
    }

    private void hideRubbish() {
        if (rubbish != null && rubbish.getVisibility() == View.VISIBLE) {
            rubbish.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        rubbish.setVisibility(View.GONE);
                        rubbishHandler.highlight(false); // Сбрасываем подсветку
                    })
                    .start();
        }
    }
}