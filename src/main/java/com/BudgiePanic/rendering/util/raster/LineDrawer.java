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
        final double offset = y0 - gradient * x0;
        for (int x = x0; x <= x1; x++) {
            final var e = (gradient * x + offset);
            final int y = (int) Math.round(e);
            drawCanvas.writePixel(x, y, lineColor);
        }
    }

}
