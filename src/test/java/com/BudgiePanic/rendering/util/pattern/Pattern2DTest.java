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

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Pair;

public class Pattern2DTest {
    
    @Test
    void testChecker() {
        var tests = List.of(
            new Pair<>(new Pair<>(0.0, 0.0), Colors.black),
            new Pair<>(new Pair<>(0.5, 0.0), Colors.white),
            new Pair<>(new Pair<>(0.0, 0.5), Colors.white),
            new Pair<>(new Pair<>(0.5, 0.5), Colors.black),
            new Pair<>(new Pair<>(1.0, 1.0), Colors.black),
            new Pair<>(new Pair<>(0.25, 0.25), Colors.black),
            new Pair<>(new Pair<>(0.75, 0.75), Colors.black),
            new Pair<>(new Pair<>(0.75, 0.25), Colors.white),
            new Pair<>(new Pair<>(0.25, 0.75), Colors.white)
        );
        var pattern = Pattern2D.checker(2, 2, Pattern2D.solidColor(Colors.black), Pattern2D.solidColor(Colors.white));
        for (final var test : tests) {
            double u = test.a().a();
            double v = test.a().b();
            var expected = test.b();
            var result = pattern.sample(u, v);
            assertEquals(expected, result, test.toString());
        }
    }

    @Test
    void testSolidColor() {
        var pattern = Pattern2D.solidColor(Colors.blue);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                var result = pattern.sample(x, y);
                assertEquals(Colors.blue, result, "x " + x + " y " + y);
            }
        }
    }

    @Test
    void testMapCheckPattern() {
        Color a = Colors.white, b = Colors.red, c = new Color(1,1,0), d = Colors.green, e = new Color(0,1,1);
        var pattern = Pattern2D.mapCheck(a, b, c, d, e);
        var tests = List.of(
            new Pair<>(new double[]{0.5,0.5}, a),
            new Pair<>(new double[]{0.1,0.9}, b),
            new Pair<>(new double[]{0.9,0.99}, c),
            new Pair<>(new double[]{0.1,0.1}, d),
            new Pair<>(new double[]{0.9,0.1}, e)
        );
        for (final var test : tests) {
            var u = test.a()[0];
            var v = test.a()[1];
            var expected = test.b();
            var result = pattern.sample(u, v);
            assertEquals(expected, result);
        }
    }
}
