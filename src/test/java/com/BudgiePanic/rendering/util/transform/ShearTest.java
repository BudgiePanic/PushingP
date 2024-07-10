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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Unit tests for the shear matrix builder utility class.
 * 
 * @author BudgiePanic
 */
public class ShearTest {

    @Test
    void testXYshear() {
        var transform = Shear.buildShearMatrix(1, 0, 0, 0, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(5, 3, 4);
        assertEquals(expected, result);
    }

    @Test
    void testXZshear() {
        var transform = Shear.buildShearMatrix(0, 1, 0, 0, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(6, 3, 4);
        assertEquals(expected, result);
    }

    @Test
    void testYXshear() {
        var transform = Shear.buildShearMatrix(0, 0, 1, 0, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 5, 4);
        assertEquals(expected, result);
    }

    @Test
    void testYZshear() {
        var transform = Shear.buildShearMatrix(0, 0, 0, 1, 0, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 7, 4);
        assertEquals(expected, result);
    }

    @Test
    void testZXshear() {
        var transform = Shear.buildShearMatrix(0, 0, 0, 0, 1, 0);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 3, 6);
        assertEquals(expected, result);
    }

    @Test
    void testZYshear() {
        var transform = Shear.buildShearMatrix(0, 0, 0, 0, 0, 1);
        var point = Tuple.makePoint(2, 3, 4);
        var result = transform.multiply(point);
        var expected = Tuple.makePoint(2, 3, 7);
        assertEquals(expected, result);
    }
    
}
