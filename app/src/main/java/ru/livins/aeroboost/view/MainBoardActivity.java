package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.content.ClipData;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.adapter.GameGridAdapter;
import ru.livins.aeroboost.model.GameModel;
import ru.livins.aeroboost.model.RunningPlane;
import ru.livins.aeroboost.view.drag.DragHelper;
import ru.livins.aeroboost.view.drag.RubbishHandler;
import ru.livins.aeroboost.view.drag.ToastHelper;
import ru.livins.aeroboost.viewmodel.MainBoardViewModel;

public class MainBoardActivity extends AppCompatActivity {

    private static MainBoardActivity mainBoardinstance;
    SaveManager saveManager;

    // ==================== NATIVE METHODS ====================
    private static native double countCpsPerSecond(int planeId);
    private static native int getPlaneLevel();
    private static native int getPlaneCost(int planeId);
    private static native int clearCurPurchased();
    private static native void restorePurchasedCounts(int[] array);
    private static native boolean isOpen(int level);
    private static native void clearOpenedPlanes();
    private static native void restoreOpenedPlanes(boolean[] isOpened);


    // ==================== CONSTANTS ====================
    private static final char[] LETTERS = new char[]{'K', 'M', 'B', 't'};
    private static final int GRID_ROWS = 4;
    private static final int GRID_COLS = 2;

    // ==================== UI COMPONENTS ====================
    private TextView userNameTextView;
    private TextView totalProfitRateTextView;
    private TextView totalCoinsTextView;
    private GameBoardView gameBoardView;
    private GridView gridView;
    private ImageView rubbish;
    private LinearLayout btnBuyPlane;
    private ImageView buyPlaneImageButton;
    private TextView buyPlanePrice;
    private TextView buyPlaneLevel;
    private ImageButton btnShop;
    private ImageButton btnSettings;

    // ==================== MODELS & ADAPTERS ====================
    private GameModel gameModel;
    private GameGridAdapter gridAdapter;
    private RunningPlane[][] gridPlanes = new RunningPlane[GRID_ROWS][GRID_COLS];
    private MainBoardViewModel viewModel;

    // ==================== HELPERS ====================
    private DragHelper dragHelper;
    private RubbishHandler rubbishHandler;
    private ToastHelper toastHelper;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBoardinstance = this;
        saveManager = new SaveManager(this);

        setContentView(R.layout.main_board_activity);
        initializeGameModel();
        initializeViews();
        initializeViewModel();
        initializeHelpers();
        initializeDragAndDrop();
        setupClickListeners();
        setupViewModelObservers();
        initializeBuyButton();

