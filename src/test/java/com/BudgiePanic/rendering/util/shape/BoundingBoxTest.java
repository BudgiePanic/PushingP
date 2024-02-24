package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.AngleHelp;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
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
        box = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY, 0, Float.NEGATIVE_INFINITY), makePoint(Float.POSITIVE_INFINITY,0,Float.POSITIVE_INFINITY));
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
        var expected = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY), makePoint(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
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
        var expected = new BoundingBox(makePoint(-2,-4,-2), makePoint(3, 2, 2)); // TODO this might be wrong
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
        var expected = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY,-2,Float.NEGATIVE_INFINITY), makePoint(Float.POSITIVE_INFINITY,2,Float.POSITIVE_INFINITY)); // TODO this might be wrong
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsB() {
        // with an infinite cone
        var group = new Group(Matrix4.identity());
        var cone = new Cone(Matrix4.identity());
        group.addShape(cone);
        var result = group.bounds();
        var expected = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY), makePoint(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY)); // TODO this might be wrong
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsC() {
        // with an infinite cylinder
        var group = new Group(Matrix4.identity());
        var cylinder = new Cylinder(Transforms.identity().rotateZ(AngleHelp.toRadians(90)).assemble());
        group.addShape(cylinder);
        var result = group.bounds();
        var expected = new BoundingBox(makePoint(Float.NEGATIVE_INFINITY,-1,-1), makePoint(Float.POSITIVE_INFINITY,1,1)); // TODO this might be wrong
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
        var expected = new BoundingBox(makePoint(-1,-1,-1), makePoint(3,1,1)); // TODO this might be wrong
        assertEquals(expected, result);
    }

    @Test
    void testLocalGroupBoundsE() {
        var group = new Group(Matrix4.identity());
        var cube = new Cube(Transforms.identity().rotateX(AngleHelp.toRadians(45f)).assemble());
        group.addShape(cube);
        var result = group.bounds();
        var matrix = Transforms.identity().rotateX(AngleHelp.toRadians(45f)).assemble();
        var expectedPointMax = matrix.multiply(makePoint(1, 1, 1));
        var expectedPointMin = matrix.multiply(makePoint(-1, -1, -1));
        var expected = new BoundingBox(expectedPointMin, expectedPointMax); // TODO this might be wrong
        assertEquals(expected, result);
    }

}
