package com.BudgiePanic.rendering.util.noise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.FloatHelp;

public class PerlinTest {
    
    // because we are using the same permutation table as Perlin's reference implementation,
    // we can sample noise from his reference implementation and check that our values match his values

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
            0.18616071666792597f,
            -0.25f,
            -0.39553093537688255f,
            -0.5f,
            0.3032029124187421f,
            0.25f,
            0.17139582891775695f,
            -0.4136016582348199f,
            -0.125f,
            -0.10694424403659789f,
            0f
        };
        assertEquals(inputs.length, outputs.length); // sanity check
        for (int i = 0; i < inputs.length; i++) {
            float[] input = inputs[i];
            float actual = Perlin.noise(input[0], input[1], input[2]);
            float expected = outputs[i];
            var outcome = ((float)Math.abs(actual - expected)) < FloatHelp.bigEpsilon;
            assertTrue(outcome, Arrays.toString(input) + " produced wrong output expected[" + expected + "]  actual[" + actual + "] difference:" + (expected - actual));
        }
    }

}
