package com.BudgiePanic.rendering.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Canvas implementation using vanilla arrays.
 * 
 * @author BudgiePanic
 */
public final class ArrayCanvas implements Canvas {

    /**
     * 2D array iterator.
     */
    private final class ArrayCanvasIterator implements Iterator<Color> {

        /**
         * Progress pointers
         */
        private int row = 0, column = 0;

        @Override
        public boolean hasNext() {
            return row < ArrayCanvas.this.getHeight() && column < ArrayCanvas.this.getWidth();
        }

        @Override
        public Color next() {
            if (!hasNext()) throw new NoSuchElementException();
            var color = ArrayCanvas.this.getPixel(column, row);
            column++;
            if (column > ArrayCanvas.this.getWidth() - 1) {
                column = 0;
                row++;
            }
            return color;
        }
    }

    /**
     * The color array of the canvas.
     */
    private Color[][] colors;

    /**
     * Create a new ArrayCanvas.
     * The minimum dimensions of the canvas is 1*1.
     * 
     * @param columns
     *     The WIDTH of the canvas in pixels.
     * @param rows
     *     The HEIGHT of the canvas in pixels.
     */
    public ArrayCanvas(int columns, int rows) {
        // precondition check: dimensions
        if (columns <= 0) throw new IllegalArgumentException("Invalid width for canvas ->" + columns);
        if (rows <= 0) throw new IllegalArgumentException("Invalid height for canvas ->" + rows);

        this.colors = new Color[columns][rows];
        writeAll(CLEAR);
    }

    /**
     * Shallow copy constructor.
     * The copy creates new arrays, but alias each color object in the original.
     * This should be OK as the color operations create new color objects.
     * However, cirectly modifying a color (i.e. color.x = 1) will mutate both canvases.
     * 
     * @param other
     *     The original canvas.
     */
    public ArrayCanvas(ArrayCanvas other){
        // make a new array
        this.colors = new Color[this.getWidth()][other.getHeight()];
        // write each color to the new arrays
        for (int row = 0; row < getWidth(); row++) {
            for (int column = 0; column < getWidth();  column++) {
                // use shallow copy because color methods don't modify the orginal color.
                var color = other.colors[column][row];
                this.colors[column][row] = color;
            }
        }
    }

    @Override
    public Iterator<Color> iterator() {
        return new ArrayCanvasIterator();
    }

    /**
     * Check for array boundary conditions. Throws a runtime exception if the provided position is invalid.
     *
     * @param column
     *     The column to check
     * @param row
     *     The row to check
     */
    private void boundsCheck(int column, int row) {
        if (column >= getWidth() || column < 0) throw new IllegalArgumentException("Invalid column for canvas of width " + getWidth() + " ->" + column);
        if (row >= getHeight() || row < 0) throw new IllegalArgumentException("Invalid row for canvas of height " + getHeight() + " ->" + row);
    } 

    @Override
    public Color getPixel(int column, int row) {
        // precondition checks: bounds
        boundsCheck(column, row);

        var pixel = colors[column][row];
        
        // post condition check
        assert pixel != null;

        return pixel;
    }

    @Override
    public void writePixel(int column, int row, Color pixel) {
        // precondition checks
        if (pixel == null) throw new IllegalArgumentException("cannot write null pixel to canvas");
        boundsCheck(column, row);

        colors[column][row] = pixel;
    }

    @Override
    public void writePixel(int column, int row, Function<? super Color, ? super Color> mappingFunction) {
        var currentColor = getPixel(column, row);
        var newColor = (Color) mappingFunction.apply(currentColor);
        if (newColor == null) throw new RuntimeException("write pixel remap function converted pixel to null");
        colors[column][row] = newColor;
    }

    @Override
    public int getWidth() {
        return colors.length;
    }

    @Override
    public int getHeight() {
        // Assumes the secondary array (the rows) are the same for every column.
        return colors[0].length;
    }

    @Override
    public void writeAll(Function<? super Color, ? super Color> mappingFunction) {
        // NOTE: not sure if there is a way to use the iterator here to simplify the code
        //       since we are mutating the arrays.
        for (int row = 0; row < getWidth(); row++) {
            for (int column = 0; column < getWidth();  column++) {
                var currentColor = colors[column][row];
                var newColor = (Color) mappingFunction.apply(currentColor);
                if (newColor == null) throw new RuntimeException("write pixel remap function converted pixel to null");
                colors[column][row] = newColor;
            }
        }
    }
    
}
