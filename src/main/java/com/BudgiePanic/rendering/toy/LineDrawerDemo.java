package com.BudgiePanic.rendering.toy;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.raster.CanvasLineDrawer;

public class LineDrawerDemo implements Runnable {

    @Override
    public void run() {
        final int size = 256;
        ArrayCanvas canvas = new ArrayCanvas(size, size);
        CanvasLineDrawer.drawLine(0, 0, size-1, size-1, canvas, Colors.white);
        CanvasLineDrawer.drawLine(size-1, 0, 0, size-1, canvas, Colors.white);
        CanvasLineDrawer.drawLine(size/2, 0, size/2, size-1, canvas, Colors.white);
        CanvasLineDrawer.drawLine(0, size/2, size-1, size/2, canvas, Colors.white);
        CanvasWriter.saveImageToFile(canvas, "lines.ppm");
    }
    
}
