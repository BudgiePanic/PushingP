package com.BudgiePanic.rendering.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Color;

/**
 * Canvas File IO tests. PPM format.
 * 
 * @author BudgiePanic
 */
public class CanvasWriterTest {
    
    @Test
    void testPPMHeaderConstruction(){
        var canvas = new ArrayCanvas(5, 3);
        var outputString = CanvasWriter.canvasToPPMString(canvas);
        assertEquals("P3", outputString.get(0));
        assertEquals("5 3", outputString.get(1)); // width:5 | height:3
        assertEquals("255", outputString.get(2)); // color range: [0 - 255] 256 possible values
    }

    @Test 
    void testPPMPixelWrite() {
        var canvas = new ArrayCanvas(5, 3);
        canvas.writePixel(0, 0, new Color(1.5f, 0f, 0f));
        canvas.writePixel(2, 1, new Color(0f, 0.5f, 0f));
        canvas.writePixel(4, 2, new Color(-0.5f, 0f, 1f));
        var outputString = CanvasWriter.canvasToPPMString(canvas);
        assertEquals("255 0 0 0 0 0 0 0 0 0 0 0 0 0 0", outputString.get(3));
        assertEquals("0 0 0 0 0 0 0 128 0 0 0 0 0 0 0", outputString.get(4));
        assertEquals("0 0 0 0 0 0 0 0 0 0 0 0 0 0 255", outputString.get(5));
    }

    @Test
    void testPPMPixelWriteTwo() {
        var canvas = new ArrayCanvas(10, 2);
        final Color c = new Color(1f, 0.8f, 0.6f);
        canvas.writeAll((oldColor) -> {
            return c;
        });
        var lines = CanvasWriter.canvasToPPMString(canvas);
        assertEquals("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204​", lines.get(3));
        assertTrue(lines.get(3).length() <= 70);
        assertEquals("153 255 204 153 255 204 153 255 204 153 255 204 153​", lines.get(4));
        assertTrue(lines.get(4).length() <= 70);
        assertEquals("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204​", lines.get(5));
        assertTrue(lines.get(5).length() <= 70);
        assertEquals("153 255 204 153 255 204 153 255 204 153 255 204 153​", lines.get(6));
        assertTrue(lines.get(6).length() <= 70);
    }

    @Test
    void testPPMnewLineTerminate() {
        // depending on how File IO works, this unit test might not be needed.
        var canvas = new ArrayCanvas(5, 3);
        var lines = CanvasWriter.canvasToPPMString(canvas);
        assertTrue(lines.get(lines.size() - 1).length() == 1);
        assertEquals('\n', lines.get(lines.size() - 1).charAt(0));
    }
}
