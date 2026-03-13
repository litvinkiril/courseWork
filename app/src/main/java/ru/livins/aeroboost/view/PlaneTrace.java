package ru.livins.aeroboost.view;

import java.util.ArrayList;
import java.util.List;

public class PlaneTrace {

    public static class TracePosition {
        public final double x;
        public final double y;
        public final double direction;
        public TracePosition(double x, double y, double direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    private final List<TracePosition> positions;

    public PlaneTrace(int viewWidth, int viewHeight, int traceDimension) {
        positions = new ArrayList<>();

        int leftMargin = 100;
        double coefficient = 1.8;
        double centerX = viewWidth / 2.6;
        double centerY = viewHeight / 3.8;

        double rectHeight = traceDimension * coefficient;

        int pointsWidth = 100; // Количество точек на каждой стороне
        int pointsHeigh = (int) (pointsWidth * coefficient);
        // ВЕРХНЯЯ СТОРОНА (справо налево)
        for (int i = 0; i <= pointsWidth; i++) {
            double t = (double) i / pointsWidth;
            double x = centerX + (double) traceDimension /2 - t * (double) traceDimension;
            double y = centerY - rectHeight/2;
            positions.add(new TracePosition(x, y, 270)); // Движение влево
        }

        // ЛЕВАЯ СТОРОНА (сверху вниз)
        for (int i = 1; i <= pointsHeigh; i++) { // i начинается с 1, чтобы не дублировать угол
            double t = (double) i / pointsHeigh;
            double x = centerX - (double) traceDimension /2;
            double y = centerY - rectHeight/2 + t * rectHeight;
            positions.add(new TracePosition(x, y, 180)); // Движение вниз
        }

        // НИЖНЯЯ СТОРОНА (слево направо)
        for (int i = 1; i <= pointsWidth; i++) {
            double t = (double) i / pointsWidth;
            double x = centerX - (double) traceDimension /2 + t * (double) traceDimension;
            double y = centerY + rectHeight/2;
            positions.add(new TracePosition(x, y, 90)); // Движение вправо
        }

        // ПРАВАЯ СТОРОНА (снизу вверх)
        for (int i = 1; i < pointsHeigh; i++) { // i < pointsPerSide, чтобы не дублировать угол
            double t = (double) i / pointsHeigh;
            double x = centerX + (double) traceDimension /2;
            double y = centerY + rectHeight/2 - t * rectHeight;
            positions.add(new TracePosition(x, y, 0)); // Движение вверх
        }
    }

    public TracePosition getPosition(double odometer) {
        var fraction = odometer - (int)odometer;
        var index = (int)(fraction * positions.size());
        if (index < 0) index = 0;
        if (index >= positions.size()) index = positions.size() - 1;
        return positions.get(index);
    }
}