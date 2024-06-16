package com.BudgiePanic.rendering.util.raster;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;

/**
 * Rasterizes straight lines onto the canvas.
 *
 * @author BudgiePanic
 */
public class LineDrawer {
    
    private LineDrawer() {}

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
    protected static void drawLine(int x0, int y0, int x1, int y1, Canvas drawCanvas, Color lineColor) {
        final int xStep = x1 - x0, yStep = y1 - y0;
        if (Math.abs(xStep) > Math.abs(yStep)) {
            // draw a horizontal line
            if (x0 > x1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
            }
            final double gradient = ((double)yStep) / ((double)xStep);
            double y = y0;
            for (int x = x0; x <= x1; x++) {
                drawCanvas.writePixel(x, (int) Math.round(y), lineColor);
                y = y + gradient;
            }
        } else {
            // draw a vertical line
            if (y0 > y1) {
                // swap
                int x = x0, y = y0;
                x0 = x1; y0 = y1;
                x1 = x; y1 = y;
            }
            final double gradient = ((double)xStep) / ((double)yStep);
            double x = x0;
            for (int y = y0; y <= y1; y++) {
                drawCanvas.writePixel((int) Math.round(x), y, lineColor);
                x = x + gradient;
            }
        }
    }
}
