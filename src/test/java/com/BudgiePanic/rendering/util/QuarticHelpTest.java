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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class QuarticHelpTest {
    @Test
    void testSolveQuartic() {
        var tests = List.of(
            new Pair<>(new float[]{1f, -8f, 21.875f, -23.5f, 8.378906f}, List.of(0.75, 1.25, 2.75, 3.25)),
            new Pair<>(new float[]{1f/14f, 1f/14f, -13f/14f, -1f/14f, 1.35714f}, List.of(-3.93008, -1.26495, 1.33748, 2.85755)),
            new Pair<>(new float[]{1f, 1f, 1f, 1f, -1f}, List.of(-1.2906, 0.51879)),
            new Pair<>(new float[]{-2f, -4f, 7f, 4f, 0f}, List.of(-2.9553, -0.47361, 0.0, 1.4289)),
            new Pair<>(new float[]{1f, 1f, 1f, 1f, -10f}, List.of(-2, 1.4026)),
            new Pair<>(new float[]{1f, -8f, 18f, -9f, -10f}, List.of(-0.49813, 4.6371)),
            new Pair<>(new float[]{2f, 8f, 7f, 4f, -10f}, List.of(-3.2588, 0.71891)),
            new Pair<>(new float[]{1f, 1f, 1f, 1f, 1f}, List.<Double> of())
        );
        for (var test : tests) {
            var a = test.a(); // coefficients
            var b = test.b(); // expected roots
            var foundRoots = QuarticHelp.solveQuartic(a[0], a[1], a[2], a[3], a[4]);
            assertEquals(b.size(), foundRoots.size());
            for (int i = 0; i < foundRoots.size(); i++) {
                var expected = b.get(i);
                var actual = foundRoots.get(i);
                assertTrue(FloatHelp.compareFloat(expected.floatValue(), actual) == 0, "test: " + Arrays.toString(a) + " expected: " + expected + " actual: " + actual);
            }
        }
    }
}
