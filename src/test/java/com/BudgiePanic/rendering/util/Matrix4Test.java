package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

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
}
