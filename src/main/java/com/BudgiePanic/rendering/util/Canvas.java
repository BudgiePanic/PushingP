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

import java.util.function.Function;

/**
 * A canvas is a grid of Colors. It must support read and write operations.
 * Coordinates take the form of (x,y) integers.
 * The for each method not modify the canvas, it is a convience method to sample each pixel easily.
 *
 * @author BudgiePanic
 */
public interface Canvas extends Iterable<Color> {

    /**
     * Helper remapping function to clear the canvas.
     */
    final static Function<Color, Color> CLEAR = (color) -> {
        return Colors.black;
    };

    /**
     * Get a pixel from the canvas.
     * pixel (c0,r0) is the top left corner of the canvas.
     * pixel (cWidth, rHeight) is the bottom right corner of the canvase.
     * 
     * @param column
     *     The column of the desired pixel.
     * @param row
     *     The row of the desired pixel.
     * @return
     *     The pixel at (x:width:column, y:height:row)
     */
    Color getPixel(int column, int row);

    /**
     * Replace the pixel at (x,y) with pixel.
     *
     * @param column
     *     The column of the pixel being replaced.
     * @param row
     *     The row of the pixel being replaced.
     * @param pixel
     *     The new pixel to replace the current pixel
     */
    void writePixel(int column, int row, Color pixel);

    /**
     * Replace the pixel at (column:x, row:y) with a new pixel created by the mapping function.
     *
     * @param column
     *     The column of the pixel being replaced.
     * @param row
     *     The row of the pixel being replaced.
     * @param mappingFunction
     *     A function that will convert the pixel to a new pixel.
     */
    void writePixel(int column, int row, Function<? super Color, ? super Color> mappingFunction);

    /**
     * Get the number of columns in the canvas.
     * 
     * @return
     *     The number of columns in the canvas.
     */
    int getWidth();

    /**
     * Get the number of rows in the canvas.
     * 
     * @return
     *     The number of rows in the canvas.
     */
    int getHeight();

    /**
     * Mutates the canvas. Replaces each pixel in the canvas with the result of the remapping function.
     * If the position of each color is needed for the remap then please use a normal FOR loop.
     * 
     * @param mappingFunction
     *     A function that will convert each pixel to a new pixel.
     */
    void writeAll(Function<? super Color, ? super Color> mappingFunction);
}
