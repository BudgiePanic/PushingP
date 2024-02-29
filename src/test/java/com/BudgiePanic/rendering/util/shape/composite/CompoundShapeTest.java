package com.BudgiePanic.rendering.util.shape.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Sphere;

public class CompoundShapeTest {

    @Test
    void testCompoundShapeProperties() {
        var shape1 = new Sphere(Matrix4.identity());
        var shape2 = new Cube(Matrix4.identity());
        var shapeCompound = new CompoundShape(CompoundOperation.union, shape1, shape2, Matrix4.identity());
        assertEquals(CompoundOperation.union, shapeCompound.operation());
        assertEquals(shape1, shapeCompound.left());
        assertEquals(shape2, shapeCompound.right());
        assertEquals(shapeCompound, shape1.parent().get());
        assertEquals(shapeCompound, shape2.parent().get());
    }

    @Test
    void testCompoundShapeIntersectFiltering() {
        var shape1 = new Sphere(Matrix4.identity());
        var shape2 = new Cube(Matrix4.identity());
        var tests = List.of(
            new Pair<>(CompoundOperation.union, new Pair<>(0, 3)),
            new Pair<>(CompoundOperation.intersect, new Pair<>(1, 2)),
            new Pair<>(CompoundOperation.difference, new Pair<>(0, 1))
        );
        var dummyIntersections = List.of(
            new Intersection(1f, shape1),
            new Intersection(2f, shape2),
            new Intersection(3f, shape1),
            new Intersection(4f, shape2)
        );
        for (var test: tests) {
            var cShape = new CompoundShape(test.a(), shape1, shape2, Matrix4.identity());
            var result = cShape.filter(dummyIntersections);
            assertEquals(2, result.size());
            var resultA = result.get(0);
            var resultB = result.get(1);
            assertEquals(dummyIntersections.get(test.b().a()), resultA);
            assertEquals(dummyIntersections.get(test.b().b()), resultB);
        }
        
    }

}
