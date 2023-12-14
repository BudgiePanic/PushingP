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
}
