package com.BudgiePanic.rendering.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.ArrayCanvas;

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

}
