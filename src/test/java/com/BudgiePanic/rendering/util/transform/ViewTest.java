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
import com.BudgiePanic.rendering.util.matrix.Matrix4;

public class ViewTest {
    
    @Test
    void testViewMatrix() {
        // simple view matrix test
        var from = Tuple.makePoint();
        var to = Tuple.makePoint(0, 0, -1);
        var up = Tuple.makeVector(0, 1, 0);
        var transform = View.makeViewMatrix(from, to, up);
        assertEquals(Transforms.identity().assemble(), transform);
    }

    @Test
    void testViewBehind() {
        var from = Tuple.makePoint();
        var to = Tuple.makePoint(0, 0, 1);
        var up = Tuple.makeVector(0, 1, 0);
        var transform = View.makeViewMatrix(from, to, up);
        assertEquals(Transforms.identity().scale(-1, 1, -1).assemble(), transform);
    }

    @Test
    void testViewMovedFromOrigin() {
        var from = Tuple.makePoint(0,0,8);
        var to = Tuple.makePoint(0, 0, 0);
        var up = Tuple.makeVector(0, 1, 0);
        var transform = View.makeViewMatrix(from, to, up);
        assertEquals(Transforms.identity().translate(0, 0, -8).assemble(), transform);
    }

    @Test
    void testViewArbitrary() {
        var from = Tuple.makePoint(1,3,2);
        var to = Tuple.makePoint(4, -2, 8);
        var up = Tuple.makeVector(1, 1, 0);
        var transform = View.makeViewMatrix(from, to, up);
        assertEquals(Matrix4.buildMatrix(
             -0.50709f, 0.50709f, 0.67612f, -2.36643f,
             0.76772f, 0.60609f, 0.12122f, -2.82843f,
             -0.35857f, 0.59761f, -0.71714f, 0f,
             0f, 0f, 0f, 1f), transform);
    }

    @Test
    void testViewMovedFromOriginNegative() {
        var from = Tuple.makePoint(0,0,-8);
        var to = Tuple.makePoint(0, 0, 0);
        var up = Tuple.makeVector(0, 1, 0);
        var transform = View.makeViewMatrix(from, to, up);
        assertEquals(Transforms.identity().translate(0, 0, 8).scale(-1, 1, -1).assemble(), transform);
    }

}
