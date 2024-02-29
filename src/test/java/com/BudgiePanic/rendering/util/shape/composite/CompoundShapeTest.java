package com.BudgiePanic.rendering.util.shape.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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

}
