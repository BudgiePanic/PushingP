package com.BudgiePanic.rendering.util.shape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Material;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

public class GroupTest {

    static Matrix4 identity = Matrix4.identity();

    @Test
    void testGetShapes() {
        Group group = new Group(identity);
        var shapes = group.getShapes();
        assertTrue(shapes.isEmpty());
    }

    @Test
    void testParentAttribute() {
        var shape = new Shape() {
            @Override
            public Matrix4 transform() { return null; }
            @Override
            public Material material() { return null; }
            @Override
            public Optional<List<Intersection>> intersect(Ray ray) { return Optional.empty(); }
            @Override
            public Tuple normal(Tuple point) { return makeVector(); }
        };
        assertTrue(shape.parent().isEmpty());
        var group = new Group(identity);
        assertTrue(group.parent().isEmpty());
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

}
