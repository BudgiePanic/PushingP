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

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Pair;

public class TextureMapTest {

    @Test
    void testColorAt() {
        var pattern = new TextureMap(
            Pattern2D.checker(16, 8, Pattern2D.solidColor(Colors.black), Pattern2D.solidColor(Colors.white)), 
            CoordinateMapper.sphere
        );
        var tests = List.of(
            new Pair<>(makePoint(0.4315, 0.4670, 0.7719), Colors.white),
            new Pair<>(makePoint(-0.9654, 0.2552, -0.0534), Colors.black),
            new Pair<>(makePoint(0.1039, 0.7090, 0.6975), Colors.white),
            new Pair<>(makePoint(-0.4986, -0.7856, -0.3663), Colors.black),
            new Pair<>(makePoint(-0.0317, -0.9395, 0.3411), Colors.black),
            new Pair<>(makePoint(0.4809, -0.7721, 0.4154), Colors.black),
            new Pair<>(makePoint(0.0285, -0.9612, -0.2745), Colors.black),
            new Pair<>(makePoint(-0.5734, -0.2162, -0.7903), Colors.white),
            new Pair<>(makePoint(0.7688, -0.1470, 0.6223), Colors.black),
            new Pair<>(makePoint(-0.7652, 0.2175, 0.6060), Colors.black)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = pattern.colorAt(test.a());
            assertEquals(expected, actual, test.toString());
        }
    }
}
