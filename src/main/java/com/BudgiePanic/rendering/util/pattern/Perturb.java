package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.noise.Perlin;

/**
 * Perturbs the point before the pattern is sampled to add organic noise to the output.
 * 
 * @author BudgiePanic
 */
public record Perturb(Pattern pattern) implements Pattern {

    @Override
    public Color colorAt(Tuple point) {
        final float noise = Perlin.noise(point.x, point.y, point.z);
        final float scaledNoise = noise * 0.2f;
        final var perturbedPoint = point.add((point.x * scaledNoise), (point.y * scaledNoise), (point.z * scaledNoise));
        return pattern.colorAt(perturbedPoint);
    }

    @Override
    public Matrix4 transform() { return pattern.transform(); }
    
}
