package com.BudgiePanic.rendering.scene;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;

public class SuperSamplingCameraTest {
    @Test
    void gridOutput() {
        assertDoesNotThrow(()->{
            var pattern = new SuperSamplingCamera.RotatedGrid(6);
            pattern.cachedPoints.size();
            // print pattern result to test output file for manual inspection of results
            // pattern.cachedPoints.forEach(p -> System.out.println(p.a()));
            // System.out.println();
            // pattern.cachedPoints.forEach(p -> System.out.println(p.b()));
        });
    }

    @Test
    void testRotateGridRawPointsB() {
        var result = SuperSamplingCamera.RotatedGrid.rawPoints(4);
        result.forEach(p -> {
            assertTrue(p.a() >= 0, p.toString());
            assertTrue(p.a() <= 1, p.toString());
            assertTrue(p.b() >= 0, p.toString());
            assertTrue(p.b() <= 1, p.toString());
        });
    }

    @Test
    void testRotateGridRawPointsA() {
        var result = SuperSamplingCamera.RotatedGrid.rawPoints(16);
        result.forEach(p -> {
            assertTrue(p.a() >= 0, p.toString());
            assertTrue(p.a() <= 1, p.toString());
            assertTrue(p.b() >= 0, p.toString());
            assertTrue(p.b() <= 1, p.toString());
        });
    }

    @Test
    void testRotateGridRawPoints() {
        var result = SuperSamplingCamera.RotatedGrid.rawPoints(64);
        result.forEach(p -> {
            assertTrue(p.a() >= 0, p.toString());
            assertTrue(p.a() <= 1, p.toString());
            assertTrue(p.b() >= 0, p.toString());
            assertTrue(p.b() <= 1, p.toString());
        });
    }

    @Test
    void testDynamicSampler() {
        int[] depths = {1,2,3,4,5};
        float[] expecteds = {0.5f, 0.25f, 0.125f, 0.0625f, 0.03125f};
        for (int i = 0; i < depths.length; i++) {
            var depth = depths[i];
            var expected = expecteds[i];
            var result = SuperSamplingCamera.DynamicSampler.offset(depth);
            assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " result " + result);
        }
    }
}
