package com.BudgiePanic.rendering.util.shape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class GroupTest {

    static Matrix4 identity = Matrix4.identity();

    @Test
    void testGetShapes() {
        Group group = new Group(identity);
        var shapes = group.getShapes();
        assertTrue(shapes.isEmpty());
    }

    @Test
    void testAddChildren() {
        var group = new Group(identity);
        var shape = new BaseShapeTest.TestShape(identity);
        group.addShape(shape);
        assertFalse(group.children.isEmpty());
        assertTrue(group.children.contains(shape));
        assertTrue(shape.parent.isPresent());
        assertEquals(group, shape.parent().get());
    }

    @Test
    void testIntersectionEmptyGroup() {
        var group = new Group(identity);
        var ray = new Ray(makePoint(), makeVector(0, 0, 1));
        var result = group.intersect(ray);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParentAttribute() {
        var group = new Group(identity);
        assertTrue(group.parent().isEmpty());
    }
    
    @Test
    void testNonEmptyGroupIntersection() {
        var group = new Group(identity);
        Shape shape1 = new Sphere(identity),
              shape2 = new Sphere(Transforms.identity().translate(0, 0, -3).assemble()),
              shape3 = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        group.addShape(shape1);
        group.addShape(shape2);
        group.addShape(shape3);
        var ray = new Ray(makePoint(0,0,-5), makeVector(0, 0, 1));
        var result = group.localIntersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(4, intersections.size());
        assertEquals(shape2, intersections.get(0).shape());
        assertEquals(shape2, intersections.get(1).shape());
        assertEquals(shape1, intersections.get(2).shape());
        assertEquals(shape1, intersections.get(3).shape());
    }

    @Test
    void testGroupTransform() {
        var group = new Group(Transforms.identity().scale(2, 2, 2).assemble());
        group.addShape(new Sphere(Transforms.identity().translate(5, 0, 0).assemble()));
        var ray = new Ray(makePoint(10, 0, -10), makeVector(0, 0, 1));
        var result = group.intersect(ray);
        assertTrue(result.isPresent());
        var intersections = result.get();
        assertEquals(2, intersections.size());
    }

    @Test
    void testRecursivePointTransformation() {
        float piOver2 = (float) (Math.PI / 2.0);
        var groupA = new Group(Transforms.identity().rotateY(piOver2).assemble());
        var groupB = new Group(Transforms.identity().scale(2, 2, 2).assemble());
        groupA.addShape(groupB);
        var shape = new Sphere(Transforms.identity().translate(5, 0, 0).assemble());
        groupB.addShape(shape);
        var result = shape.toObjectSpace(makePoint(-2, 0, -10));
        assertEquals(makePoint(0, 0, -1), result);
    }
}
