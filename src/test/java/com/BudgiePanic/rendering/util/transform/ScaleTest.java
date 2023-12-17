package com.BudgiePanic.rendering.util.transform;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Unit tests for scale matrix builder utility
 * 
 * @author BudgiePanic
 */
public class ScaleTest {
    
    @Test
    void testScale() {
        var transform = Scale.makeScaleMatrix(2, 3, 4);
        var point = Tuple.makePoint(-4, 6, 8);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(-8, 18, 32);
        assertEquals(expected, result);
    }

    @Test
    void testScaleVector() {
        var transform = Scale.makeScaleMatrix(2, 3, 4);
        var point = Tuple.makeVector(-4, 6, 8);
        var result = transform.multiply(point);
        var expected = Tuple.makeVector(-8, 18, 32);
        assertEquals(expected, result);
    }

    @Test 
    void testScaleInverse() {
        var transform = Scale.makeScaleMatrix(2, 3, 4).inverse();
        var point = Tuple.makeVector(-4, 6, 8);
        var result = transform.multiply(point);
        var expected = Tuple.makeVector(-2, 2, 2);
        assertEquals(expected, result);
    }

    @Test
    void testScaleReflect() {
        var transform = Scale.makeReflectMatrix(true, false, false);
        var point = Tuple.makeVector(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makeVector(-2, 3, 4);
        assertEquals(expected, result);
    }

}
