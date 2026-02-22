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

        int viewHorzCenter = viewWidth / 2;
        int viewVertCenter = viewHeight / 2;
        int traceHalfDimension = traceDimension / 2;
        int traceLeft = viewHorzCenter - traceDimension;
        int traceRight = viewHorzCenter + traceDimension;
        int traceTop1 = viewVertCenter - traceDimension - traceHalfDimension;
        int traceTop2 = viewVertCenter - traceHalfDimension;
        int traceTop3 = viewVertCenter + traceHalfDimension;
        int traceTop4 = viewVertCenter + traceDimension + traceHalfDimension;

        int arcStepCount = (int)(Math.PI / 2 * traceDimension);
        double arcStepAngle = 90.0 / (arcStepCount + 1);

        positions = new ArrayList<>();

        // Правая верхняя дуга.
        positions.add(new TracePosition(viewHorzCenter, traceTop1, 0));
        for (int i = 1; i <= arcStepCount; i++) {
            var stepAngle = 0.0 + arcStepAngle * i;
            var xOffset = traceDimension * Math.cos(stepAngle);
            var yOffset = traceDimension * Math.sin(stepAngle);
            positions.add(new TracePosition(viewHorzCenter + xOffset, traceTop2 - yOffset, 90 - stepAngle));
        }

        // Правая прямая.
        for (int i = 0; i <= traceDimension; i++) {
            positions.add(new TracePosition(traceRight, traceTop2 + i, 90));
        }

        // Правая нижняя дуга.
        positions.add(new TracePosition(traceRight, traceTop3, 90));
        for (int i = 1; i <= arcStepCount; i++) {
            var stepAngle = 90.0 + arcStepAngle * i;
            var xOffset = traceDimension * Math.cos(stepAngle);
            var yOffset = traceDimension * Math.sin(stepAngle);
            positions.add(new TracePosition(viewHorzCenter + xOffset, traceTop3 + yOffset, 180 - stepAngle));
        }

        // Левая нижняя дуга.
        positions.add(new TracePosition(viewHorzCenter, traceTop4, 180));
        for (int i = 1; i <= arcStepCount; i++) {
            var stepAngle = 180.0 + arcStepAngle * i;
            var xOffset = traceDimension * Math.cos(stepAngle);
            var yOffset = traceDimension * Math.sin(stepAngle);
            positions.add(new TracePosition(viewHorzCenter - xOffset, traceTop4 - yOffset, 270 - stepAngle));
        }

        // Левая прямая.
        for (int i = 0; i <= traceDimension; i++) {
            positions.add(new TracePosition(traceLeft, traceTop3 - i, 270));
        }

        // Левая верхняя дуга.
        positions.add(new TracePosition(traceLeft, traceTop2, 270));
        for (int i = 1; i <= arcStepCount; i++) {
            var stepAngle = 270.0 + arcStepAngle * i;
            var xOffset = traceDimension * Math.cos(stepAngle);
            var yOffset = traceDimension * Math.sin(stepAngle);
            positions.add(new TracePosition(viewHorzCenter + xOffset, traceTop2 - yOffset, 270 + stepAngle));
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
