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
package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

/**
 * Tests for color type
 */
public class ColorTest {
    
    @Test
    void testColorGetters() {
        Color color = new Color(-0.5f, 0.4f, 1.7f);
        assertTrue(compareFloat(-0.5f, color.getRed()) == 0);
        assertTrue(compareFloat(0.4f, color.getGreen()) == 0);
        assertTrue(compareFloat(1.7f, color.getBlue()) == 0);
    }

    @Test
    void testColorAdd() {
        var c1 = new Color(0.9f, 0.6f, 0.75f);
        var c2 = new Color(0.7f, 0.1f, 0.25f);
        var result = c1.add(c2);
        var expected = new Color(1.6f, 0.7f, 1.0f);
        assertEquals(expected, result);
    }

    @Test
    void testColorSubtract() {
        var c1 = new Color(0.9f, 0.6f, 0.75f);
        var c2 = new Color(0.7f, 0.1f, 0.25f);
        var result = c1.subtract(c2);
        var expected = new Color(0.2f, 0.5f, 0.5f);
        assertEquals(expected, result);
    }

    @Test
    void testColorMultiply() {
        var c1 = new Color(0.2f, 0.3f, 0.4f);
        var result = c1.multiply(2.0f);
        var expected = new Color(0.4f, 0.6f, 0.8f);
        assertEquals(expected, result);
    }

    @Test
    void testColorColorMultiply() {
        var c1 = new Color(1.0f, 0.2f, 0.4f);
        var c2 = new Color(0.9f, 1.0f, 0.1f);
        var result = c1.colorMul(c2);
        var expected = new Color(0.9f, 0.2f, 0.04f);
        assertEquals(expected, result);
    }
}
