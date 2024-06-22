package com.BudgiePanic.rendering.util.raster;

import java.util.Arrays;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;

/**
 * Rasterizes straight lines onto the canvas. Currently has O(n) runtime complexity and O(n) memory complexity.
 *
 * @author BudgiePanic
 */
public final class CanvasLineDrawer {
    
    private CanvasLineDrawer() {}

    /**
     * Draw a straight line on a canvas
     * @param x0
     *   The x coord of the start point of the line
     * @param y0
     *   The y coord of the start point of the line
     * @param x1
     *   The x coord of the end point of the line
     * @param y1
     *   The y coord of the end point of the line
     * @param drawCanvas
     *   The canvas the line will be drawn onto
     * @param lineColor
     *   The color of the line
     */
    public final static void drawLine(int x0, int y0, int x1, int y1, Canvas drawCanvas, Color lineColor) {
        int[] clamped = boundsCheck(x0, y0, x1, y1, drawCanvas);
        x0 = clamped[0]; y0 = clamped[1]; x1 = clamped[2]; y1 = clamped[3];
        final int xStep = x1 - x0, yStep = y1 - y0;
        if (Math.abs(xStep) > Math.abs(yStep)) {
            // draw a horizontal line
            if (x0 > x1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
            }
            final int[] yValues = linearInterpolate(x0, y0, x1, y1, new int[1 + (x1-x0)]);
            for (int x = x0; x <= x1; x++) {
                drawCanvas.writePixel(x, yValues[x - x0], lineColor);
            }
        } else {
            // draw a vertical line
            if (y0 > y1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
            }
            final int[] xValues = linearInterpolate(y0, x0, y1, x1, new int[1 + (y1-y0)]);
            for (int y = y0; y <= y1; y++) {
                drawCanvas.writePixel(xValues[y - y0], y, lineColor);
            }
        }
    }

    /**
     * Draw a straight line on the canvas. Don't draw the line segment if it is behind an object according to the depth buffer.
     *
     * @param x0
     *   The column of the start pixel of the line
     * @param y0
     *   The row of the start pixel of the line
     * @param z0
     *   The depth of the start pixel in local camera space
     * @param x1
     *   The column of the end pixel of the line
     * @param y1
     *   The row of the end pixel of the line
     * @param z1
     *   The depth of the end pixel in local camera space
     * @param drawCanvas
     *   The canvas the line will be drawn onto
     * @param depthBuffer
     *   The depth buffer. 
     *   If using DepthCamera to generate a depth buffer, use rawUnclamped depth mode and PointDistance distance mode for normal results.
     * @param lineColor
     *   The color of the line
     */
    public final static synchronized void drawLineDepth(int x0, int y0, double z0, int x1, int y1, double z1, Canvas drawCanvas, Canvas depthBuffer, Color lineColor) {
        int[] clamped = boundsCheck(x0, y0, x1, y1, drawCanvas);
        x0 = clamped[0]; y0 = clamped[1]; x1 = clamped[2]; y1 = clamped[3];
        final int xStep = x1 - x0, yStep = y1 - y0;
        if (Math.abs(xStep) > Math.abs(yStep)) {
            // draw a horizontal line
            if (x0 > x1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
                double z = z0;
                z0 = z1; z1 = z;
            }
            final int[] yValues = linearInterpolate(x0, y0, x1, y1, new int[1 + (x1-x0)]);
            final double[] zInvValues = linearInterpolate(x0, 1.0 / z0, x1, 1.0 / z1, new double[1 + (x1-x0)]);
            for (int x = x0; x <= x1; x++) {
                final int y = yValues[x - x0];
                final double depth = 1.0 / zInvValues[x - x0];
                if (depth < depthBuffer.getPixel(x, y).x) { 
                    drawCanvas.writePixel(x, y, lineColor);
                    depthBuffer.writePixel(x, y, new Color(depth, depth, depth));
                }
            }
        } else {
            // draw a vertical line
            if (y0 > y1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
                double z = z0;
                z0 = z1; z1 = z;
            }
            final int[] xValues = linearInterpolate(y0, x0, y1, x1, new int[1 + (y1-y0)]);
            final double[] zInvValues = linearInterpolate(y0, 1.0 / z0, y1, 1.0 / z1, new double[1 + (y1-y0)]);
            for (int y = y0; y <= y1; y++) {
                final int x = xValues[y-y0];
                final double depth = 1.0 / zInvValues[y - y0];
                if (depth < depthBuffer.getPixel(x, y).x) {
                    drawCanvas.writePixel(xValues[y - y0], y, lineColor);
                    depthBuffer.writePixel(x, y, new Color(depth, depth, depth));
                }
            }
        }
    }

    private static int[] boundsCheck(int x0, int y0, int x1, int y1, Canvas canvas) {
        final var input = new int[] {x0,y0,x1,y1};
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        if((x0 < 0 || x0 >= width) || 
           (x1 < 0 || x1 >= width) ||
           (y0 < 0 || y0 >= height) ||
           (y1 < 0 || y1 >= height)) {
            System.out.print("WARN: line " + Arrays.toString(input) + " is out of bounds for canvas " + canvas.toString() + ". Clamping out of bounds values! ");
            x0 = x0 < 0 ? 0 : x0 >= width ? width - 1 : x0;
            y0 = y0 < 0 ? 0 : y0 >= height ? height - 1 : y0;
            x1 = x1 < 0 ? 0 : x1 >= width ? width - 1 : x1;
            y1 = y1 < 0 ? 0 : y1 >= height ? height - 1 : y1;
            final var clamped = new int[] {x0,y0,x1,y1};
            final var diff = Arrays.copyOf(clamped, 4);
            for (int i = 0; i < diff.length; i++) {
                diff[i] = diff[i] - input[i];
            }
            System.out.println("line parameter diff after clamping: " + Arrays.toString(diff));
            return clamped;
        }
        return input;
    }

    /**
     * Determine the interpolated values for a linear function ranging from i0 to i1, that outputs values ranging from d0 to d1.
     *
     * @param i0
     *   The first input into the linear function
     * @param d0
     *   The first output of the linear function
     * @param i1
     *   The last input into the linear function
     * @param d1
     *   The last output of the linear function
     * @param result
     *   An array of length (i1 - i0) to write the interpolated function output values into.
     * @return
     *   Returns the inputted result array.
     */
    protected static final int[] linearInterpolate(int i0, int d0, int i1, int d1, int[] result) {
        assert result != null && result.length == Math.abs((i0 - i1)) + 1;
        if (i0 == i1) { 
            result[0] = i0;
            return result;
        }
        final double gradient = ((double)(d1 - d0))/((double)(i1 - i0));
        double dependent = d0;
        for (int i = i0; i <= i1; i++) {
            result[i - i0] = (int) Math.round(dependent);
            dependent += gradient;
        }
        return result;
    }

    /**
     * Determine the interpolated values for a linear function ranging from i0 to i1, that outputs values ranging from d0 to d1.
     *
     * @param i0
     *   The first input into the linear function
     * @param d0
     *   The first output of the linear function
     * @param i1
     *   The last input into the linear function
     * @param d1
     *   The last output of the linear function
     * @param result
     *   An array to write the interpolated function output values into. The array length should match the length of the pixel array
     * @return
     *   Returns the inputted result array.
     */
    protected static final double[] linearInterpolate(double i0, double d0, double i1, double d1, double[] result) {
        assert result != null;
        final double gradient = (d1 - d0)/(i1 - i0);
        double dependent = d0;
        for (int i = 0; i < result.length; i++) {
            result[i] = dependent;
            dependent += gradient;
        }
        return result;
    }
}
