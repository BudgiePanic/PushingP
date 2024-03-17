package com.BudgiePanic.rendering.util.light;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class AreaLightTest {

    @Test 
    void testConstructorProperties() {
        var light = new AreaLight(Colors.white, makePoint(0, 0, 0), makeVector(2, 0, 0), makeVector(0, 0, 1), 4, 2);
        assertEquals(8, light.resolution());
        assertEquals(makeVector(0.5f, 0, 0), light.uUnit());
        assertEquals(makeVector(0f, 0, 0.5f), light.vUnit());
        assertEquals(makePoint(1, 0, 0.5f), light.position());
    }

    @Test
    void testAreaLightSampling() {
        var light = new AreaLight(Colors.white, makePoint(), makeVector(2, 0, 0), makeVector(0,0,1), 4, 2);
        var tests = List.of(
            new Pair<>(makePoint(0.25f, 0, 0.25f), new Pair<>(0f, 0f)),
            new Pair<>(makePoint(0.75f, 0, 0.25f), new Pair<>(1f, 0f)),
            new Pair<>(makePoint(0.25f, 0, 0.75f), new Pair<>(0f, 1f)),
            new Pair<>(makePoint(1.25f, 0, 0.25f), new Pair<>(2f, 0f)),
            new Pair<>(makePoint(1.75f, 0, 0.75f), new Pair<>(3f, 1f))
        );
        for (var test : tests) {
            var u = test.b().a();
            var v = test.b().b();
            var expected = test.a();
            var actual = light.sample(u, v);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testIntensityAt() {
        var world = new World();
        var light = new AreaLight(Colors.white, makePoint(-0.5f, -0.5f, -5), makeVector(1, 0, 0), makeVector(0, 1, 0), 2, 2);
        var sphereA = new Sphere(Transforms.identity().assemble());
        var sphereB = new Sphere(Transforms.identity().scale(0.5f, 0.5f, 0.5f).assemble());
        world = new World();
        world.addLight(light);
        world.addShape(sphereA);
        world.addShape(sphereB);
        var tests = List.of(
            new Pair<>(makePoint(0, 0, 2), 0f),
            new Pair<>(makePoint(1, -1, 2), 0.25f),
            new Pair<>(makePoint(1.5f, 0, 2), 0.5f),
            new Pair<>(makePoint(1.25f, 1.25f, 3), 0.75f),
            new Pair<>(makePoint(0, 0, -2), 1.0f)
        );
        for (var test : tests) {
            var point = test.a();
            var expected = test.b();
            var actual = light.intensityAt(point, world);
            assertEquals(0, FloatHelp.compareFloat(expected, actual), "test: [" + test + "] expected: [" + expected + "] actual: [" + actual + "]");
        }
    }
}
