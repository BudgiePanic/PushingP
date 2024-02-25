package com.BudgiePanic.rendering.reporting;

import java.util.Iterator;
import java.util.function.Function;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;

/**
 * Monitors a render's progress and emits command line messages when progress milestones are met.
 * 
 * @author BudgiePanic
 */
public class ProgressWrapper implements Canvas {
    
    protected final Canvas internalCanvas;

    protected int counter;

    protected final int numbPixels;

    protected final int reportingInterval;

    protected int lastReport;

    public ProgressWrapper(Canvas canvas, int reportingInterval) {
        System.out.println("INFO: tracking write progress to " + canvas);
        this.internalCanvas = canvas;
        this.counter = 0;
        this.numbPixels = canvas.getHeight() * canvas.getWidth();
        this.reportingInterval = reportingInterval;
        this.lastReport = 0;
    }

    /**
     * Reports canvas write progress everytime 5% of the pixels are written to.
     * @param canvas
     *   The canvas to monitor.
     */
    public ProgressWrapper(Canvas canvas) { this(canvas, 5); }

    @Override
    public Iterator<Color> iterator() { return internalCanvas.iterator(); }

    @Override
    public Color getPixel(int column, int row) { return internalCanvas.getPixel(column, row); }

    private synchronized void incrementAndReport() { // only one thread should be allowed to access this method at a time
        counter++;
        int percentWritten = (int) ((counter / (double) numbPixels) * 100);
        if (lastReport != percentWritten && percentWritten % reportingInterval == 0) {
            System.out.println("INFO: canvas " + this + " has had " + percentWritten + "% of its pixels written to.");
            lastReport = percentWritten;
        }
    }

    /**
     * Reset the progress wrappers write counter.
     */
    public synchronized void reset() { counter = 0; lastReport = 0; }

    @Override
    public void writePixel(int column, int row, Color pixel) {
        incrementAndReport();
        internalCanvas.writePixel(column, row, pixel);
    }

    @Override
    public void writePixel(int column, int row, Function<? super Color, ? super Color> mappingFunction) {
        incrementAndReport();
        internalCanvas.writePixel(column, row, mappingFunction);
    }

    @Override
    public int getWidth() { return internalCanvas.getWidth(); }

    @Override
    public int getHeight() { return internalCanvas.getHeight(); }

    @Override
    public void writeAll(Function<? super Color, ? super Color> mappingFunction) { internalCanvas.writeAll(mappingFunction); }

}
