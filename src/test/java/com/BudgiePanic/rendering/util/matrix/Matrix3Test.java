package com.BudgiePanic.rendering.util.matrix;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

public class Matrix3Test {
    @Test
    void testCreateMatrix3() {
        var mat3 = Matrix3.buildMatrix(
            -3f, 5f, 0f, 
            1f, -2f, -7f,
            0f, 1f, 1f
        );
        assertTrue(compareFloat(mat3.matrix[0][0], -3f) == 0, "[0,0] matrix element was not -3");
        assertTrue(compareFloat(mat3.matrix[1][1], -2f) == 0, "[1,1] matrix element was not -2");
        assertTrue(compareFloat(mat3.matrix[2][2], 1f) == 0, "[2,2] matrix element was not 1");
    }

    // TODO write additional tests for the row and column constructors

    @Test
    void testMat3Equals() {
        var mat1 = Matrix3.buildMatrix(
            1f, 2f, 3f, 
            4f, 5f, 6f,
            7f, 8f, 9f);
        var mat2 = Matrix3.buildMatrix(
            1f, 2f, 3f,
            4f, 5f, 6f,
            7f, 8f, 9f);
        assertEquals(mat1, mat2);

        mat2 = Matrix3.buildMatrix(
            10f, 20f, 30f,
            40f, 50f, 60f,
            70f, 80f, 90f);
        assertNotEquals(mat1, mat2);
    }

    @Test
    void testMat3SubMat() {
        var mat3 = Matrix3.buildMatrix(
            1, 5, 0, 
            -3, 2, 7, 
            0, 6, 3
        );
        var result = mat3.getSubMatrix(0, 2);
        var expected = Matrix2.buildMatrix(-3, 2, 0, 6);
        assertEquals(expected, result);
    }

    @Test
    void testM3Minors() {
        var mat = Matrix3.buildMatrix(
            3, 5, 0, 
            2, -1, -7, 
            6, -1, 5
        );
        var result = mat.getMinor(1, 0);
        var expected = 25f;
        assertEquals(0, compareFloat(expected, result));
    }

    @Test
    void testM3Cofactor() {
        var mat = Matrix3.buildMatrix(
            3, 5, 0, 
            2, -1, -7, 
            6, -1, 5
        );
        assertEquals(0, compareFloat(-12f, mat.getMinor(0, 0)));
        assertEquals(0, compareFloat(-12f, mat.getCofactor(0, 0)));
        assertEquals(0, compareFloat(25f, mat.getMinor(1, 0)));
        assertEquals(0, compareFloat(-25f, mat.getCofactor(1, 0)));
    }

    @Test
    void testM3Deteminant() {
        var mat = Matrix3.buildMatrix(
            1, 2, 6, 
            -5, 8, -4, 
            2, 6, 4
        );
        assertEquals(0, compareFloat(56f, mat.getCofactor(0, 0)));
        assertEquals(0, compareFloat(12f, mat.getCofactor(0, 1)));
        assertEquals(0, compareFloat(-46f, mat.getMinor(0, 2)));
        assertEquals(0, compareFloat(-196f, mat.getDeterminant()));
    }
}
