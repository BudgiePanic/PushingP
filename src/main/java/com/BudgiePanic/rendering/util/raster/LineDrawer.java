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
        if (x0 > x1) {
            // swap
            int x = x0, y = y0;
            x0 = x1; y0 = y1;
            x1 = x; y1 = y;
        }
        final double gradient = ((double)(y1 - y0)) / ((double)(x1 - x0));
        double y = y0;
        for (int x = x0; x <= x1; x++) {
            drawCanvas.writePixel(x, (int) Math.round(y), lineColor);
            y = y + gradient;
        }
    }
}
