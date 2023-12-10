package com.BudgiePanic.rendering.io;

import java.util.LinkedList;
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
    
    private CanvasWriter() {}

    /**
     * Converts a canvas into lines of text, following the PPM format.
     * The lines can be written to a file to viewing.
     * 
     * @param canvas
     *     The canvas to convert to a PPM compliant String.
     * @return
     *     Lines of text representing the canvas in PPM format.
     */
    static List<String> canvasToPPMString(Canvas canvas) {
        var header = buildPPMHeader(canvas);
        var pixels = writePixelsToPPM(canvas);
        List<String> result = new LinkedList<>();
        result.addAll(header);
        result.addAll(pixels);
        return result;
    }

    private static List<String> buildPPMHeader(Canvas canvas) {
        final String magic = "P3";
        String widthHeight = String.format("%d %d", canvas.getWidth(), canvas.getHeight());
        final String maxColorValue = "255";
        return List.of(magic, widthHeight, maxColorValue);
    }

    /**
     * Writes a single color from a pixel to a string.
     * 
     * @param builder
     *     The string builder for a row of pixels
     * @param color
     *     Single color from one pixel
     * @param lines
     *     All lines of pixels
     * @return
     *     Builder, if the row has room for more colors.
     *     If the row has reached maximum length, the string builder is written to lines and a new SB is returned. 
     */
    private static StringBuilder writeColor(StringBuilder builder, float color, List<String> lines) {
        final float min = 0f;
        final float max = 1f;
        final float scale = 255f;
        final int maxLineLength = 70;
        int colorDiscrete = (int) (scale * Math.clamp(color, min, max));
        var strColor = String.format(" %d", colorDiscrete);
        var size = strColor.length();
        if ((builder.length() - 1) + size > maxLineLength) { // (length - 1) because we will always remove the space at position 0
            builder.deleteCharAt(0); // remove space at position 0
            lines.add(builder.toString());
            return new StringBuilder();
        } else {
            builder.append(strColor);
            return builder;
        }
    }

    /**
     * Clamps color values to be between 0f and 1f then scales by 255.
     * Naive implementation.
     * 
     * @param canvas
     * @return
     */
    private static List<String> writePixelsToPPM(Canvas canvas) {
        List<String> result = new LinkedList<>();
        for (int row = 0; row < canvas.getHeight(); row++) {
            StringBuilder rowPixels = new StringBuilder();
            for (int column = 0; column < canvas.getWidth(); column++) {
                var pixel = canvas.getPixel(column, row);
                rowPixels = writeColor(rowPixels, pixel.getRed(), result);
                rowPixels = writeColor(rowPixels, pixel.getGreen(), result);
                rowPixels = writeColor(rowPixels, pixel.getBlue(), result);  
            }
            rowPixels.deleteCharAt(0); // remove space at position 0
            result.add(rowPixels.toString());
        }
        return result;
    } 
}
