package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
    }

}
