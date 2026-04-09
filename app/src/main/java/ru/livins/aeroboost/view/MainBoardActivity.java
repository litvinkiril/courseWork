package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.view.View;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.adapter.GameGridAdapter;
import ru.livins.aeroboost.model.GameModel;
import ru.livins.aeroboost.model.RunningPlane;
import ru.livins.aeroboost.view.drag.DragHelper;
import ru.livins.aeroboost.view.drag.RubbishHandler;
import ru.livins.aeroboost.view.drag.ToastHelper;
import ru.livins.aeroboost.viewmodel.MainBoardViewModel;

public class MainBoardActivity extends AppCompatActivity {

    private static native double countCpsPerSecond(int planeId);

    private static native int getPlaneLevel();
    private static native int getPlaneCost(int planeId);


    // UI Components
    private TextView userNameTextView;
    private TextView totalProfitRateTextView;
    private TextView totalCoinsTextView;
    private GameBoardView gameBoardView;
    private GridView gridView;
    private ImageView rubbish;
    private LinearLayout btnBuyPlane;
    private ImageView  buyPlaneImageButton;
    private TextView buyPlanePrice;
    private TextView buyPlaneLevel;
    private ImageButton btnShop;
    private ImageButton btnSettings;

    // Models
    private GameModel gameModel;
    private GameGridAdapter gridAdapter;
    private RunningPlane[][] gridPlanes = new RunningPlane[4][2];
    private MainBoardViewModel viewModel;

    // Helpers
    private DragHelper dragHelper;
    private RubbishHandler rubbishHandler;
    private ToastHelper toastHelper;
    private char[] letters = new char[]{'k', 'm'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board_activity);

        gameModel = GameModel.getInstance();
        gameModel.setBuyPlaneListener(() -> {
            runOnUiThread(() -> setImageBuyBtn());
        });

        initViews();
        initViewModel();
        initDragAndDrop();
        setupClickListeners();
        setupViewModelObservers();
        try {
            setImageBuyBtn();
        } catch (Exception e) {
            setDefaultBuyButton();
        }

    }

    private void setImageBuyBtn() {
        int planeLevel = getPlaneLevel();
        int planeId = planeLevel - 1;
        int cost = getPlaneCost(planeId);
        String level = "lvl. " + planeLevel;
        String imageName = "plane" + planeLevel;
        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        if (imageResId != 0) {
            buyPlaneImageButton.setImageResource(imageResId);
        } else {
            buyPlaneImageButton.setImageResource(R.drawable.plane2);
        }

        buyPlanePrice.setText(String.valueOf(cost));
        buyPlaneLevel.setText(level);
        btnBuyPlane.setAlpha(1.0f);
        btnBuyPlane.setClickable(true);
    }

    private void initViews() {
        userNameTextView = findViewById(R.id.userName);
        totalProfitRateTextView = findViewById(R.id.totalProfitRate);
        totalCoinsTextView = findViewById(R.id.totalCoins);
        gameBoardView = findViewById(R.id.gameBoardView);
        gridView = findViewById(R.id.gridView);
        rubbish = findViewById(R.id.rubbishImage);
        btnBuyPlane = findViewById(R.id.btnBuyPlane);
        buyPlaneImageButton = findViewById(R.id.buyPlaneImageButton);
        buyPlaneLevel = findViewById(R.id.buyPlaneLevel);
        buyPlanePrice = findViewById(R.id.buyPlanePrice);
        btnShop = findViewById(R.id.btnShop);
        btnSettings = findViewById(R.id.btnSettings);

        gridAdapter = new GameGridAdapter(this);
        gridView.setAdapter(gridAdapter);

        toastHelper = new ToastHelper(this);
        rubbishHandler = new RubbishHandler(rubbish, gridView);
    }

    private void setDefaultBuyButton() {
        buyPlaneImageButton.setImageResource(R.drawable.plane1);
        buyPlanePrice.setText("100");
        buyPlaneLevel.setText("lvl 1");  // Уровень по умолчанию
        btnBuyPlane.setAlpha(1.0f);
        btnBuyPlane.setClickable(true);
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
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            int levelPlaneOnCell = gridAdapter.getLevelPlane(position);

            if (levelPlaneOnCell > 0) {
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

        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainBoardActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        btnBuyPlane.setOnClickListener(v -> {
            GameGridAdapter gameGrid = GameGridAdapter.getInstance();
            int[][] myGrid = gameGrid.getGrid();
            int planeLevel = getPlaneLevel();
            int planeId = planeLevel - 1;
            int emptyCell = gameGrid.foundEmptyCell();
            if (emptyCell == -1) {
                Toast.makeText(this, "Доска заполнена!", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = gameModel.buyPlane(planeId);

            if (success) {
                Toast.makeText(this, "Куплено!", Toast.LENGTH_SHORT).show();
                myGrid[emptyCell / 2][emptyCell % 2] = planeId + 1;
                gameGrid.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainBoardActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }

    private void setupViewModelObservers() {
        viewModel.getUserName().observe(this, value -> {
            userNameTextView.setText(value);
        });

        viewModel.getTotalCoins().observe(this, value -> {
            var roundedValue = Math.round(value);
            int symbol = -1;
            if (value >= 10000000) {
                roundedValue /= 1000000;
                symbol = 1;
            } else if (value >= 10000) {
                roundedValue /= 1000;
                symbol = 0;
            }
            String need = String.valueOf(roundedValue);
            if (symbol != -1) {
                need += letters[symbol];
            }
            totalCoinsTextView.setText(need);
        });

        viewModel.getGameBoardVersion().observe(this, value -> {
            gameBoardView.invalidate();
        });
    }

    private void updateProfit(int level, boolean isAdding) {
        String text = totalProfitRateTextView.getText().toString();
        double value = 0.0;
        if (text != null && !text.isEmpty()) {
            try {
                String normalized = text.replace(',', '.');
                value = Double.parseDouble(normalized);
            } catch (NumberFormatException e) {
                value = 0.0;
            }
        }

        try {
            double nowProfit = countCpsPerSecond(level - 1);
            double result = isAdding ? value + nowProfit : Math.abs(value - nowProfit);
            totalProfitRateTextView.setText(String.format("%.2f", result));
        } catch (Throwable t) {
            Log.e("MainBoard", "Error in countCpsPerSecond", t);
        }
    }

    private void showRubbish() {
        if (rubbish != null && rubbish.getVisibility() != View.VISIBLE) {
            rubbish.setVisibility(View.VISIBLE);
            rubbish.setAlpha(0f);
            rubbish.animate().alpha(1f).setDuration(200).start();
        }
    }

    private void hideRubbish() {
        if (rubbish != null && rubbish.getVisibility() == View.VISIBLE) {
            rubbish.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                rubbish.setVisibility(View.GONE);
                rubbishHandler.highlight(false);
            }).start();
        }
    }
}