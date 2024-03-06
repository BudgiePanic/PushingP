package com.BudgiePanic.rendering.util.shape.composite;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.difference;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.union;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Cube;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

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

    @Test
    void testCompoundShapeRayIntersectionA() {
        var shape1 = new Sphere(Matrix4.identity());
        var shape2 = new Cube(Matrix4.identity());
        var shapeCompound = new CompoundShape(CompoundOperation.union, shape1, shape2, Matrix4.identity());
        var ray = new Ray(makePoint(0, 2, -5), makeVector(0, 0, 1));
        var result = shapeCompound.intersect(ray);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCompoundShapeRayIntersectionB() {
        var shape1 = new Sphere(Matrix4.identity());
        var shape2 = new Sphere(Transforms.identity().translate(0, 0, 0.5f).assemble());
        var shapeCompound = new CompoundShape(CompoundOperation.union, shape1, shape2, Matrix4.identity());
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        var result = shapeCompound.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(2, intersections.size());
        var intersection1 = intersections.getFirst();
        var intersection2 = intersections.getLast();

        assertEquals(0, FloatHelp.compareFloat(4, intersection1.a()), "4 == " + intersection1.a());
        assertEquals(shape1, intersection1.shape());

        assertEquals(0, FloatHelp.compareFloat(6.5f, intersection2.a()), "6.5 == " + intersection2.a());
        assertEquals(shape2, intersection2.shape());
    }

    @Test
    void testCompoundShapeDifferenceNested() {
        var compound = new CompoundShape(union, new Cube(Matrix4.identity()), new Cube(Matrix4.identity()), Matrix4.identity());
        var subtracted = new Cube(Transforms.identity().translate(0, 0, -1).assemble());
        var shape = new CompoundShape(difference, compound, subtracted, Matrix4.identity());
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        var result = shape.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(2, intersections.size());
    }

    @Test
    void testCompoundShapeGroupDifference() {
        var compound = new CompoundShape(union, new Cube(Matrix4.identity()), new Cube(Matrix4.identity()), Matrix4.identity());
        var subtracted = new Group(Matrix4.identity());
        subtracted.addShape(new Cube(Transforms.identity().translate(0, 0, -1).assemble()));
        var shape = new CompoundShape(difference, compound, subtracted, Matrix4.identity());
        var ray = new Ray(makePoint(0, 0, -5), makeVector(0, 0, 1));
        var result = shape.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(2, intersections.size());
    }

    @Disabled("disabled until issue #79 is resolved")
    @Test
    void testCompoundShapeDifferenceA() {
        // open shapes that do not enclose a volume
        var compound = new CompoundShape(difference, 
        new Cylinder(Matrix4.identity(), 1, 0, false), 
        new Sphere(Transforms.identity().scale(0.5f).translate(0, 0.5f, -1).assemble()), Matrix4.identity());
        var ray = new Ray(makePoint(0, 0.5f, -1), makeVector(0, 0.5f, 0.5f));
        var result = compound.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(1, intersections.size());
        assertEquals(compound.right, intersections.get(0).shape());
        var expected = -0.707107f;
        var actual = intersections.get(0).a();
        assertEquals(0, FloatHelp.compareFloat(expected, actual), "expected " + Float.toString(expected) + " actual " + Float.toString(actual));
    }

}
