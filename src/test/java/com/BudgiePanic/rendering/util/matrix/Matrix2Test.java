/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.util.matrix;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

/**
 * Matrix 2 by 2 tests
 */
public class Matrix2Test {
    @Test
    void testCreateMatrix2() {
        var mat2 = Matrix2.buildMatrix(
            -3f, 5f, 
            1f, -2f
        );
        assertTrue(compareFloat(mat2.matrix[0][0], -3f) == 0, "[0,0] matrix element was not -3");
        assertTrue(compareFloat(mat2.matrix[0][1], 5f) == 0, "[0,1] matrix element was not 5");
        assertTrue(compareFloat(mat2.matrix[1][0], 1f) == 0, "[1,0] matrix element was not 1");
        assertTrue(compareFloat(mat2.matrix[1][1], -2f) == 0, "[1,1] matrix element was not -2");
    }

    // TODO write additional tests for the row and column constructors

    @Test
    void testMat2Equals() {
        var mat1 = Matrix2.buildMatrix(
            1f, 2f,
            3f, 4f);
        var mat2 = Matrix2.buildMatrix(
            1f, 2f,
            3f, 4f);
        assertEquals(mat1, mat2);

        mat2 = Matrix2.buildMatrix(
            10f, 20f,
            30f, 40f);
        assertNotEquals(mat1, mat2);
    }

    @Test
    void testMat2Determinant() {
        var mat = Matrix2.buildMatrix(1f, 5f, -3f, 2f);
        var det = mat.getDeterminant();
        assertEquals(17f, det);
    }
}