        // Загружаем после всей инициализации
        gridView.post(() -> loadGameState());
    }

    // ==================== INITIALIZATION ====================

    private void initializeGameModel() {
        gameModel = GameModel.getInstance();
        gameModel.setBuyPlaneListener(() -> runOnUiThread(this::setImageBuyBtn));
    }

    private void initializeViews() {
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
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainBoardViewModel.class);
    }

    private void initializeHelpers() {
        toastHelper = new ToastHelper(this);
        rubbishHandler = new RubbishHandler(rubbish, gridView);
    }

    private void initializeBuyButton() {
        try {
            setImageBuyBtn();
        } catch (Exception e) {
            setDefaultBuyButton();
        }
    }

    // ==================== BUY BUTTON MANAGEMENT ====================

    private void setImageBuyBtn() {
        int planeLevel = getPlaneLevel();
        int planeId = planeLevel - 1;
        int cost = getPlaneCost(planeId);
        String level = "lvl. " + planeLevel;
        String imageName = "plane" + planeLevel;
        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        buyPlaneImageButton.setImageResource(imageResId != 0 ? imageResId : R.drawable.plane1);
        buyPlanePrice.setText(String.valueOf(cost));
        buyPlaneLevel.setText(level);
        btnBuyPlane.setAlpha(1.0f);
        btnBuyPlane.setClickable(true);
    }

    private void setDefaultBuyButton() {
        buyPlaneImageButton.setImageResource(R.drawable.plane1);
        buyPlanePrice.setText("100");
        buyPlaneLevel.setText("lvl 1");
        btnBuyPlane.setAlpha(1.0f);
        btnBuyPlane.setClickable(true);
    }

    // ==================== DRAG & DROP ====================

    private void initializeDragAndDrop() {
        dragHelper = new DragHelper(
                gridView,
                gridAdapter,
                gridPlanes,
                viewModel,
                gameBoardView,
                rubbishHandler,
                toastHelper
        );

        setupGridDragListener();
        setupRubbishDragListener();
    }

    private void setupGridDragListener() {
        gridView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    showRubbish();
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
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
    }

    private void setupRubbishDragListener() {
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
                    handleRubbishDrop(event);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    rubbishHandler.highlight(false);
                    return true;
            }
            return false;
        });
    }

    private void handleRubbishDrop(DragEvent event) {
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
    }

    // ==================== CLICK LISTENERS ====================

    private void setupClickListeners() {
        setupGridItemClickListener();
        setupGridItemLongClickListener();
        setupNavigationListeners();
        setupBuyPlaneListener();
    }

    private void setupGridItemClickListener() {
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            int levelPlaneOnCell = gridAdapter.getLevelPlane(position);

            if (levelPlaneOnCell > 0) {
                if (levelPlaneOnCell > 10) {
                    gridAdapter.openGiftBox(position);
                    return;
                }
                addPlaneToCell(row, col, levelPlaneOnCell, position);
            } else if (levelPlaneOnCell < 0) {
                removePlaneFromCell(row, col, levelPlaneOnCell, position);
            }
        });
    }

    private void addPlaneToCell(int row, int col, int levelPlaneOnCell, int position) {
        RunningPlane runningPlane = new RunningPlane();
        runningPlane.setPlaneId(levelPlaneOnCell);
        runningPlane.setOdometer(0);
        runningPlane.setSpeed(0.2 * Math.pow(1.1, levelPlaneOnCell - 1));

        gridPlanes[row][col] = runningPlane;
        viewModel.onPlaneAdded(runningPlane);
        gameBoardView.addRunningPlane(runningPlane);
        gridAdapter.cellClicked(position);
        updateProfit(levelPlaneOnCell, true);
    }

    private void removePlaneFromCell(int row, int col, int levelPlaneOnCell, int position) {
        RunningPlane planeToRemove = gridPlanes[row][col];

        if (planeToRemove != null) {
            viewModel.onPlaneRemoved(planeToRemove);
            gameBoardView.removeRunningPlane(planeToRemove);
            gridAdapter.cellClicked(position);
            gridPlanes[row][col] = null;
            updateProfit(Math.abs(levelPlaneOnCell), false);
        }
    }

    private void setupGridItemLongClickListener() {
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
    }

    private void setupNavigationListeners() {
        btnShop.setOnClickListener(v ->
                startActivity(new Intent(MainBoardActivity.this, ShopActivity.class))
        );

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainBoardActivity.this, SettingActivity.class))
        );
    }

    private void setupBuyPlaneListener() {
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
    }

    // ==================== VIEWMODEL OBSERVERS ====================

    private void setupViewModelObservers() {
        viewModel.getUserName().observe(this, value ->
                userNameTextView.setText(value)
        );

        viewModel.getTotalCoins().observe(this, this::updateCoinsDisplay);

        viewModel.getGameBoardVersion().observe(this, value ->
                gameBoardView.invalidate()
        );
    }

    private void updateCoinsDisplay(double value) {
        long  roundedValue = Math.round(value);
        int symbol = -1;
        if (value >= 10000000) {
            roundedValue /= 1000000;
            symbol = 1;
        } else if (value >= 10000) {
            roundedValue /= 1000;
            symbol = 0;
        }

        String displayText = String.valueOf(roundedValue);
        if (symbol != -1) {
            displayText += LETTERS[symbol];
        }

        totalCoinsTextView.setText(displayText);
    }

    // ==================== PROFIT MANAGEMENT ====================

    private void updateProfit(int level, boolean isAdding) {
        if (level == 0) {
            double result = 0;
            totalProfitRateTextView.setText(String.format("%.2f", result));
            return;
        }
        if (level < 0 || level > 10) {
            return;
        }
        String text = totalProfitRateTextView.getText().toString();
        double currentValue = parseCurrentProfit(text);
        try {
            double nowProfit = countCpsPerSecond(level - 1);
            double result = isAdding ? currentValue + nowProfit : Math.abs(currentValue - nowProfit);
            totalProfitRateTextView.setText(String.format("%.2f", result));
        } catch (Throwable t) {
            Log.e("MainBoard", "Error in countCpsPerSecond", t);
        }
    }

    private double parseCurrentProfit(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }

        try {
            String normalized = text.replace(',', '.');
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // ==================== UI ANIMATIONS ====================

    private void showRubbish() {
        if (rubbish != null && rubbish.getVisibility() != View.VISIBLE) {
            rubbish.setVisibility(View.VISIBLE);
            rubbish.setAlpha(0f);
            rubbish.animate().alpha(1f).setDuration(200).start();
        }
    }

    private void hideRubbish() {
        if (rubbish != null && rubbish.getVisibility() == View.VISIBLE) {
            rubbish.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        rubbish.setVisibility(View.GONE);
                        rubbishHandler.highlight(false);
                    })
                    .start();
        }
    }

    // =================== For DeleteAll ======================

    public static void clearGameBoard() {
        if (mainBoardinstance != null && mainBoardinstance.gameBoardView != null) {
            mainBoardinstance.clearMainBoard();
        }
    }
    public void clearMainBoard() {
        // 1. Очищаем сохранение
        saveManager.clearSave();
        clearOpenedPlanes();
        // 2. Убираем все самолеты с трассы
        for (int row = 0; row < GRID_ROWS; ++row) {
            for (int col = 0; col < GRID_COLS; ++col) {
                if (gridPlanes[row][col] != null) {
                    viewModel.onPlaneRemoved(gridPlanes[row][col]);
                    gameBoardView.removeRunningPlane(gridPlanes[row][col]);
                    gridPlanes[row][col] = null;
                }
            }
        }

        // 3. Зануляем все ячейки в grid
        GameGridAdapter gameGrid = GameGridAdapter.getInstance();
        for (int row = 0; row < GRID_ROWS; ++row) {
            for (int col = 0; col < GRID_COLS; ++col) {
                gameGrid.setCell(row, col, 0);
            }
        }
        gameGrid.notifyDataSetChanged();

        // 4. Обнуляем количество купленных самолетов в C++
        clearCurPurchased();

        // 5. Сбрасываем GameModel
        gameModel.clearGame();
        gameModel.setBalance(1000.0);
        gameModel.setUserName("Player");

        // 6. Обновляем UI
        totalProfitRateTextView.setText("0.00");
        totalCoinsTextView.setText("1000");
        gameBoardView.invalidate();

        // 7. Перезапускаем активити для полного сброса
        Toast.makeText(this, "Прогресс сброшен", Toast.LENGTH_SHORT).show();

        // Перезапуск
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGameState();  // Сохраняем при сворачивании
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveGameState();  // Сохраняем при закрытии
    }
    private void saveGameState() {
        GameGridAdapter gameGrid = GameGridAdapter.getInstance();
        int[][] grid = gameGrid.getGrid();

        int[] purchasedCounts = new int[10];
        for (int i = 0; i < 10; i++) {
            purchasedCounts[i] = ShopActivity.getPlanePurchased(i);
        }

        String playerName = gameModel.getUserName();
        if (playerName == null) playerName = "Player";

        double totalCoin = gameModel.getTotalCoins();
        boolean[] wasOpened = new boolean[10];
        for (int i = 0; i < 10; ++i) {
            wasOpened[i] = isOpen(i);
        }
        saveManager.saveGame(grid, purchasedCounts, playerName, totalCoin, wasOpened);
    }

    private void loadGameState() {
        SaveManager.GameSaveData data = saveManager.loadGame();

        // Устанавливаем монеты
        gameModel.setBalance(data.totalCoin);

        // Устанавливаем имя
        gameModel.setUserName(data.playerName);

        // Восстанавливаем grid
        GameGridAdapter gameGrid = GameGridAdapter.getInstance();
        int[][] loadedGrid = data.grid;
        for (int i = 0; i < 8; i++) {
            int row = i / 2;
            int col = i % 2;
            gameGrid.setCell(row, col, loadedGrid[row][col]);
        }

        // Восстанавливаем purchasedCounts в C++
        restorePurchasedCounts(data.purchasedCounts);
        restoreOpenedPlanes(data.isOpened);
        gameGrid.notifyDataSetChanged();

        // Если первый запуск - сразу сохраняем начальное состояние
        if (data.isFirstLaunch) {
            saveGameState();
            Log.d("MainBoard", "First launch - saved initial state");
        }
        setImageBuyBtn();
    }
    //--------------------ANIMATION----------------------

    public static void playMergeAnimationStatic(int planeLevel) {
        if (mainBoardinstance != null) {
            mainBoardinstance.playMergeAnimation(planeLevel);
        }
    }
    public void playMergeAnimation(int planeLevel) {
        String videoName = "animation_merge_plane" + planeLevel;
        int videoRes = getResources().getIdentifier(videoName, "raw", getPackageName());

        if (videoRes == 0) {
            Log.e("Anim", "Video not found: " + videoName);
            return;
        }

        VideoView videoView = new VideoView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        videoView.setLayoutParams(params);
        videoView.setClickable(true);
        videoView.setFocusable(true);

        String path = "android.resource://" + getPackageName() + "/" + videoRes;
        videoView.setVideoURI(Uri.parse(path));

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
        });

        // По завершению ставим на паузу на последнем кадре
        videoView.setOnCompletionListener(mp -> {
            ViewGroup rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                rootView.removeView(videoView);
            }
        });

        // При нажатии — убрать
        videoView.setOnClickListener(v -> {
            ViewGroup rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                rootView.removeView(videoView);
            }
        });

        ViewGroup rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.addView(videoView);
            videoView.start();
        }
    }
}