package ru.livins.aeroboost.view.drag;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.GridView;

public class CellHighlighter {

    private View currentlyHighlightedCell;

    public void highlightCell(GridView gridView, int position) {
        View cellView = gridView.getChildAt(position);

        if (currentlyHighlightedCell == cellView) {
            return;
        }

        clearHighlight();

        if (cellView != null) {
            cellView.setForeground(new ColorDrawable(0x80FFFFFF));
            currentlyHighlightedCell = cellView;
        }
    }

    public void clearHighlight() {
        if (currentlyHighlightedCell != null) {
            currentlyHighlightedCell.setForeground(null);
            currentlyHighlightedCell = null;
        }
    }
}