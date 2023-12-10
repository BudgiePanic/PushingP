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

    @Test
    void testOutOfBoundsGet() {
        // Test that illegal arg exception is thrown when going out of array bounds to help with BLAME.
        Canvas canvas = new ArrayCanvas(10, 20);
        // edge condition: column:width
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.getPixel(10, 19);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.getPixel(-1, 0);
        });
        // edge condition: row:height
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.getPixel(1, 20);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.getPixel(0, -1);
        });
        // edge condition, getters
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.getPixel(canvas.getWidth(), canvas.getHeight());
        });
        // pass conditions
        assertDoesNotThrow(()->{
            canvas.getPixel(0, 0);
            canvas.getPixel(9, 19);
            canvas.getPixel(canvas.getWidth() - 1, canvas.getHeight() - 1);
        });
    }

    @Test
    void testSize() {
        Canvas canvas = new ArrayCanvas(10, 20);
        assertEquals(10, canvas.getWidth());
        assertEquals(20, canvas.getHeight());

        assertEquals(9, canvas.getWidth() - 1);
        assertEquals(19, canvas.getHeight() - 1);
    }

    @Test 
    void testOutOfBoundsWrite() {
        Canvas canvas = new ArrayCanvas(10, 20);
        // invalid writes
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.writePixel(-1, 0, Colors.blue);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.writePixel(0, -1, Colors.blue);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.writePixel(0, 20, Colors.blue);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.writePixel(10, 0, Colors.blue);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.writePixel(canvas.getWidth(), 0, Colors.blue);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            canvas.writePixel(0, canvas.getWidth(), Colors.blue);
        });     
        // valid writes on boundary conditions
        assertDoesNotThrow(() -> {
            canvas.writePixel(0, 0, Colors.blue);
        });
        assertDoesNotThrow(()->{
            canvas.writePixel(canvas.getWidth() - 1, canvas.getHeight() - 1, Colors.blue);
        });
        assertDoesNotThrow(()->{
            canvas.writePixel(0, canvas.getHeight() - 1, Colors.blue);
        });
        assertDoesNotThrow(()->{
            canvas.writePixel(canvas.getWidth() - 1, 0, Colors.blue);
        });
    }

    // TODO posssible future tests:
    //      check that the iterator works on 1 by 1 canvases
    //      test the copy constructor, make sure modifications aren't reflected in the other canvas.

}
