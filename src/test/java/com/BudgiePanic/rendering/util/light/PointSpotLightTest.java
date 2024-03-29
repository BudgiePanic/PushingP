package com.BudgiePanic.rendering.util.light;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.AngleHelp.toRadians;

public class PointSpotLightTest {

    @Test
    void testIntensityAt() {
        var light = new PointSpotLight(makePoint(0, 1, 0), Colors.white, Directions.down, toRadians(45), toRadians(45));
        var world = new World();
        // when the inner angle == the outer angle, results should be either 1f or 0f
        // make several points inside and outside the cone and check their intensity
        world.addLight(light);
        var tests = List.of(
            new Pair<>(makePoint(0, 0, 0), 1f),
            new Pair<>(makePoint(0, 0.5f, 0), 1f),
            new Pair<>(makePoint(0.1f, 0, 0), 1f),
            new Pair<>(makePoint(0.9f, 0, 0), 1f),
            new Pair<>(makePoint(1f, 0, 0), 1f),
            new Pair<>(makePoint(-0.5f, 0, 0), 1f),
            new Pair<>(makePoint(0, -1f, 0), 1f),
            new Pair<>(makePoint(0, 1.1f, 0), 0f),
            new Pair<>(makePoint(1.1f, 0, 0), 0f),
            new Pair<>(makePoint(-1.1f, 0, 0), 0f)
        );
        for (var test : tests) {
            var point = test.a();
            var expected = test.b();
            var result = light.intensityAt(point, world, 0);
            assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " result " + result);
        }
    }

    @Test
    void testIntensityAtA() {
        // when the inner angle < outer angle
        // make several points inside the cone and check their intensity
        // close to the center should be in the inner cone and have 1f intensity
        // beyond the inner angle should have non 1f but greater than 0f intensity
        // put the light in pointlight mode
        var light = new PointSpotLight(makePoint(0, 1, 0), Colors.white, Directions.down, toRadians(22.5f), toRadians(45));
        var world = new World();
        // when the inner angle == the outer angle, results should be either 1f or 0f
        // make several points inside and outside the cone and check their intensity
        world.addLight(light);
        var tests = List.of(
            new Pair<>(makePoint(0, 0, 0), 1f),
            new Pair<>(makePoint(0, 0.5f, 0), 1f),
            new Pair<>(makePoint(0.41f, 0, 0), 1f),
            new Pair<>(makePoint(0, 0, -0.41f), 1f)
        );
        for (var test : tests) {
            var point = test.a();
            var expected = test.b();
            var result = light.intensityAt(point, world, 0);
            assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " result " + result);
        }
        var point = makePoint(0.5f,0,0);
        var result = light.intensityAt(point, world, 0);
        assertTrue(FloatHelp.compareFloat(1f, result) == 1, "result " + result);
        assertTrue(FloatHelp.compareFloat(0, result) == -1, "result " + result);
    }

    @Test
    void testIntensityAtB() {
        // make the inner angle = 0
        // then all points within the cone should be LERP
        var light = new PointSpotLight(makePoint(0, 1, 0), Colors.white, Directions.down, 0, toRadians(45));
        var world = new World();
        var point = makePoint(0.41421f, 0, 0);
        var expected = 0.5f;
        var result = light.intensityAt(point, world, 0);
        assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " result " + result);
        light.intensityAt(makePoint(0.55f, 0, 0), world, 0);
        light.intensityAt(makePoint(0.35f, 0, 0), world, 0);
    }
}
