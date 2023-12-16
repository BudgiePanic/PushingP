package com.BudgiePanic.rendering.util.matrix;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Tests for the Matrix4 type.
 */
public class Matrix4Test {
    @Test
    void testConstructor() {
        var mat = Matrix4.buildMatrix
        (1f, 2f, 3f, 4f,
        5.5f, 6.5f, 7.5f, 8.5f,
        9f, 10f, 11f, 12f,
        13.5f, 14.5f, 15.5f, 16.5f);
        assertTrue(compareFloat(mat.matrix[0][0], 1f) == 0, "[0,0] matrix element was not 1");
        assertTrue(compareFloat(mat.matrix[0][3], 4f) == 0, "[0,3] matrix element was not 4");
        assertTrue(compareFloat(mat.matrix[1][0], 5.5f) == 0, "[1,0] matrix element was not 5.5");
        assertTrue(compareFloat(mat.matrix[1][2], 7.5f) == 0, "[1,2] matrix element was not 7.5");
        assertTrue(compareFloat(mat.matrix[2][2], 11f) == 0, "[2,2] matrix element was not 11");
        assertTrue(compareFloat(mat.matrix[3][0], 13.5f) == 0, "[3,0] matrix element was not 13.5");
        assertTrue(compareFloat(mat.matrix[3][2], 15.5f) == 0, "[3,2] matrix element was not 15.5");
    }

    @Test
    void testRowConstructor() {
        var mat = Matrix4.buildMatrixRow(
            new float[] {1f, 2f, 3f, 4f},
            new float[] {5.5f, 6.5f, 7.5f, 8.5f},
            new float[] {9f, 10f, 11f, 12f},
            new float[] {13.5f, 14.5f, 15.5f, 16.5f});
        assertTrue(compareFloat(mat.matrix[0][0], 1f) == 0, "[0,0] matrix element was not 1");
        assertTrue(compareFloat(mat.matrix[0][3], 4f) == 0, "[0,3] matrix element was not 4");
        assertTrue(compareFloat(mat.matrix[1][0], 5.5f) == 0, "[1,0] matrix element was not 5.5");
        assertTrue(compareFloat(mat.matrix[1][2], 7.5f) == 0, "[1,2] matrix element was not 7.5");
        assertTrue(compareFloat(mat.matrix[2][2], 11f) == 0, "[2,2] matrix element was not 11");
        assertTrue(compareFloat(mat.matrix[3][0], 13.5f) == 0, "[3,0] matrix element was not 13.5");
        assertTrue(compareFloat(mat.matrix[3][2], 15.5f) == 0, "[3,2] matrix element was not 15.5");
    }

    @Test
    void testColumnConstructor() {
        var mat = Matrix4.buildMatrixColumn(
            new float[] {1f, 5.5f, 9f, 13.5f},
            new float[] {2f, 6.5f, 10f, 14.5f},
            new float[] {3f, 7.5f, 11f, 15.5f},
            new float[] {4f, 8.5f, 12f, 16.5f});
        assertTrue(compareFloat(mat.matrix[0][0], 1f) == 0, "[0,0] matrix element was not 1");
        assertTrue(compareFloat(mat.matrix[0][3], 4f) == 0, "[0,3] matrix element was not 4");
        assertTrue(compareFloat(mat.matrix[1][0], 5.5f) == 0, "[1,0] matrix element was not 5.5");
        assertTrue(compareFloat(mat.matrix[1][2], 7.5f) == 0, "[1,2] matrix element was not 7.5");
        assertTrue(compareFloat(mat.matrix[2][2], 11f) == 0, "[2,2] matrix element was not 11");
        assertTrue(compareFloat(mat.matrix[3][0], 13.5f) == 0, "[3,0] matrix element was not 13.5");
        assertTrue(compareFloat(mat.matrix[3][2], 15.5f) == 0, "[3,2] matrix element was not 15.5");
    }

    @Test
    void testMatrixToString(){
        var mat = Matrix4.buildMatrix
        (1f, 2f, 3f, 4f,
        5.5f, 6.5f, 7.5f, 8.5f,
        9f, 10f, 11f, 12f,
        13.5f, 14.5f, 15.5f, 16.5f);
        var matString = mat.toString();
        var expected = "[[1.0, 2.0, 3.0, 4.0], [5.5, 6.5, 7.5, 8.5], [9.0, 10.0, 11.0, 12.0], [13.5, 14.5, 15.5, 16.5]]";
        assertEquals(expected, matString);
    }

    @Test
    void testMat4Equals() {
        var mat1 = Matrix4.buildMatrix(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f);
        var mat2 = Matrix4.buildMatrix(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f);
        assertEquals(mat1, mat2);

        mat2 = Matrix4.buildMatrix(
            10f, 20f, 30f, 40f,
            50f, 60f, 70f, 80f,
            90f, 100f, 110f, 120f,
            130f, 140f, 150f, 160f);
        assertNotEquals(mat1, mat2);
    }

    @Test 
    void testMat4EqualsNearMiss() {
        var mat1 = Matrix4.buildMatrix(
            1.000001f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f);
        var mat2 = Matrix4.buildMatrix(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f);
        assertEquals(mat1, mat2);
    }

    @Test
    void testMat4Multiply() {
        var mat1 = Matrix4.buildMatrix(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 8f, 7f, 6f,
            5f, 4f, 3f, 2f
        );

        var mat2 = Matrix4.buildMatrix(
            -2f, 1f, 2f, 3f,
            3f, 2f, 1f, -1f,
            4f, 3f, 6f, 5f,
            1f, 2f, 7f, 8f
        );

        var result = mat1.multiply(mat2);

        var expected = Matrix4.buildMatrix(
            20f, 22f, 50f, 48f,
            44f, 54f, 114f, 108f,
            40f, 58f, 110f, 102f,
            16f, 26f, 46f, 42f
        );

        assertEquals(expected, result);
    }

    @Test
    void testMat4TupleMultiply(){
        var mat = Matrix4.buildMatrix(
            1f, 2f, 3f, 4f, 
            2f, 4f, 4f, 2f, 
            8f, 6f, 4f, 1f,
            0f, 0f, 0f, 1f
        );
        var tuple = new Tuple(1f, 2f, 3f, 1f);
        
        var result = mat.multiply(tuple);
        var expected = new Tuple(18f, 24f, 33f, 1f);

        assertEquals(expected, result);
    }

}
