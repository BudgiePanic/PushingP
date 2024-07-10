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
package com.BudgiePanic.rendering.util.noise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;

public class PerlinTest {
    
    // because we are using the same permutation table as Perlin's reference implementation,
    // we can sample noise from his reference implementation and check that our values match his values
    // note that this idea only works if the reference implementation is modified to use the same gradient array as our implementation
    // otherwise the pseudo random indexing into the gradient array will give different gradients

    @Test
    void testPerlin() {
        final float[][] inputs = {
            {0.1f,0.1f,0.1f},
            {0.5f,0.5f,0.5f},
            {1.25f,1.25f,1.25f},
            {1.5f,1.5f,1.5f},
            {2.3f,2.3f,2.3f},
            {2.5f,2.5f,2.5f},
            {3.65f,3.65f,3.65f},
            {3.3f,3.3f,3.3f},
            {4.5f,4.5f,4.5f},
            {4.7f,4.7f,4.7f},
            {5,5,5}
        };
        final float[] outputs = {
            0.18655187814166527f,
            0.0f,
            -0.051203202456235886f,
            -0.25f,
            -0.02343283749561467f,
            0.125f,
            0.22010569760539736f,
            0.004451584588074847f,
            -0.25f,
            0.05532185845280016f,
            0.0f,
        };
        assertEquals(inputs.length, outputs.length); // sanity check
        for (int i = 0; i < inputs.length; i++) {
            float[] input = inputs[i];
            float actual = (float) Perlin.noise(input[0], input[1], input[2]);
            float expected = outputs[i];
            var outcome = FloatHelp.compareFloat(actual, expected) == 0;
            assertTrue(outcome, Arrays.toString(input) + " produced wrong output expected[" + expected + "]  actual[" + actual + "] difference:" + (expected - actual));
        }
    }

}
