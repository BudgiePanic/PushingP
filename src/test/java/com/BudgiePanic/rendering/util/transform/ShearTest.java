package com.BudgiePanic.rendering.util.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Unit tests for the shear matrix builder utility class.
 * 
 * @author BudgiePanic
 */
public class ShearTest {

    @Test
    void testXYshear() {
        var transform = Shear.buildShearMatrix(1, 0, 0, 0, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(5, 3, 4);
        assertEquals(expected, result);
    }

    @Test
    void testXZshear() {
        var transform = Shear.buildShearMatrix(0, 1, 0, 0, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(6, 3, 4);
        assertEquals(expected, result);
    }

    @Test
    void testYXshear() {
        var transform = Shear.buildShearMatrix(0, 0, 1, 0, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 5, 4);
        assertEquals(expected, result);
    }

    @Test
    void testYZshear() {
        var transform = Shear.buildShearMatrix(0, 0, 0, 1, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 7, 4);
        assertEquals(expected, result);
    }

    @Test
    void testZXshear() {
        var transform = Shear.buildShearMatrix(0, 0, 0, 0, 1, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 3, 6);
        assertEquals(expected, result);
    }

    @Test
    void testZYshear() {
        var transform = Shear.buildShearMatrix(0, 0, 0, 0, 0, 1);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 3, 7);
        assertEquals(expected, result);
    }
    
}
