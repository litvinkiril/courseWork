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
        int cellSize = 250;
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;
        //путь от старта и +500
        for (int i = 0; i < cellSize * 2; i += 10) {
            positions.add(new TracePosition((double) viewWidth / 2 + traceDimension, (double) viewHeight / 2 - i, 0));
        }
        //вверх
        for (int i = 0; i < 2 * cellSize + 50; i += 7) {
            double t = (double) i / (2 * cellSize + 50);
            double grade = i / 550.0 * 180;
            grade *= -1;
            double currentAngle;
            currentAngle = t * Math.PI;
            double x = centerX + traceDimension * Math.cos(currentAngle);
            double y = centerY - (traceDimension / 2.0)* Math.sin(currentAngle);

            positions.add(new TracePosition(x, y - 500 , (int) grade));
        }
        //путь слева
        for (int i = 0; i < cellSize * 3.5; i += 10) {
            positions.add(new TracePosition( (double) viewWidth / 2 - traceDimension, (double) viewHeight / 2 - 500 + i, 180));
        }
        //путь снизу
        for (int i = 0; i < 2 * cellSize + 50; i += 6) {
            double t = (double) i / (2 * cellSize + 50);
            double grade = i / 550.0 * 180 + 180;
            grade *= -1;
            double currentAngle;
            currentAngle = t * Math.PI;
            double x = centerX - traceDimension * Math.cos(-1 * currentAngle);
            double y = centerY - (traceDimension / 2.0)* Math.sin(-1 * currentAngle);

            positions.add(new TracePosition(x, y + 375 , (int) grade));
        }
        //дорисовываем путь справо
        for (int i = 0; i < cellSize * 1.5; i += 10) {
            positions.add(new TracePosition((double) viewWidth / 2 + traceDimension, centerY + 375 - i, 0));
        }
    }

    public TracePosition getPosition(double odometer) {
        var fraction = odometer - (int)odometer;
        var index = (int)(fraction * positions.size());
        if (index < 0) index = 0;
        if (index >= positions.size()) index = positions.size() - 1;
        return positions.get(index);
    }
    // В классе PlaneTrace добавьте этот метод:
    public List<TracePosition> getAllPositions() {
        return new ArrayList<>(positions);
    }
}