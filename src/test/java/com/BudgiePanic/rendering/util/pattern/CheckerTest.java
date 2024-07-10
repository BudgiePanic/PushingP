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
import static com.BudgiePanic.rendering.util.pattern.BiOperation.checker;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Tests for the checkerbox pattern. Tests work in the local pattern coordinate space.
 */
public class CheckerTest {
 
    @Test
    void testCheckerPatternX() {
        var pattern = new BiPattern(checker, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0.99f, 0, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(1.01f, 0, 0)));
    }
    
    @Test
    void testCheckerPatternY() {
        var pattern = new BiPattern(checker, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0.99f, 0)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(0, 1.01f, 0)));
    }

    @Test
    void testCheckerPatternZ() {
        var pattern = new BiPattern(checker, Colors.white, Colors.black);
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0)));
        assertEquals(Colors.white, pattern.colorAt(Tuple.makePoint(0, 0, 0.99f)));
        assertEquals(Colors.black, pattern.colorAt(Tuple.makePoint(0, 0, 1.01f)));
    }
}
