package ru.livins.aeroboost.view.drag;

import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.adapter.GameGridAdapter;
import ru.livins.aeroboost.model.RunningPlane;
import ru.livins.aeroboost.view.GameBoardView;
import ru.livins.aeroboost.viewmodel.MainBoardViewModel;

public class DragHelper {

    private final GridView gridView;
    private final GameGridAdapter gridAdapter;
    private final RunningPlane[][] gridPlanes;
    private final MainBoardViewModel viewModel;
    private final GameBoardView gameBoardView;
    private final RubbishHandler rubbishHandler;
    private final CellHighlighter cellHighlighter;
    private final ToastHelper toastHelper;

    public DragHelper(GridView gridView, GameGridAdapter gridAdapter,
                      RunningPlane[][] gridPlanes, MainBoardViewModel viewModel,
                      GameBoardView gameBoardView, RubbishHandler rubbishHandler,
                      ToastHelper toastHelper) {
        this.gridView = gridView;
        this.gridAdapter = gridAdapter;
        this.gridPlanes = gridPlanes;
        this.viewModel = viewModel;
        this.gameBoardView = gameBoardView;
        this.rubbishHandler = rubbishHandler;
        this.cellHighlighter = new CellHighlighter();
        this.toastHelper = toastHelper;
    }

    public void startDraggableImage(int row, int col, int level, Context context) {
        ImageView dragImageView = new ImageView(context);
        dragImageView.setImageResource(getPlaneImageForLevel(level));

        int sizeInPx = 250;
        dragImageView.setLayoutParams(new ViewGroup.LayoutParams(sizeInPx, sizeInPx));
        dragImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        dragImageView.measure(
                View.MeasureSpec.makeMeasureSpec(sizeInPx, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(sizeInPx, View.MeasureSpec.EXACTLY)
        );
        dragImageView.layout(0, 0, sizeInPx, sizeInPx);

        View cellView = gridView.getChildAt(row * 2 + col);
        if (cellView != null) {
            int[] location = new int[2];
            cellView.getLocationOnScreen(location);

            FrameLayout rootLayout = ((android.app.Activity) context).findViewById(android.R.id.content);
            int[] rootLocation = new int[2];
            rootLayout.getLocationOnScreen(rootLocation);

            rootLayout.addView(dragImageView);
            dragImageView.setX(location[0] - rootLocation[0]);
            dragImageView.setY(location[1] - rootLocation[1]);

            startDrag(dragImageView, row, col, level);
        }
    }

    private void startDrag(ImageView imageView, int startRow, int startCol, int level) {
        ClipData clipData = ClipData.newPlainText("plane",
                startRow + "," + startCol + "," + level);

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
        imageView.startDragAndDrop(clipData, shadowBuilder, imageView, 0);

        new android.os.Handler().postDelayed(() -> {
            if (imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }
        }, 100);
    }

    public void updateHighlight(DragEvent event) {
        float x = event.getX();
        float y = event.getY();
        int position = getPositionFromCoordinates(x, y);

        if (position == -2) {
            cellHighlighter.clearHighlight();
            rubbishHandler.highlight(true);
        } else if (position >= 0) {
            rubbishHandler.highlight(false);
            cellHighlighter.highlightCell(gridView, position);
        } else {
            cellHighlighter.clearHighlight();
            rubbishHandler.highlight(false);
        }
    }

    public void clearHighlights() {
        cellHighlighter.clearHighlight();
        rubbishHandler.highlight(false);
    }

    public boolean handleDrop(DragEvent event) {
        ClipData clipData = event.getClipData();
        if (clipData == null) return false;

        String data = clipData.getItemAt(0).getText().toString();
        String[] parts = data.split(",");
        int startRow = Integer.parseInt(parts[0]);
        int startCol = Integer.parseInt(parts[1]);
        int level = Integer.parseInt(parts[2]);

        float dropX = event.getX();
        float dropY = event.getY();

        int targetPosition = getPositionFromCoordinates(dropX, dropY);
        toastHelper.showToast("targetPosition: " + targetPosition);

        if (targetPosition == -2) {
            deletePlane(startRow, startCol, level);
            return true;
        }

        if (targetPosition >= 0) {
            int targetRow = targetPosition / 2;
            int targetCol = targetPosition % 2;

            if (targetRow == startRow && targetCol == startCol) {
                return true;
            }

            int targetLevel = gridAdapter.getLevelPlane(targetPosition);

            if (gridPlanes[targetRow][targetCol] == null && targetLevel == level) {
                mergePlane(startRow * 2 + startCol, targetRow * 2 + targetCol);
            } else if (gridPlanes[targetRow][targetCol] == null) {
                movePlane(startRow * 2 + startCol, targetRow * 2 + targetCol);
            }
            return true;
        }

        return false;
    }

    private void deletePlane(int row, int col, int level) {
        RunningPlane planeToRemove = gridPlanes[row][col];
        if (planeToRemove != null) {
            gridPlanes[row][col] = null;
            viewModel.onPlaneRemoved(planeToRemove);
            gameBoardView.removeRunningPlane(planeToRemove);
            gridAdapter.cellClicked(row * 2 + col);
            toastHelper.showToast("Самолет удален!");
        }
    }

    private void mergePlane(int fromPosition, int toPosition) {
        gridAdapter.upgradePlane(fromPosition, toPosition);
    }

    private void movePlane(int fromPosition, int toPosition) {
        gridAdapter.movePlane(fromPosition, toPosition);
    }

    private int getPositionFromCoordinates(float x, float y) {
        int gridWidth = 500;
        int gridHeight = 1000;
        int totalItems = gridAdapter.getCount();

        int cellWidth = gridWidth / 2;
        int cellHeight = gridHeight / (totalItems / 2);

        int col = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);

        int totalRows = totalItems / 2;
        if (row >= 0 && row < totalRows && col >= 0 && col < 2) {
            return row * 2 + col;
        }

        if (rubbishHandler.isOverRubbish(x, y)) {
            return -2;
        }

        return -1;
    }

    private int getPlaneImageForLevel(int level) {
        switch (level) {
            case 1: return R.drawable.plane1;
            case 2: return R.drawable.plane2;
            case 3: return R.drawable.plane3;
            case 4: return R.drawable.plane4;
            case 5: return R.drawable.plane5;
            case 6: return R.drawable.plane6;
            case 7: return R.drawable.plane7;
            case 8: return R.drawable.plane8;
            case 9: return R.drawable.plane9;
            case 10: return R.drawable.plane10;
            default: return R.drawable.plane1;
        }
    }
}