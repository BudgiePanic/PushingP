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
package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the normal processing modes of the normal debugging camera
 */
public class NormalCameraTest {
    @Test
    void testScaledNormalRemapping() {
        var mapper = NormalCamera.scaled;
        var normal = makeVector(-1, 0, 0);
        var result = mapper.apply(normal);
        var expected = makeVector(0, 0.5f, 0.5f);
        assertEquals(expected, result);
    }
    @Test
    void testScaledNormalRemappingA() {
        var mapper = NormalCamera.scaled;
        var normal = makeVector(-1, 1, -1).normalize();
        var result = mapper.apply(normal);
        var expected = makeVector(0.2113f, 0.7886f, 0.2113f);
        assertEquals(expected, result);
    } 
}
