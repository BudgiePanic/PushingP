package com.BudgiePanic.rendering.util.raster;

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
        final int xStep = x1 - x0, yStep = y1 - y0;
        if (Math.abs(xStep) > Math.abs(yStep)) {
            // draw a horizontal line
            if (x0 > x1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
            }
            int[] yValues = linearInterpolate(x0, y0, x1, y1, new int[1 + (x1-x0)]);
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
            int[] xValues = linearInterpolate(y0, x0, y1, x1, new int[1 + (y1-y0)]);
            for (int y = y0; y <= y1; y++) {
                drawCanvas.writePixel(xValues[y - y0], y, lineColor);
            }
        }
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
}