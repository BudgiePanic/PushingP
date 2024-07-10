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

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.noise.Perlin;

/**
 * Perturbs the point before the pattern is sampled to add organic noise to the output.
 * 
 * @author BudgiePanic
 */
public record Perturb(Pattern pattern, float scale) implements Pattern {

    /**
     * Default noise scale perturbation pattern.
     *
     * @param pattern
     *   The pattern to perturb.
     */
    public Perturb(Pattern pattern) {
        this(pattern, 0.2f);
    }

    @Override
    public Color colorAt(Tuple point) {
        final double noise = Perlin.noise(point.x, point.y, point.z);
        final double scaledNoise = noise * scale;
        final var perturbedPoint = point.add((point.x * scaledNoise), (point.y * scaledNoise), (point.z * scaledNoise));
        return pattern.colorAt(perturbedPoint);
    }

    @Override
    public Matrix4 transform() { return pattern.transform(); }
    
}
