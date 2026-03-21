package ru.livins.aeroboost.view;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.ColorDrawable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.adapter.GameGridAdapter;
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
    private GridView gridView;

    private ImageView rubbish;

    private static native double countCpsPerSecond(int planeId);

    private RunningPlane[][] gridPlanes = new RunningPlane[4][2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_board_activity);

        var viewModelProvider = new ViewModelProvider(this);
        var viewModel = viewModelProvider.get(MainBoardViewModel.class);

        // Статус игры.
        rubbish =findViewById(R.id.rubbishImage);
        userNameTextView = findViewById(R.id.userName);
        totalProfitRateTextView = findViewById(R.id.totalProfitRate);
        totalCoinsTextView = findViewById(R.id.totalCoins);

        gameBoardView = findViewById(R.id.gameBoardView);

        // Элементы на форме.
        gridView = findViewById(R.id.gridView);
        gridAdapter = new GameGridAdapter(this);
        gridView.setAdapter(gridAdapter);

        // Обработчик короткого нажатия
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
                totalProfitRateTextView.setText(String.format("%.2f", result));
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
                totalProfitRateTextView.setText(String.format("%.2f", result));
            }
        });

        // Обработчик долгого нажатия
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            int row = position / 2;
            int col = position % 2;
            int levelPlaneOnCell = gridAdapter.getLevelPlane(position);
            if (levelPlaneOnCell > 0) {
                startDraggableImage(row, col, levelPlaneOnCell);
                return true;
            }
            return false;
        });

        // Drag and Drop слушатель для GridView
        gridView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    // Определяем ячейку под курсором и подсвечиваем её
                    highlightCellAtPosition(event);
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    // Обновляем подсветку при движении
                    updateHighlightAtPosition(event);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    // Убираем подсветку
                    clearAllHighlights();
                    return true;

                case DragEvent.ACTION_DROP:
                    clearAllHighlights();
                    handleDrop(event, v);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    clearAllHighlights();
                    v.setBackgroundColor(0x00000000);
                    return true;
            }
            return false;
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

    private void startDraggableImage(int row, int col, int level) {
        // Создаем ImageView с картинкой самолета
        ImageView dragImageView = new ImageView(this);

        // Устанавливаем картинку
        int imageRes = getPlaneImageForLevel(level);
        dragImageView.setImageResource(imageRes);

        // Явно задаем размеры в пикселях
        int sizeInPx = 250;
        dragImageView.setLayoutParams(new ViewGroup.LayoutParams(sizeInPx, sizeInPx));
        dragImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Принудительно задаем размеры
        dragImageView.measure(
                View.MeasureSpec.makeMeasureSpec(sizeInPx, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(sizeInPx, View.MeasureSpec.EXACTLY)
        );
        dragImageView.layout(0, 0, sizeInPx, sizeInPx);

        // Добавляем на экран
        View cellView = gridView.getChildAt(row * 2 + col);
        if (cellView != null) {
            int[] location = new int[2];
            cellView.getLocationOnScreen(location);

            FrameLayout rootLayout = findViewById(android.R.id.content);
            int[] rootLocation = new int[2];
            rootLayout.getLocationOnScreen(rootLocation);

            rootLayout.addView(dragImageView);

            dragImageView.setX(location[0] - rootLocation[0]);
            dragImageView.setY(location[1] - rootLocation[1]);

            // Запускаем drag
            startDrag(dragImageView, row, col, level);
        }
    }

    private void startDrag(ImageView imageView, int startRow, int startCol, int level) {
        // Создаем данные для перетаскивания
        ClipData clipData = ClipData.newPlainText("plane",
                startRow + "," + startCol + "," + level);

        // Создаем тень на основе ImageView
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);

        // Запускаем drag
        imageView.startDragAndDrop(clipData, shadowBuilder, imageView, 0);

        // Удаляем ImageView после начала drag
        new Handler().postDelayed(() -> {
            if (imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }
        }, 100);
    }

    private void handleDrop(DragEvent event, View targetView) {
        // Получаем данные о перетаскиваемом объекте
        ClipData clipData = event.getClipData();
        if (clipData == null) {
            Toast.makeText(this, "Упало не на ячейку", Toast.LENGTH_SHORT).show();
            return;

        };

        String data = clipData.getItemAt(0).getText().toString();
        String[] parts = data.split(",");
        int startRow = Integer.parseInt(parts[0]);
        int startCol = Integer.parseInt(parts[1]);
        int level = Integer.parseInt(parts[2]);

        // Получаем координаты падения относительно GridView
        float dropX = event.getX();
        float dropY = event.getY();

        // Определяем, на какую ячейку упала картинка
        int targetPosition = getPositionFromCoordinates(dropX, dropY);

        if (targetPosition >= 0) {
            int targetRow = targetPosition / 2;
            int targetCol = targetPosition % 2;
            if (targetRow == startRow && targetCol == startCol) {
                return;
            }

            if (gridPlanes[targetRow][targetCol] == null && gridAdapter.getLevelPlane(targetPosition) == level) {
                // Перемещаем самолет
                mergePlane(startRow * 2 + startCol, targetRow * 2 + targetCol);

                Toast.makeText(this, "Самолет перемещен в ячейку [" + targetRow + "," + targetCol + "]",
                        Toast.LENGTH_SHORT).show();
            }
            else if (gridPlanes[targetRow][targetCol] == null && gridAdapter.getLevelPlane(targetPosition) == 0) {
                movePlane(startRow * 2 + startCol, targetRow * 2 + targetCol);
            }
            else {
                Toast.makeText(this, "Ячейка занята!", Toast.LENGTH_SHORT).show();
            }
        }
        else  {
            Toast.makeText(this, "Самолет удален [" +"]",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void mergePlane(int fromPosition, int toPosition) {
        gridAdapter.upgradePlane(fromPosition, toPosition);
    }

    private void movePlane(int fromPosition, int toPosition) {
        gridAdapter.movePlane(fromPosition, toPosition);
    }

    private int getPositionFromCoordinates(float x, float y) {
        // Получаем реальные размеры GridView, а не жестко заданные
        int gridWidth = gridView.getWidth();
        int gridHeight = gridView.getHeight();
        int totalItems = gridAdapter.getCount();

        if (gridWidth == 0 || gridHeight == 0 || totalItems == 0) {
            return -1;
        }

        int cellWidth = gridWidth / 2;
        int cellHeight = gridHeight / (totalItems / 2);

        int col = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);

        int totalRows = totalItems / 2;
        if (row >= 0 && row < totalRows && col >= 0 && col < 2) {
            return row * 2 + col;
        }

        // Проверка на мусорку
        if (rubbish != null) {
            int[] rubbishLocation = new int[2];
            rubbish.getLocationOnScreen(rubbishLocation);

            int[] gridLocation = new int[2];
            gridView.getLocationOnScreen(gridLocation);

            float rubbishLeft = rubbishLocation[0] - gridLocation[0];
            float rubbishTop = rubbishLocation[1] - gridLocation[1];
            float rubbishRight = rubbishLeft + rubbish.getWidth();
            float rubbishBottom = rubbishTop + rubbish.getHeight();

            if (x >= rubbishLeft && x <= rubbishRight &&
                    y >= rubbishTop && y <= rubbishBottom) {
                return -2; // Код для мусорки
            }
        }

        return -1;
    }


    private int getPlaneImageForLevel(int level) {
        // Здесь укажите свои ресурсы картинок
        switch (level) {
            case 1:
                return R.drawable.plane1;
            case 2:
                return R.drawable.plane2;
            case 3:
                return R.drawable.plane3;
            case 4:
                return R.drawable.plane4;
            case 5:
                return R.drawable.plane5;
            case 6:
                return R.drawable.plane6;
            case 7:
                return R.drawable.plane7;
            case 8:
                return R.drawable.plane8;
            case 9:
                return R.drawable.plane9;
            case 10:
                return R.drawable.plane10;

            default:
                return R.drawable.plane1;
        }
    }

    private View currentlyHighlightedCell = null;

    private void highlightCellAtPosition(DragEvent event) {
        // Получаем координаты относительно GridView
        float x = event.getX();
        float y = event.getY();

        // Определяем позицию ячейки
        int position = getPositionFromCoordinates(x, y);

        if (position != -1) {
            highlightCell(position);
        }
    }

    private void updateHighlightAtPosition(DragEvent event) {
        float x = event.getX();
        float y = event.getY();

        int position = getPositionFromCoordinates(x, y);

        if (position != -1) {
            highlightCell(position);
        } else {
            clearAllHighlights();
        }
    }

    private void highlightCell(int position) {
        View cellView = gridView.getChildAt(position);

        if (currentlyHighlightedCell == cellView) {
            return;
        }

        clearAllHighlights();

        if (cellView != null) {
            // Устанавливаем foreground (полупрозрачный белый)
            cellView.setForeground(new ColorDrawable(0x80FFFFFF));
            currentlyHighlightedCell = cellView;
        }
    }

    private void clearAllHighlights() {
        if (currentlyHighlightedCell != null) {
            // Убираем foreground
            currentlyHighlightedCell.setForeground(null);
            currentlyHighlightedCell = null;
        }
    }
}