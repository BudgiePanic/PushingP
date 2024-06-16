package com.BudgiePanic.rendering.util.raster;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;

public class LineDrawerTest {

    static final Color lineColor = Colors.white;
    static final Color blankColor = Colors.black;

    static Canvas horizontalCanvas() { return new ArrayCanvas(6, 2); }

    static Canvas vericalCanvas() { return new ArrayCanvas(2, 6); }

    static void testHarness(final int[] linePixels, final int[] blankPixels, final Canvas canvas) {
        for (int i = 0; i < linePixels.length - 1; i += 2) {
            int x = linePixels[i];
            int y = linePixels[i + 1];
            assertEquals(lineColor, canvas.getPixel(x, y));
        }
        for (int i = 0; i < blankPixels.length - 1; i += 2) {
            int x = blankPixels[i];
            int y = blankPixels[i + 1];
            assertEquals(blankColor, canvas.getPixel(x, y));
        }
    }

    @Test
    void testDrawLineA() {
        // A flat horizontal line
        var canvas = horizontalCanvas();
        LineDrawer.drawLine(0, 0, 5, 0, canvas, lineColor);
        int[] expectedLine = new int[] {0,0, 1,0, 2,0, 3,0, 4,0, 5,0};
        int[] expectedBlank = new int[] {0,1, 1,1, 2,1, 3,1, 4,1, 5,1};
        testHarness(expectedLine, expectedBlank, canvas);
    }

    @Test
    void testDrawLineB() {
        // A slightly upwards line
        var canvas = horizontalCanvas();
        LineDrawer.drawLine(0, 0, 5, 1, canvas, lineColor);
        int[] expectedLine = new int[] {0,0, 1,0, 2,0, 3,1, 4,1, 5,1};
        int[] expectedBlank = new int[] {0,1, 1,1, 2,1, 3,0, 4,0, 5,0};
        testHarness(expectedLine, expectedBlank, canvas);
    }

    @Test
    void testDrawLineBA() {
        // A slightly upwards line backwards line
        var canvas = horizontalCanvas();
        LineDrawer.drawLine(5, 1, 0, 0, canvas, lineColor);
        int[] expectedLine = new int[] {0,0, 1,0, 2,0, 3,1, 4,1, 5,1};
        int[] expectedBlank = new int[] {0,1, 1,1, 2,1, 3,0, 4,0, 5,0};
        testHarness(expectedLine, expectedBlank, canvas);
    }

    @Test
    void testDrawLineC() {
        // A slightly downwards line
        var canvas = horizontalCanvas();
        LineDrawer.drawLine(0, 1, 5, 0, canvas, lineColor);
        int[] expectedLine = new int[] {0,1, 1,1, 2,1, 3,0, 4,0, 5,0};
        int[] expectedBlank = new int[] {0,0, 1,0, 2,0, 3,1, 4,1, 5,1};
        testHarness(expectedLine, expectedBlank, canvas);
    }

    @Test
    void testDrawLineD() {
        // A vertical line
        var canvas = vericalCanvas();
        LineDrawer.drawLine(0, 0, 0, 5, canvas, lineColor);
        int[] expectedLine = new int[] {0,0, 0,1, 0,2, 0,3, 0,4, 0,5};
        int[] expectedBlank = new int[] {1,0, 1,1, 1,2, 1,3, 1,4, 1,5};
        testHarness(expectedLine, expectedBlank, canvas);
    }

    @Test
    void testDrawLineE() {
        // A vertical rightward line
        var canvas = vericalCanvas();
        LineDrawer.drawLine(0, 0, 1, 5, canvas, lineColor);
        int[] expectedLine = new int[] {0,0, 0,1, 0,2, 1,3, 1,4, 1,5};
        int[] expectedBlank = new int[] {1,0, 1,1, 1,2, 0,3, 0,4, 0,5};
        testHarness(expectedLine, expectedBlank, canvas);
    }

    @Test
    void testDrawLineF() {
        // A vertical leftward line
        var canvas = vericalCanvas();
        LineDrawer.drawLine(1, 0, 0, 5, canvas, lineColor);
        int[] expectedLine = new int[] {1,0, 1,1, 1,2, 0,3, 0,4, 0,5};
        int[] expectedBlank = new int[] {0,0, 0,1, 0,2, 1,3, 1,4, 1,5};
        testHarness(expectedLine, expectedBlank, canvas);
    }
}
