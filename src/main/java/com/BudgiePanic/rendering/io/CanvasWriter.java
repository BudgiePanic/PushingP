package com.BudgiePanic.rendering.io;

import java.util.List;

import com.BudgiePanic.rendering.util.Canvas;

/**
 * Writes canvas information to a PPM image file for viewing.
 * PPM files can be viewed in GIMP. 
 * 
 * @see https://en.wikipedia.org/wiki/Netpbm
 * 
 * @author BudgiePanic
 */
public final class CanvasWriter {
    
    static List<String> canvasToPPMString(Canvas canvas) {
        var header = buildPPMHeader(canvas);
        return header;
    }

    private static List<String> buildPPMHeader(Canvas canvas) {
        final String magic = "P3";
        String widthHeight = String.format("%d %d", canvas.getWidth(), canvas.getHeight());
        final String maxColorValue = "255";
        return List.of(magic, widthHeight, maxColorValue);
    }
}
