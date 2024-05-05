package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.composite.CompoundOperation;
import com.BudgiePanic.rendering.util.shape.composite.CompoundShape;
import com.BudgiePanic.rendering.util.shape.composite.Group;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class BoundingBoxTest {

    /**
     * BOUNDING BOX FUNCTIONALITY TESTS
     */

    @Test
    void testBoundingBoxContains() {
        var box = new BoundingBox(makePoint(-10, -10, -10), makePoint(10, 10, 10));
        // simple case pass
        assertTrue(box.contains(makePoint(0, 0, 0)));
        assertTrue(box.contains(makePoint(-5, -5, -5)));
        assertTrue(box.contains(makePoint(6, 6, 6)));
        assertTrue(box.contains(makePoint(-2, 3, 9)));
        assertTrue(box.contains(makePoint(10, 10, 10)));
        assertTrue(box.contains(makePoint(-10, -10, -10)));
        assertFalse(box.contains(makePoint(10.0001f, 10, 10)));
        assertFalse(box.contains(makePoint(-10, -10, -10.0001f)));
        // simple case fail
        assertFalse(box.contains(makePoint(11, 0, 0)));
        assertFalse(box.contains(makePoint(0, -11, 0)));
        assertFalse(box.contains(makePoint(1, -3, -15)));
        // infinity case pass
        box = new BoundingBox(
            makePoint(Float.NEGATIVE_INFINITY, 0, Float.NEGATIVE_INFINITY),
            makePoint(Float.POSITIVE_INFINITY,0,Float.POSITIVE_INFINITY));
        assertTrue(box.contains(makePoint(0,0,0)));
        assertTrue(box.contains(makePoint(10,0,10)));
        assertTrue(box.contains(makePoint(-1000,0,-1000)));
        assertTrue(box.contains(makePoint(Float.POSITIVE_INFINITY,0,0)));
        assertTrue(box.contains(makePoint(Float.POSITIVE_INFINITY,0,Float.NEGATIVE_INFINITY)));
        // infinity case fail
        assertFalse(box.contains(makePoint(0, 1, 0)));
    }

    @Test
    void testBoundingBoxGrow() {
        // simple case 
        var box = new BoundingBox(makePoint(), makePoint());
        box = box.grow(makePoint(1, 1, 1));
        assertEquals(makePoint(0, 0, 0), box.minimum());
        assertEquals(makePoint(1, 1, 1), box.maximum());
        box = box.grow(makePoint(-1, -1, -1));
        assertEquals(makePoint(-1, -1, -1), box.minimum());
        assertEquals(makePoint(1, 1, 1), box.maximum());
        // infinity case 
        box = new BoundingBox(makePoint(), makePoint());
        box = box.grow(makePoint(Float.NEGATIVE_INFINITY, 0, Float.NEGATIVE_INFINITY));
        assertEquals(makePoint(0, 0, 0), box.maximum());
        assertEquals(makePoint(Float.NEGATIVE_INFINITY, 0, Float.NEGATIVE_INFINITY), box.minimum());
    }



    /*
     * LOCAL BOUNDING BOX TESTS
     */

    @Test
    void testSphereBoundingBox() {
        Sphere sphere = new Sphere(Matrix4.identity());
        BoundingBox result = sphere.bounds();
        BoundingBox expected = new BoundingBox(makePoint(-1,-1,-1), makePoint(1,1,1));
        assertEquals(expected, result);
    }

    @Test
    void testConeBoundingBox() {
        // infinite cone
        var cone = new Cone(Matrix4.identity());
        var result = cone.bounds();
        var expected = new BoundingBox(
            makePoint(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY),
            makePoint(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
        assertEquals(expected, result);
    }

    @Test
    void testConeBoundingBoxA() {
        // finite cone
        var cone = new Cone(Matrix4.identity(), 5.5f, -1);
        var result = cone.bounds();
        var expected = new BoundingBox(makePoint(-5.5f, -1, -5.5f), makePoint(5.5f, 5.5f, 5.5f));
        assertEquals(expected, result);
    }

    @Test
    void testConeBoundingBoxB() {
        var cone = new Cone(Matrix4.identity(), 3, -5);
        var result = cone.bounds();
        assertEquals(makePoint(-5,-5,-5), result.minimum());
        assertEquals(makePoint(5,3,5), result.maximum());
    }

    @Test
    void testCubeBoundingBox() {
        var cube = new Cube(Matrix4.identity());
        var result = cube.bounds();
        var expected = new BoundingBox(makePoint(-1, -1, -1), makePoint(1, 1, 1));
        assertEquals(expected, result);
    }

    @Test
    void testCylinderBoundingBox() {
        // infinite cylinder
        var cylinder = new Cylinder(Matrix4.identity());
        var result = cylinder.bounds();
        var expected = new BoundingBox(makePoint(-1, Float.NEGATIVE_INFINITY, -1), makePoint(1, Float.POSITIVE_INFINITY, 1));
        assertEquals(expected, result);
    }

    @Test
    void testCylinderBoundingBoxA() {
        // finite cylinder
        var cylinder = new Cylinder(Matrix4.identity(), 10, -5);
        var result = cylinder.bounds();
        var expected = new BoundingBox(makePoint(-1, -5, -1), makePoint(1, 10, 1));
        assertEquals(result, expected);
    }

    @Test
    void testPlaneBoundingBox() {
        var plane = new Plane(Matrix4.identity());
        var result = plane.bounds();
        var expected = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY, 0, Float.NEGATIVE_INFINITY), makePoint(Float.POSITIVE_INFINITY,0,Float.POSITIVE_INFINITY));
        assertEquals(expected, result);
    }

    /*
     * END OF LOCAL BOUNDING BOX TESTS
     */

    @Test
    void testLocalGroupBounds() {
        // all the bounds of shapes within the group are converted to group space
        // then convert the bounds to a single bounding box (by accepting the max of all the BBs and the minimums of the BBs)
        // the group should lazilly calculate its AABB and cache the result
        // if the Group is modified (added to or removed from), it has become dirty and needs to recalculate the AABB on the next call
        // rotations require all 8 points of the AABB to be transformed before searching for an encompassing AABB
        var group = new Group(Matrix4.identity());
        var sphereA = new Sphere(Transforms.identity().translate(2, 1, 0).assemble());
        var sphereB = new Sphere(Transforms.identity().scale(2, 2, 2).translate(0, -2, 0).assemble());
        group.addShape(sphereA);
        group.addShape(sphereB);
        var result = group.bounds();
        var expected = new BoundingBox(makePoint(-2,-4,-2), makePoint(3, 2, 2));
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsA() {
        // with an infinite plane
        var group = new Group(Matrix4.identity());
        var plane = new Plane(Transforms.identity().translate(0, -1, 0).assemble());
        var cube = new Cube(Transforms.identity().scale(2, 2, 2).assemble());
        group.addShape(plane);
        group.addShape(cube);
        var result = group.bounds();
        var expected = new BoundingBox(
            makePoint(Float.NEGATIVE_INFINITY,-2,Float.NEGATIVE_INFINITY),
            makePoint(Float.POSITIVE_INFINITY,2,Float.POSITIVE_INFINITY));
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsB() {
        // with an infinite cone
        var group = new Group(Matrix4.identity());
        var cone = new Cone(Matrix4.identity());
        group.addShape(cone);
        var result = group.bounds();
        var expected = new BoundingBox(
            makePoint(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY),
            makePoint(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY));
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsC() {
        // with an infinite cylinder
        var group = new Group(Matrix4.identity());
        var cylinder = new Cylinder(Transforms.identity().rotateZ(AngleHelp.toRadians(90)).assemble());
        group.addShape(cylinder);
        var result = group.bounds();
        var expected = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY,-1,-1), makePoint(Float.POSITIVE_INFINITY,1,1)); 
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsD() {
        // nested groups
          // 1 sphere
          // 1 cube
        var groupA = new Group(Matrix4.identity());
        var groupB = new Group(Transforms.identity().translate(2, 0, 0).assemble());
        var sphere = new Sphere(Matrix4.identity());
        var cube = new Cube(Matrix4.identity());
        groupA.addShape(sphere);
        groupB.addShape(cube);
        groupA.addShape(groupB);
        var result = groupA.bounds();
        var expected = new BoundingBox(makePoint(-1,-1,-1), makePoint(3,1,1)); 
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsE() {
        var group = new Group(Matrix4.identity());
        var cube = new Cube(Transforms.identity().rotateX(AngleHelp.toRadians(45f)).assemble());
        group.addShape(cube);
        var result = group.bounds();
        float sqrt2 = (float) Math.sqrt(2.0);
        var expectedPointMax = makePoint(1, sqrt2, sqrt2);
        var expectedPointMin = makePoint(-1, -sqrt2, -sqrt2);
        var expected = new BoundingBox(expectedPointMin, expectedPointMax); 
        assertEquals(expected, result);
    }

    //
    // BOOK TESTS
    // 
    @Test 
    void testBoundingBoxGrowA() {
        BoundingBox box = new BoundingBox(makePoint(0, 0, 0), makePoint());
        box = box.grow(makePoint(-5, 2, 0));
        box = box.grow(makePoint(7, 0, -3));
        assertEquals(makePoint(-5, 0, -3), box.minimum());
        assertEquals(makePoint(7, 2, 0), box.maximum());
    }

    @Test
    void testTriangleBoundingBox() {
        var shape = new Triangle(makePoint(-3, 7, 2), makePoint(6, 2, -4), makePoint(2, -1, -1));
        var result = shape.bounds();
        assertEquals(makePoint(-3, -1, -4), result.minimum());
        assertEquals(makePoint(6, 7, 2), result.maximum());
    }

    @Test
    void testBoundingBoxMerge() {
        var a = new BoundingBox(makePoint(-5,-2,0), makePoint(7,4,4));
        var b = new BoundingBox(makePoint(8, -7, -2), makePoint(14, 2, 8));
        var result = a.grow(b);
        assertEquals(makePoint(-5,-7,-2), result.minimum());
        assertEquals(makePoint(14,4,8), result.maximum());
    }

    @Test
    void testBoundingBoxContainsA() {
        var tests = List.of(
            new Pair<>(new BoundingBox(makePoint(5,-2,0), makePoint(11,4,7)), true),
            new Pair<>(new BoundingBox(makePoint(6,-1,1), makePoint(10,3,6)), true),
            new Pair<>(new BoundingBox(makePoint(4,-3,-1), makePoint(10,3,6)), false),
            new Pair<>(new BoundingBox(makePoint(6,-1,1), makePoint(12,5,8)), false)
        );
        final var box = new BoundingBox(makePoint(5,-2,0), makePoint(11, 4, 7));
        for (final var test : tests) {
            var testBox = test.a();
            var exepcted = test.b();
            assertEquals(exepcted, box.contains(testBox), test.toString() + " " + box);
        }
    }

    @Test
    void testBoundingBoxContainsB() {
        var tests = List.of(
            new Pair<>(makePoint(5, -2, 0), true),
            new Pair<>(makePoint(11, 4, 7), true),
            new Pair<>(makePoint(8, 1, 3), true),
            new Pair<>(makePoint(3, 0, 3), false),
            new Pair<>(makePoint(8, -4, 3), false),
            new Pair<>(makePoint(8, 1, -1), false),
            new Pair<>(makePoint(13, 1, 3), false),
            new Pair<>(makePoint(8, 5, 3), false),
            new Pair<>(makePoint(8, 1, 8), false)
        );
        var box = new BoundingBox(makePoint(5, -2, 0), makePoint(11, 4, 7));
        for (var test : tests) {
            var expected = test.b();
            var result = box.contains(test.a());
            assertEquals(expected, result, box.toString() + " " + test.a().toString());
        }
    }

    @Test
    void testBoundingBoxTransform() {
        var box = new BoundingBox(makePoint(-1, -1, -1), makePoint(1, 1, 1));
        var transform = Transforms.identity().rotateY(Math.PI / 4).rotateX(Math.PI / 4).assemble();
        var result = box.transform(transform);
        assertEquals(makePoint(-1.4142, -1.7071, -1.7071), result.minimum());
        assertEquals(makePoint(1.4142, 1.7071, 1.7071), result.maximum());
    }

    @Test
    void testTransformedShapePrimitiveBoundingBox() {
        var shape = new Sphere(Transforms.identity().scale(0.5, 2, 4).translate(1, -3, 5).assemble());
        var bounds = shape.bounds().transform(shape.transform());
        assertEquals(makePoint(0.5, -5, 1), bounds.minimum());
        assertEquals(makePoint(1.5, -1, 9), bounds.maximum());
    }

    @Test
    void testGroupBounds() {
        var sphere = new Sphere(Transforms.identity().scale(2).translate(2, 5, -3).assemble());
        var cylinder = new Cylinder(Transforms.identity().scale(0.5, 1, 0.5).translate(-4, -1, 4).assemble(), 2,-2);
        var group = new Group(Transforms.identity().assemble());
        group.addShape(sphere);
        group.addShape(cylinder);
        var bounds = group.bounds();
        assertEquals(makePoint(-4.5, -3, -5), bounds.minimum());
        assertEquals(makePoint(4, 7, 4.5), bounds.maximum());
    }

    @Test
    void testGroupBoundsA() {
        var sphere = new Sphere(Transforms.identity().scale(2).translate(2, 5, -3).assemble());
        var cylinder = new Cylinder(Transforms.identity().scale(0.5, 1, 0.5).translate(-4, -1, -4).assemble(), 2,-2);
        var group = new Group(Transforms.identity().assemble());
        group.addShape(sphere);
        group.addShape(cylinder);
        var bounds = group.bounds();
        assertEquals(makePoint(-4.5, -3, -5), bounds.minimum());
        assertEquals(makePoint(4, 7, -1), bounds.maximum());
    }

    @Test
    void testCSGShapeBounds() {
        var left = new Sphere(Transforms.identity().assemble());
        var right = new Sphere(Transforms.identity().translate(2, 3, 4).assemble());
        var shape = new CompoundShape(CompoundOperation.difference, left, right, Transforms.identity().assemble());
        var bounds = shape.bounds();
        assertEquals(makePoint(-1, -1, -1), bounds.minimum());
        assertEquals(makePoint(3, 4, 5), bounds.maximum());
    }

    @Test
    void testRayAABBIntersection() {
        var tests = List.of(
            new Pair<>(new Ray(makePoint(5, 0, 0), makeVector(-1, 0, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(-5, 0, 0), makeVector(1, 0, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(0.5, 0, 0), makeVector(0, -1, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(0.5, 0, 0), makeVector(0, 1, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(0.5, 0, 0), makeVector(0, 0, -1).normalize()), true),
            new Pair<>(new Ray(makePoint(0.5, 0, 0), makeVector(0, 0, 1).normalize()), true),
            new Pair<>(new Ray(makePoint(0, 0, 0), makeVector(0, 0, 1).normalize()), true),
            new Pair<>(new Ray(makePoint(-2, 0, 0), makeVector(2, 4, 6).normalize()), false),
            new Pair<>(new Ray(makePoint(0, -2, 0), makeVector(6, 2, 4).normalize()), false),
            new Pair<>(new Ray(makePoint(0, 0, -2), makeVector(4, 6, 2).normalize()), false),
            new Pair<>(new Ray(makePoint(2, 0, 2), makeVector(0, 0, -1).normalize()), false),
            new Pair<>(new Ray(makePoint(0, 2, 2), makeVector(0, -1, 0).normalize()), false),
            new Pair<>(new Ray(makePoint(2, 2, 0), makeVector(-1, 0, 0).normalize()), false)
        );
        var box = new BoundingBox(makePoint(-1, -1, -1), makePoint(1, 1, 1));
        for (var test : tests) {
            var exepcted = test.b();
            var result = box.intersect(test.a());
            assertEquals(exepcted, result);
        }
    }

    @Test
    void testRayAABBIntersectionA() {
        var tests = List.of(
            new Pair<>(new Ray(makePoint(15, 1, 2), makeVector(-1, 0, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(-5, -1, 4), makeVector(1, 0, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(7, 6, 5), makeVector(0, -1, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(9, -5, 6), makeVector(0, 1, 0).normalize()), true),
            new Pair<>(new Ray(makePoint(8, 2, 12), makeVector(0, 0, -1).normalize()), true),
            new Pair<>(new Ray(makePoint(6, 0, -5), makeVector(0, 0, 1).normalize()), true),
            new Pair<>(new Ray(makePoint(8, 1, 3.5), makeVector(0, 0, 1).normalize()), true),
            new Pair<>(new Ray(makePoint(9, -1, -8), makeVector(2, 4, 6).normalize()), false),
            new Pair<>(new Ray(makePoint(8, 3, -4), makeVector(6, 2, 4).normalize()), false),
            new Pair<>(new Ray(makePoint(9, -1, -2), makeVector(4, 6, 2).normalize()), false),
            new Pair<>(new Ray(makePoint(4, 0, 9), makeVector(0, 0, -1).normalize()), false),
            new Pair<>(new Ray(makePoint(8, 6, -1), makeVector(0, -1, 0).normalize()), false),
            new Pair<>(new Ray(makePoint(12, 5, 4), makeVector(-1, 0, 0).normalize()), false)
        );
        var box = new BoundingBox(makePoint(5, -2, 0), makePoint(11, 4, 7));
        for (var test : tests) {
            var exepcted = test.b();
            var result = box.intersect(test.a());
            assertEquals(exepcted, result);
        }
    }

    @Test
    void testBoundingBoxSplit() {
        var box = new BoundingBox(makePoint(-1, -4, -5), makePoint(9, 6, 5));
        Pair<BoundingBox, BoundingBox> split = box.split();
        var left = split.a();
        var right = split.b();
        assertEquals(makePoint(-1, -4, -5), left.minimum());
        assertEquals(makePoint(4, 6, 5), left.maximum());
        assertEquals(makePoint(4, -4, -5), right.minimum());
        assertEquals(makePoint(9, 6, 5), right.maximum());
    }

    @Test
    void testBoundingBoxSplitA() {
        var box = new BoundingBox(makePoint(-1, -2, -3), makePoint(9, 5.5, 3));
        Pair<BoundingBox, BoundingBox> split = box.split();
        var left = split.a();
        var right = split.b();
        assertEquals(makePoint(-1, -2, -3), left.minimum());
        assertEquals(makePoint(4, 5.5, 3), left.maximum());
        assertEquals(makePoint(4, -2, -3), right.minimum());
        assertEquals(makePoint(9, 5.5, 3), right.maximum());
    }

    @Test
    void testBoundingBoxSplitB() {
        var box = new BoundingBox(makePoint(-1, -2, -3), makePoint(5, 8, 3));
        Pair<BoundingBox, BoundingBox> split = box.split();
        var left = split.a();
        var right = split.b();
        assertEquals(makePoint(-1, -2, -3), left.minimum());
        assertEquals(makePoint(5, 3, 3), left.maximum());
        assertEquals(makePoint(-1, 3, -3), right.minimum());
        assertEquals(makePoint(5, 8, 3), right.maximum());
    }

    @Test
    void testBoundingBoxSplitC() {
        var box = new BoundingBox(makePoint(-1, -2, -3), makePoint(5, 3, 7));
        Pair<BoundingBox, BoundingBox> split = box.split();
        var left = split.a();
        var right = split.b();
        assertEquals(makePoint(-1, -2, -3), left.minimum());
        assertEquals(makePoint(5, 3, 2), left.maximum());
        assertEquals(makePoint(-1, -2, 2), right.minimum());
        assertEquals(makePoint(5, 3, 7), right.maximum());
    }

}
