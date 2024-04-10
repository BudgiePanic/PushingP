package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Triangle;
import com.BudgiePanic.rendering.util.transform.View;

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
    void testDynamicSamplerOffsets() {
        int[] depths = {1,2,3,4,5};
        float[] expecteds = {0.5f, 0.25f, 0.125f, 0.0625f, 0.03125f};
        for (int i = 0; i < depths.length; i++) {
            var depth = depths[i];
            var expected = expecteds[i];
            var result = SuperSamplingCamera.DynamicSampler.offset(depth);
            assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " result " + result);
        }
    }

    @Disabled("Requires investigation")
    @Test
    void testDynamicSampler() {
        var camera = new SuperSamplingCamera(
            new PinHoleCamera(100, 100, toRadians(90f), View.makeViewMatrix(makePoint(0, 0, -1), makePoint(0, 0, 0), Directions.up)),
            SuperSamplingCamera.dynamicCornerGrid);
        World world = new World();
        world.addLight(new PointLight(makePoint(0, 0, -1), Colors.white));
        // triangle covers half the screen diagonally
        world.addShape(new Triangle(makePoint(-1, 1, 0), makePoint(1, -1, 0), makePoint(-1, -1, 0), Material.color(Colors.white).setSpecular(0).setDiffuse(0).setAmbient(1)));
        boolean shouldSaveToFile = false;
        if (shouldSaveToFile) {
            CanvasWriter.saveImageToFile(camera.takePicture(world), "test.ppm"); 
        }
        var result = camera.pixelExposureAt(world, 0, 0);
        // The result should be about half black and half white because the triangle is covering half the pixel.
        var expected = Colors.white.multiply(0.5f);
        // only the rotated grid passes this currently.
        assertEquals(expected, result);
    }
}
