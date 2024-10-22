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
package com.BudgiePanic.rendering.util.transform;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Tests for the translation matrix builder utility.
 * 
 * @author BudgiePanic
 */
public class TranslationTest {
    
    @Test
    void testTranslationMultiply() {
        var transform = Translation.makeTranslationMatrix(5, -3, 2);
        var point = Tuple.makePoint(-3, 4, 5);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 1, 7);
        assertEquals(expected, result);
    }

    @Test
    void testTranslationMulInverse() {
        var transform = Translation.makeTranslationMatrix(5, -3, 2).inverse();
        var point = Tuple.makePoint(-3, 4, 5);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(-8, 7, 3);
        assertEquals(expected, result);
    }

    @Test
    void testVectorTranslation() {
        // test to check that vectors (directions) are not affected by translation matrices.
        // What does it mean to translate a direction anyway?
        var transform = Translation.makeTranslationMatrix(5, -3, 2);
        var vector = Tuple.makeVector(-3, 4, 5);
        var result = transform.multiply(vector);
        var expected = Tuple.makeVector(-3, 4, 5);
        assertEquals(expected, result);
    }

}
