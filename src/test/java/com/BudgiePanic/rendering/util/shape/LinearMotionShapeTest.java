package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class LinearMotionShapeTest {
    @Test
    void testBounds() {
        var shape = new LinearMotionShape(Matrix4.identity(), new Cube(Matrix4.identity()), Directions.left);
        var bounds = shape.bounds();
        var expected = new BoundingBox(makePoint(-1, -1, -1), makePoint(1, 1, 1));
        assertEquals(expected.minimum(), bounds.minimum());
        assertEquals(expected.maximum(), bounds.maximum());
    }

    @Test
    void testLocalIntersect() {
        var shape = new LinearMotionShape(Matrix4.identity(), new Cube(Matrix4.identity()), Directions.left);
        var tests = List.of(
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 1.0), 2.0),
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 0.5), 2.0),
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 1.0), 2.0),
            new Pair<>(new Ray(makePoint(0, 0, -2), Directions.forward, 1.1), 0.0)
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
        var shape = new LinearMotionShape(Matrix4.identity(), new Plane(Transforms.identity().rotateX(AngleHelp.toRadians(-90f)).assemble()), Directions.forward);
        var tests = List.of(
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 0.0), 1.0),
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 0.5), 1.5),
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 1.0), 2.0),
            new Pair<>(new Ray(makePoint(0, 0, -1), Directions.forward, 1.1), 2.1)
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
    void testLinearMotionShapeBounds() {
        // check that the AABB returned by this LMS when the inner shape is transformed is correct.
        var shape = new LinearMotionShape(Matrix4.identity(), new Sphere(Transforms.identity().translate(0, 1, 0).assemble()), Directions.right);
        var result = shape.bounds();
        var expected = new BoundingBox(makePoint(-1, 0, -1), makePoint(1, 2, 1));
        assertEquals(expected, result);
    }

    @Test
    void testLinearMotionShapeBoundsA() {
        // check that the AABB from the LMS is correct when the end time is set
        var shape = new LinearMotionShape(Matrix4.identity(), new Sphere(Transforms.identity().translate(0, 1, 0).assemble()), Directions.right);
        shape.setMotionEndTime(Optional.of(1.0));
        var result = shape.bounds();
        var expected = new BoundingBox(makePoint(-1, 0, -1), makePoint(2, 2, 1));
        assertEquals(expected, result);
        
        shape.setMotionEndTime(Optional.empty());
        result = shape.bounds();
        expected = new BoundingBox(makePoint(-1, 0, -1), makePoint(1, 2, 1));
        assertEquals(expected, result);
    }

    @Test
    void testSetMotionEndTime() {
        var shape = new LinearMotionShape(Matrix4.identity(), new Cube(Matrix4.identity()), Directions.right);
        shape.setMotionEndTime(Optional.of(1.0));
        var bounds = shape.bounds();
        var expected = new BoundingBox(makePoint(-1, -1, -1), makePoint(2, 1, 1));
        assertEquals(expected.minimum(), bounds.minimum());
        assertEquals(expected.maximum(), bounds.maximum());
    }

    @Test
    void testMotionShapeIntersection() {
        var ray = new Ray(makePoint(0, 10, -6), makeVector(-0.005778f, -0.789307f, 0.613971f), 0.0);
        
        var concreteShape = new Sphere(Transforms.identity().translate(0, 1, 0).assemble());
        var sanity = concreteShape.intersect(ray);
        assertTrue(sanity.isPresent());

        Shape movingShape = new LinearMotionShape(Matrix4.identity(), new Sphere(Transforms.identity().translate(0, 1, 0).assemble()), Directions.right.multiply(100));
        var result = movingShape.intersect(ray);
        assertTrue(result.isPresent());
    }

    @Test
    void testMotionShapeParent() {
        // make sure the motion shape sets itself as the parent of the child shape
        var shape = new LinearMotionShape(Matrix4.identity(), new Sphere(Transforms.identity().assemble()), Directions.right);
        assertEquals(shape, shape.shape.parent().get());
    }

    @Test
    void testMotionShapeDivideA() {
        var shape = new Sphere(Transforms.identity().assemble());
        var motionShape = new LinearMotionShape(Matrix4.identity(), shape, Directions.right);
        var result = motionShape.divide(1);
        assertEquals(motionShape, result);
        assertEquals(shape, motionShape.shape);
    }

    @Test
    void testMotionShapeDivideB() {
        // check that motion shape asks its child shape to also divide
        var shape1 = new Sphere(Transforms.identity().assemble());
        var shape2 = new Sphere(Transforms.identity().translate(2.1, 0, 0).assemble());
        var group = new Group(Matrix4.identity());
        group.addShape(shape1);
        group.addShape(shape2);
        var motionShape = new LinearMotionShape(Matrix4.identity(), group, Directions.right);
        var result = motionShape.divide(1);
        assertEquals(2, group.children().size());
        assertTrue(group.children().get(0) instanceof Group);
        assertTrue(group.children().get(1) instanceof Group);
    }

}
