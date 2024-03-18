package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

public class TorusTest {
    @Test
    void testLocalIntersect() {
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), 1f, 0.25f);
        var ray = new Ray(makePoint(-2, 0, 0), Directions.right);
        var result = shape.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(4, intersections.size());
        assertEquals(0, FloatHelp.compareFloat(-1.25f, intersections.get(0).a()));
        assertEquals(0, FloatHelp.compareFloat(-0.75f, intersections.get(1).a()));
        assertEquals(0, FloatHelp.compareFloat(0.75f, intersections.get(2).a()));
        assertEquals(0, FloatHelp.compareFloat(1.25f, intersections.get(3).a()));
    }

    @Test
    void testLocalNormal() {
        var radius = 1f;
        var thickness = 0.25f;
        var shape = new Torus(Matrix4.identity(), Material.defaultMaterial(), radius, thickness);
        var tests = List.of(
            new Pair<>(makePoint(radius + thickness, 0, 0), Directions.right),
            new Pair<>(makePoint(0, radius + thickness, 0), Directions.up),
            new Pair<>(makePoint(radius, 0, thickness), Directions.forward)
        );
        for (var test : tests) {
            var point = test.a();
            var expected = test.b();
            var actual = shape.normal(point);
            assertEquals(expected, actual);
        }
    }
}
