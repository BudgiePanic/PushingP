package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class MotionShapeTest {
    @Test
    void testBounds() {
        var shape = new MotionShape(Matrix4.identity(), new Cube(Matrix4.identity()), Directions.left);
        var bounds = shape.bounds();
        var expected = new BoundingBox(makePoint(-1, -1, -1), makePoint(1, 1, 1));
        assertEquals(expected.minimum(), bounds.minimum());
        assertEquals(expected.maximum(), bounds.maximum());
    }

    @Test
    void testLocalIntersect() {
        var shape = new MotionShape(Matrix4.identity(), new Cube(Matrix4.identity()), Directions.left);
        var tests = List.of(
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 0f), 2),
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 0.5f), 2),
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 1f), 2),
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 1.1f), 0)
        );
        for (var test : tests) {
            final var expectedIntersectionCount = test.b();
            final var ray = test.a();
            var result = shape.intersect(ray);
            if (expectedIntersectionCount == 0) {
                assertTrue(result.isEmpty());
                continue;
            } 
            assertTrue(result.isPresent());
            var intersections = result.get();
            assertEquals(expectedIntersectionCount, intersections.size());
        }
     }

    @Test
    void testLocalIntersectA() {
        var shape = new MotionShape(Matrix4.identity(), new Plane(Transforms.identity().rotateX(AngleHelp.toRadians(-90f)).assemble()), Directions.forward);
        var tests = List.of(
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 0f), 1f),
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 0.5f), 1.5f),
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 1f), 2f),
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 1.1f), 2.1f)
        );
        for (var test : tests) {
            final var expectedDistance = test.b();
            final var ray = test.a();
            var result = shape.intersect(ray);
            assertTrue(result.isPresent());
            var intersections = result.get();
            assertEquals(1, intersections.size());
            var intersection = intersections.getFirst();
            assertTrue(FloatHelp.compareFloat(expectedDistance, intersection.a()) == 0, "expected: " + expectedDistance + " actual: " + intersection.a());
        }
    }

    @Test 
    void testShadowsCastByMotionShape() {
        fail("test not implemented yet");
    }

    @Test
    void testSetMotionEndTime() {
        var shape = new MotionShape(Matrix4.identity(), new Cube(Matrix4.identity()), Directions.right);
        shape.setMotionEndTime(Optional.of(1f));
        var bounds = shape.bounds();
        var expected = new BoundingBox(makePoint(-1, -1, -1), makePoint(2, 1, 1));
        assertEquals(expected.minimum(), bounds.minimum());
        assertEquals(expected.maximum(), bounds.maximum());
    }
}
