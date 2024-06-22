package com.BudgiePanic.rendering.util.raster;

import java.util.List;
import java.util.Optional;

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
import com.BudgiePanic.rendering.util.shape.Parent;
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
    public static void drawBoundingBox(Shape shape, BasePerspectiveCamera camera, Canvas canvas, Color boxColor, Optional<Canvas> depthBuffer) {
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
            drawLine(from, to, camera, canvas, boxColor, depthBuffer);
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
    public static void drawLine(Tuple from, Tuple to, BasePerspectiveCamera camera, Canvas canvas, Color lineColor, Optional<Canvas> depthBuffer) {
        // drawing stages:
        //   - transform the points defining the line segment from world space into camera space
        //   - clip the line segment against the camera's view frustrum (this may: leave the points unchanges, change one of the points, remove the points(line can't be seen by camera))
        //   - project the clipped line segment (if it wasn't filtered by the clipping process. Can early return here)
        //   - get the pixel locations of the line segment start and end, pass them onto canvas line drawer
        Tuple localFrom = camera.transform(from);
        Tuple localTo = camera.transform(to);
        var clipped = camera.clip(localFrom, localTo);
        if (clipped.isEmpty()) { return; }
        localFrom = clipped.get().a();
        localTo = clipped.get().b();
        final int[] fromPixel = camera.project(localFrom);
        final int[] toPixel = camera.project(localTo);
        // flip the pixel coordonates, our camera transform causes the pixels to be projected onto the wrong half of the screen
        fromPixel[0] = camera.width - fromPixel[0];
        toPixel[0] = camera.width - toPixel[0];
        fromPixel[1] = camera.height - fromPixel[1];
        toPixel[1] = camera.height - toPixel[1];
        if (depthBuffer.isPresent()) {
            final double cameraOrigin = 0.0;
            final double z0 = cameraOrigin - localFrom.z;
            final double z1 = cameraOrigin - localTo.z;
            CanvasLineDrawer.drawLineDepth(fromPixel[0], fromPixel[1], z0, toPixel[0], toPixel[1], z1, canvas, depthBuffer.get(), lineColor);
        } else {
            CanvasLineDrawer.drawLine(fromPixel[0], fromPixel[1], toPixel[0], toPixel[1], canvas, lineColor);
        }
    }

    public sealed interface Depth permits Depth.All, Depth.Limit {
        final class All implements Depth {
            private All() {}
            @Override
            public int getMaxDepth() { return Integer.MAX_VALUE; }
        }
        final class Limit implements Depth {
            final int limit;
            private Limit(int limit) {this.limit = limit;}
            @Override
            public int getMaxDepth() {return this.limit;}            
        }
        int getMaxDepth();
    }

    public sealed interface DrawOption permits DrawOption.AllShapes, DrawOption.ConcreteOnly, DrawOption.ParentsOnly {
        boolean shouldDraw(Shape shape);
        final class AllShapes implements DrawOption {
            @Override
            public boolean shouldDraw(Shape shape) { return true; }
        } 
        final class ConcreteOnly implements DrawOption {
            @Override
            public boolean shouldDraw(Shape shape) { return !(shape instanceof Parent); }
        } 
        final class ParentsOnly implements DrawOption {
            @Override
            public boolean shouldDraw(Shape shape) { return shape instanceof Parent; }
        } 
    }

    /**
     * Draws all shapes' bounding boxes in the shape hierarchy.
     */
    public static DrawOption ALL_SHAPES = new DrawOption.AllShapes();
    /**
     * Only draws implementation shapes in the shape hierarchy.
     */
    public static DrawOption CONCRETE_SHAPES = new DrawOption.ConcreteOnly();
    /**
     * Only draws abstract shapes that contain other shapes within them.
     */
    public static DrawOption CONTAINER_SHAPES = new DrawOption.ParentsOnly();

    /**
     * Explore the entire parent hierarchy
     */
    public static Depth ALL_CHILDREN = new Depth.All();

    /**
     * Create a new Depth limit.
     * @param limit
     *   The Parent shape hierarchy depth exploration limit.
     * @return
     *   A depth limit.
     */
    public static Depth depthLimit(int limit) { return new Depth.Limit(limit); }

    /**
     * Traverse a parent shape hierarchy, drawinng the bounding boxes of shapes in the hierarchy.
     *
     * @param depthLimit
     *   The exploration limit of the parent shape's children hierarhy
     * @param shape
     *   The parent shape whose children's bounding boxes will be drawn.
     * @param camera
     *   The camera taking the image.
     * @param canvas
     *   The canvas to write the line segments to.
     * @param colors
     *   The colors to cycle between while drawing boxes
     * @param drawContainerShapes
     *   If true, draw all bounding boxes in the hierarchy.
     *   If false, only concrete shape implementations will be drawn, intermediate nested composite shapes will not be drawn.
     */
    public static void drawChildBoundingBoxes(Depth depthLimit, Parent shape, BasePerspectiveCamera camera, Canvas canvas, Color[] colors, Optional<Canvas> depthBuffer, DrawOption option) {
        // do a depth first exploration of the shape's hierarchy
        if (colors == null || colors.length == 0) {
            System.out.println("WARN: no colors supplied to line drawer. aborting draw.");
            return;
        }
        recursiveDraw(depthLimit, shape, 0, 0, camera, canvas, colors, depthBuffer, option);
    }

    protected static void recursiveDraw(final Depth limit, Shape shape, int depth, int color, final BasePerspectiveCamera camera, final Canvas canvas, final Color[] colors, final Optional<Canvas> depthBuffer, final DrawOption option) {
        if (limit.getMaxDepth() < depth) { return; }
        final boolean isParent = (shape instanceof Parent);
        // should we draw this shape?
        if (option.shouldDraw(shape)) {
            drawBoundingBox(shape, camera, canvas, colors[color], depthBuffer);
        }
        // does this shape have chilren we can try to draw?
        if (isParent) {
            final int numbColors = colors.length;
            Parent parent = (Parent) shape;
            for (final Shape child : parent.children()) {
                color = color < numbColors - 1 ? color + 1 : 0;
                recursiveDraw(limit, child, depth + 1, color, camera, canvas, colors, depthBuffer, option);
            }
        }
    }

}
