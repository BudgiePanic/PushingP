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
package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.BudgiePanic.rendering.util.pattern.BiOperation.gradient;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Tests for the gradient pattern. Tests work in the local pattern coordinate space.
 */
public class GradientTest {
    
    @Test
    void testGradientInterpolation() {
        var pattern = new BiPattern(gradient, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(new Color(0.75f,0.75f,0.75f), pattern.colorAt(Tuple.makePoint(0.25f, 0, 0)));
        assertEquals(new Color(0.5f,0.5f,0.5f), pattern.colorAt(Tuple.makePoint(0.50f, 0, 0)));
        assertEquals(new Color(0.25f,0.25f,0.25f), pattern.colorAt(Tuple.makePoint(0.75f, 0, 0)));
    }
}
