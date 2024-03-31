package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the functionality of the Tuple primative.
 * 
 * @author BudgiePanic
 */
public class TupleTest {
    /**
     * Dummy test to check if this test suite will actually run.
     */
    @Test
    void checkTest(){
        assertTrue(true);
    }

    @Test
    void testPointCheck(){
        Tuple tuple = new Tuple(0f, 0f, 0f, 1.0f);
        assertTrue(tuple.isPoint());
        assertFalse(tuple.isVector());
        tuple = new Tuple(0f, 0f, 0f);
        assertTrue(tuple.isPoint());
        assertFalse(tuple.isVector());
        assertTrue(tuple.w == 1.0f);
    }

    @Test
    void testVectorCheck(){
        Tuple tuple = new Tuple(0f, 0f, 0f, 0f);
        assertFalse(tuple.isPoint());
        assertTrue(tuple.isVector());
        tuple = new Tuple();
        assertFalse(tuple.isPoint());
        assertTrue(tuple.isVector());
        assertTrue(tuple.w == 0.0f);
    }

    @Test 
    void testVectorFactoryMethod(){
        Tuple tuple = Tuple.makeVector();
        assertTrue(tuple.isVector());
        assertFalse(tuple.isPoint());
    }

    @Test
    void testPointFactoryMethod(){
        Tuple tuple = Tuple.makePoint();
        assertTrue(tuple.isPoint());
        assertFalse(tuple.isVector());
    }

    @Test 
    void testTupleEquals(){
        Tuple tupleA = Tuple.makeVector(0, 0, 0);
        Tuple tupleB = Tuple.makeVector(0.0f, 0.0f, 0.0f);
        assertTrue(tupleA.equals(tupleB));
        assertTrue(tupleB.equals(tupleA));
        assertFalse(tupleA.equals(null));
    }

    @Test
    void testCloseEquals(){
        Tuple tupleA = Tuple.makeVector(0, 0, 0);
        Tuple tupleB = Tuple.makeVector(0.00000001f, 0.0f, 0.0f);
        assertTrue(tupleA.equals(tupleB));
    }

    @Test
    void testAngleBetween() {
        var tests = List.of(
            new Pair<>(Directions.up.angleBetween(Directions.left), (float)(Math.PI / 2.0)),
            new Pair<>(Directions.up.angleBetween(Directions.down), (float)(Math.PI))
        );
        for (final var test : tests) {
            var expected = test.b();
            var result = test.a();
            assertTrue(FloatHelp.compareFloat(expected, result) == 0, "expected " + expected + " result " + result);
        }
    }

}
