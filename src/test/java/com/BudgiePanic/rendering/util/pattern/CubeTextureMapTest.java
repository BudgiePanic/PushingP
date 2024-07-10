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

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Pair;

public class CubeTextureMapTest {

    @Test 
    void testCubeUVMap() {
        final Color red = Colors.red, yellow = new Color(1, 1, 0), brown = new Color(1,0.5,0), 
        green = Colors.green, cyan = new Color(0,1,1), blue = Colors.blue, purple = new Color(1, 0, 1),
        white = Colors.white;
        var left = Pattern2D.mapCheck(yellow, cyan, red, blue, brown);
        var front = Pattern2D.mapCheck(cyan, red, yellow, brown, green);
        var right = Pattern2D.mapCheck(red, yellow, purple, green, white);
        var back = Pattern2D.mapCheck(green, purple, cyan, white, blue);
        var up = Pattern2D.mapCheck(brown, cyan, purple, red, yellow);
        var down = Pattern2D.mapCheck(purple, brown, green, blue, white);
        Pattern cube = new CubeTextureMap(front, left, right, up, down, back);
        var tests = List.of(
            // LEFT FACE
            new Pair<>(makePoint(-1,0,0), yellow),
            new Pair<>(makePoint(-1,0.9,-0.9), cyan),
            new Pair<>(makePoint(-1,0.9,0.9), red),
            new Pair<>(makePoint(-1,-0.9,-0.9), blue),
            new Pair<>(makePoint(-1,-0.9,0.9), brown),
            // FRONT FACE
            new Pair<>(makePoint(0,0,1), cyan),
            new Pair<>(makePoint(-0.9,0.9,1), red),
            new Pair<>(makePoint(0.9,0.9,1), yellow),
            new Pair<>(makePoint(-0.9,-0.9,1), brown),
            new Pair<>(makePoint(0.9,-0.9,1), green),
            // RIGHT FACE
            new Pair<>(makePoint(1,0,0), red),
            new Pair<>(makePoint(1,0.9,0.9), yellow),
            new Pair<>(makePoint(1,0.9,-0.9), purple),
            new Pair<>(makePoint(1,-0.9,0.9), green),
            new Pair<>(makePoint(1,-0.9,-0.9), white),
            // BACK FACE
            new Pair<>(makePoint(0,0,-1), green), 
            new Pair<>(makePoint(0.9,0.9,-1), purple),
            new Pair<>(makePoint(-0.9,0.9,-1), cyan),
            new Pair<>(makePoint(0.9,-0.9,-1), white),
            new Pair<>(makePoint(-0.9,-0.9,-1), blue),
            // UP FACE
            new Pair<>(makePoint(0,1,0), brown),
            new Pair<>(makePoint(-0.9,1,-0.9), cyan),
            new Pair<>(makePoint(0.9,1,-0.9), purple),
            new Pair<>(makePoint(-0.9,1,0.9), red),
            new Pair<>(makePoint(0.9,1,0.9), yellow),
            // DOWN FACE
            new Pair<>(makePoint(0,-1,0), purple),
            new Pair<>(makePoint(-0.9,-1,0.9), brown),
            new Pair<>(makePoint(0.9,-1,0.9), green),
            new Pair<>(makePoint(-0.9,-1,-0.9), blue),
            new Pair<>(makePoint(0.9,-1,-0.9), white)
        );
        for (final var test : tests) {
            var expected = test.b();
            var actual = cube.colorAt(test.a());
            assertEquals(expected, actual);
        }
    }

}
