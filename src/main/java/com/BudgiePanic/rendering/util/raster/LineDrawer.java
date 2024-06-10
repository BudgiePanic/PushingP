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

    protected static void drawLine(int x0, int y0, int x1, int y1, Canvas drawCanvas, Color lineColor) {
        final double gradient =  ((double)(y1 - y0)) / ((double)(x1 - x0));
        // the initial point
        drawCanvas.writePixel(x0, y0, lineColor);
        // the rest of the points
        double lastY = y0;
        for (int x = x0 + 1; x <= x1; x++) {
            double nextY = lastY + gradient;
            final int y = (int) Math.round(nextY);
            drawCanvas.writePixel(x, y, lineColor);
            lastY = nextY;
        }
    }

}
