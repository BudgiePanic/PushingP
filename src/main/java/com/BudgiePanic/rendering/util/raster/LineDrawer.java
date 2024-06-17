package com.BudgiePanic.rendering.util.raster;

import java.util.List;

import static com.BudgiePanic.rendering.util.shape.BoundingBox.index000;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index001;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index100;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index101;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index010;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index011;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index110;
import static com.BudgiePanic.rendering.util.shape.BoundingBox.index111;

import com.BudgiePanic.rendering.scene.BasePerspectiveCamera;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.BoundingBox;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * Converts 3D lines to 2D and rasterizes them to canvas.
 * 
 * @author BudgiePanic
 */
public class LineDrawer {
    
    /**
     * Calculates the line segments of an axis aligned bounding box and draws them.
     *
     * @param shape
     *   The shape to draw.
     * @param camera
     *   The camera that is taking the image.
     * @param canvas
     *   The canvas to write to.
     * @param boxColor
     *   The color of the line segments
     */
    public static void drawBoundingBox(Shape shape, BasePerspectiveCamera camera, Canvas canvas, Color boxColor) {
        final BoundingBox localBounds = shape.bounds();
        final List<Tuple> localPoints = localBounds.localPoints();
        final Tuple[] globalPoints = new Tuple[localPoints.size()];
        for (int i = 0; i < localPoints.size(); i++) {
            final var pointLocal = localPoints.get(i);
            globalPoints[i] = shape.pointToWorldSpace(pointLocal);
        }
        final Tuple[] lineSegmentsWorld = new Tuple[] {
            globalPoints[index000], globalPoints[index001], // 1
            globalPoints[index000], globalPoints[index010], // 2
            globalPoints[index000], globalPoints[index100], // 3

            globalPoints[index010], globalPoints[index110], // 4
            globalPoints[index010], globalPoints[index011], // 5

            globalPoints[index100], globalPoints[index110], // 6
            globalPoints[index001], globalPoints[index011], // 7

            globalPoints[index111], globalPoints[index110], // 8
            globalPoints[index111], globalPoints[index011], // 9

            globalPoints[index101], globalPoints[index111], // 10
            globalPoints[index101], globalPoints[index100], // 11
            globalPoints[index101], globalPoints[index001]  // 12
        };
        for (int i = 0; i < lineSegmentsWorld.length - 1; i += 2) {
            final Tuple from = lineSegmentsWorld[i];
            final Tuple to = lineSegmentsWorld[i + 1];
            drawLine(from, to, camera, canvas, boxColor);
        }
    }

    /**
     * 
     * @param from
     *   The starting point of the line segment
     * @param to
     *   The end point of the line segment
     * @param camera
     *   The camera taking the image
     * @param canvas
     *   The canvas to write to
     * @param lineColor
     *   The color of the line segment
     */
    public static void drawLine(Tuple from, Tuple to, BasePerspectiveCamera camera, Canvas canvas, Color lineColor) {
        final int[] fromPixel = camera.project(from);
        final int[] toPixel = camera.project(to);
        CanvasLineDrawer.drawLine(fromPixel[0], fromPixel[1], toPixel[0], toPixel[1], canvas, lineColor);
    }

}
