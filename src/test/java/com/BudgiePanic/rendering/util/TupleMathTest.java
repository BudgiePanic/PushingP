package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the math operations of the Tuple type.
 * 
 * @author BudgiePanic
 */
public class TupleMathTest {
    
    @Test
    void testAdd(){
        Tuple a = Tuple.makePoint(3f, -2f, 5f);
        Tuple b = Tuple.makeVector(-2f, 3f, 1f);
        Tuple result = a.add(b);
        assertEquals(0, compareFloat(result.x, 1f));
        assertEquals(0, compareFloat(result.y, 1f));
        assertEquals(0, compareFloat(result.z, 6f));
        // A point plus a vector takes us to a new point
        // Note that adding two points together doesn't make logical sense, this is reflected in the w component becoming 2 in the resulting tuple
        //    causing isPoint() and isVector() to both return false.
        assertTrue(result.isPoint());
        // check that the original tuples were unmodified by the operation
        assertEquals(0, compareFloat(a.x, 3f));
        assertEquals(0, compareFloat(a.y, -2f));
        assertEquals(0, compareFloat(a.z, 5f));
        assertTrue(a.isPoint());

        assertEquals(0, compareFloat(b.x, -2f));
        assertEquals(0, compareFloat(b.y, 3f));
        assertEquals(0, compareFloat(b.z, 1f));
        assertTrue(b.isVector());
    }

    @Test
    void testSubtract() {
        Tuple a = Tuple.makePoint(3f, 2f, 1f);
        Tuple b = Tuple.makePoint(5f, 6f, 7f);
        Tuple result = a.subtract(b);
        assertEquals(0, compareFloat(result.x, -2f));
        assertEquals(0, compareFloat(result.y, -4f));
        assertEquals(0, compareFloat(result.z, -6f));
        // A point subtracted from another point is a vector (direction)
        assertTrue(result.isVector());
    }

    @Test
    void testSubtractPoint() {
        Tuple a = Tuple.makePoint(3f, 2f, 1f);
        Tuple b = Tuple.makeVector(5f, 6f, 7f);
        Tuple result = a.subtract(b);
        assertTrue(result.isPoint());
        // Note that subtracting a point from a vector doesn't make logical sense
        //    this is reflected in the w component becoming negative
        //    causing isPoint() and isVector() to both return false.
    }

    @Test
    void testSubtractVector() {
        var a = Tuple.makeVector(3f, 2f, 1f);
        var b = Tuple.makeVector(5f, 6f, 7f);
        var result = a.subtract(b);
        assertEquals(0, compareFloat(result.x, -2f));
        assertEquals(0, compareFloat(result.y, -4f));
        assertEquals(0, compareFloat(result.z, -6f));
        assertTrue(result.isVector());
    }

    @Test
    void testNegate() {
        var a = new Tuple(1f, -2f, 3f, -4f);
        var expected = new Tuple(-1f, 2f, -3f, 4f);
        var result = a.negate();
        assertEquals(expected, result);
    }

    @Test
    void testMultiply() {
        throw new RuntimeException("test not implemented yet");
    }

    @Test
    void testDivide() {
        throw new RuntimeException("test not implemented yet");
    }

    @Test
    void testMagnitude() {
        var tuple = Tuple.makeVector();
        assertEquals(0, compareFloat(tuple.magnitude(), 0f));

        tuple = Tuple.makeVector(1f, 0f, 0f);
        assertEquals(0, compareFloat(tuple.magnitude(), 1f));

        tuple = Tuple.makeVector(0f, 1f, 0f);
        assertEquals(0, compareFloat(tuple.magnitude(), 1f));

        tuple = Tuple.makeVector(0f, 0f, 1f);
        assertEquals(0, compareFloat(tuple.magnitude(), 1f));

        tuple = Tuple.makeVector(1f, 2f, 3f);
        // 1**2 + 2**2 + 3**2 == 14
        assertEquals(0, compareFloat(tuple.magnitude(), (float)Math.sqrt(14.0)));

        tuple = Tuple.makeVector(-1f, -2f, -3f);
        assertEquals(0, compareFloat(tuple.magnitude(), (float)Math.sqrt(14.0)));
    }

    @Test
    void testNormalization() {
        var tuple = Tuple.makeVector(4f, 0f, 0f);
        var result = tuple.normalize();
        assertEquals(Tuple.makeVector(1f, 0f, 0f), result);

        result = Tuple.makeVector(1f, 2f, 3f).normalize();
        assertEquals(0, compareFloat(result.x, (float) (1.0 / Math.sqrt(14.0))));
        assertEquals(0, compareFloat(result.y, (float) (2.0 / Math.sqrt(14.0))));
        assertEquals(0, compareFloat(result.z, (float) (3.0 / Math.sqrt(14.0))));
    }

    @Test
    void testNormalizationMagnitude() {
        var result = Tuple.makeVector(1f, 2f, 3f).normalize();
        assertEquals(0, compareFloat(result.magnitude(), 1f));
    }

    @Test
    void testDotProduct() {
        throw new RuntimeException("test not implemented yet");
    }

    @Test
    void testCrossProduct() {
        throw new RuntimeException("test not implemented yet");
    }

    @Test
    void operationNullChecks() {
        // this test should check each applicable operation for precondition checking.
        // Invalid argument exceptions should be thrown instead of null ptr exceptions, to help with BLAME.
        throw new RuntimeException("test not implemented yet");
    }
}
