/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.util;

/**
 * The canvas resampler downsamples a higher resolution canvas to a lower resolution canvas by averaging neighboring pixels together.
 * 
 * @author BudgiePanic
 */
public class CanvasResampler {
    
    /**
     * Super resolution image
     */
    protected final Canvas canvas;

    /**
     * The downsampling factor
     */
    protected final int factor;

    /**
     * Create a new canvas resampler
     * @param image
     *   A high resolution image
     * @param factor
     *   The downsampling factor. A factor of 2 will average pixels in 2 by 2 blocks.
     */
    public CanvasResampler(Canvas image, int factor) {
        if (factor < 2) { throw new IllegalArgumentException("minimum factor size is 2"); }
        this.factor = factor;
        this.canvas = image;
    }

    /**
     * Create the smaller canvas.
     * @param widthPadding
     * @param heightPadding
     * @return
     */
    protected Canvas makeSmallerCanvas(int widthPadding, int heightPadding) {
        final int newWidth = (canvas.getWidth() + widthPadding) / factor;
        final int newHeight = (canvas.getHeight() + heightPadding) / factor;
        return new ArrayCanvas(newWidth, newHeight);
    }

    /**
     * Get the color at [row, col] in the super resolution canvas, returns a default color if requesting an out of bounds pixel.
     * @param row
     * @param col
     * @return
     */
    private Color getColorAt(int row, int col) {    
        if (row >= canvas.getHeight() || col >= canvas.getWidth()) {
            // you could make it return the nearest neighbor pixel color?
            return Colors.black;
        }
        return canvas.getPixel(col, row);
    }

    /**
     * Gets the averaged color for region [col, row] to [col + factor, row + factor]
     * @param col
     * @param row
     * @return
     */
    protected Color averageColorAt(int col, int row) {
        double red = 0, green = 0, blue = 0;
        for (int r = row; r < row + factor; r++) {
            for (int c = col; c < col + factor; c++) {
                final var color = getColorAt(r, c);
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
            }
        }
        final double numbColor = (factor * factor); 
        return new Color(red / numbColor, green / numbColor, blue / numbColor);
    }

    /**
     * Create the down sampled image.
     * @return
     */
    public Canvas downSample() {
        final int widthRemainer = canvas.getWidth() % factor;
        final int heightRemainer = canvas.getHeight() % factor;
        final boolean widthNeedsPadding = widthRemainer != 0;
        final boolean heightNeedsPadding = heightRemainer != 0;
        if (heightNeedsPadding || widthNeedsPadding) {
            // padding pixels will be black - could also re-use the same color as the neighbor pixel
            System.out.println("WARN: adding padding to " + canvas);
        }
        Canvas result = makeSmallerCanvas(widthRemainer, heightRemainer);
        for (int row = 0; row < result.getHeight() * factor; row += factor) {
            for (int col = 0; col < result.getWidth() * factor; col += factor) {
                final var color = averageColorAt(col, row);
                result.writePixel(col / factor, row / factor, color);
            }
        }
        return result;
    }

}
