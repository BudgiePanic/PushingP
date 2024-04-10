package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CanvasResamplerTest {
    @Test
    void testAverageColorAt() {
        assertDoesNotThrow(() -> {
            var resampler = new CanvasResampler(new ArrayCanvas(16, 16), 2);
            resampler.canvas.writeAll(c -> Colors.red);
            resampler.averageColorAt(0, 0);
            resampler.averageColorAt(2, 2);
        });
    }

    @Test
    void testDownSample() {
        // happy path
        var resampler = new CanvasResampler(new ArrayCanvas(16, 16), 4);
        resampler.canvas.writeAll(c -> Colors.red);
        var result = resampler.downSample();
        assertEquals(4, result.getWidth());
        assertEquals(4, result.getHeight());
        result.forEach(c -> {
            assertEquals(Colors.red, c);
        });
        // unhappy path
        resampler = new CanvasResampler(new ArrayCanvas(17, 19), 4);
        resampler.canvas.writeAll(c -> Colors.red);
        result = resampler.downSample();
        assertEquals(4, result.getWidth());
        assertEquals(5, result.getHeight());
        // not all pixels will be red due to kernel overflow falling back to default pixel (black)
        // NOTE: if resampler implements nearest neighbor color on overflow, then this test will become invalid.
        int maxR = result.getHeight() - 1;
        for (int c = 0; c < result.getWidth(); c++) {
            var color = result.getPixel(c, maxR);
            assertEquals(Colors.red.multiply(0.75f), color);
        }
        for (int r = 0; r < maxR; r++) {
            for (int c = 0; c < 4; c++) {
                assertEquals(Colors.red, result.getPixel(c, r));
            }
        }
    }

    @Test
    void testMakeSmallerCanvas() {
        // happy path
        var resampler = new CanvasResampler(new ArrayCanvas(16, 16), 4);
        var result = resampler.makeSmallerCanvas(0, 0);
        assertEquals(16/4, result.getHeight());
        assertEquals(16/4, result.getWidth());
        // other case
        resampler = new CanvasResampler(new ArrayCanvas(18, 18), 4);
        result = resampler.makeSmallerCanvas(2, 2);
        assertEquals(20/4, result.getHeight());
        assertEquals(20/4, result.getWidth());
    }
}
