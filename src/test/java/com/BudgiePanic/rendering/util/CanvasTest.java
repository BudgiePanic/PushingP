package com.BudgiePanic.rendering.util;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CanvasTest {

    @Test
    void testCanvasSizeGetters(){
        Canvas canvas = new ArrayCanvas(10, 20);
        assertEquals(10, canvas.getWidth());
        assertEquals(20, canvas.getHeight());
        canvas.forEach((color)->{
            assertTrue(compareFloat(color.getRed(), 0f) == 0);
            assertTrue(compareFloat(color.getGreen(), 0f) == 0);
            assertTrue(compareFloat(color.getBlue(), 0f) == 0);
        });
    }

    @Test
    void testCanvasWritting() {
        Canvas canvas = new ArrayCanvas(10, 20);
        canvas.writePixel(2, 3, Colors.red);
        var actual = canvas.getPixel(2, 3);
        var expected = new Color(1.0f, 0f, 0f);
        assertEquals(expected, actual);
    }

    @Test
    void testRemapSingle(){
        Canvas canvas = new ArrayCanvas(10, 20);
        canvas.writePixel(2, 3, (currentColor) -> {
            currentColor.x = 1.0f;
            return currentColor;
        });
        var result = canvas.getPixel(2,3);
        var expected = Colors.red;
        assertEquals(expected, result);
    }

    @Test
    void testRemapAll(){
        // try to make all the pixels white
        Canvas canvas = new ArrayCanvas(10, 20);
        canvas.writeAll((currrentColor) -> {
            return Colors.white;
        });
        canvas.forEach((color)->{
            assertTrue(compareFloat(color.getRed(), 1f) == 0);
            assertTrue(compareFloat(color.getGreen(), 1f) == 0);
            assertTrue(compareFloat(color.getBlue(), 1f) == 0);
        });
        // Try make all the pixels black
        canvas.writeAll(Canvas.CLEAR);
        canvas.forEach((color)->{
            assertTrue(compareFloat(color.getRed(), 0f) == 0);
            assertTrue(compareFloat(color.getGreen(), 0f) == 0);
            assertTrue(compareFloat(color.getBlue(), 0f) == 0);
        });
    }

}
