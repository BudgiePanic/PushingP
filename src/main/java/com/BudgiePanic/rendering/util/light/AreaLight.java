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
package com.BudgiePanic.rendering.util.light;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.RandomSuppliers;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * An area light is a flat retangular panel light source.
 * 
 * @author BudgiePanic
 */
public record AreaLight(Color color, Tuple corner, Tuple uVector, Tuple vVector, int uStep, int vStep, Tuple vUnit, Tuple uUnit, Tuple position, Supplier<Double> generator) implements Light {

    /**
     * Constantly sample the area light segments.
     */
    public static final Supplier<Double> constantSamples = () -> { return 0.5; };

    /**
     * Randomly sample the area light segments with a thread safe random number generator.
     */
    public static final Supplier<Double> randomSamples = RandomSuppliers.threadSafeRandomSupplier;

    private final class AreaLightIterator implements Iterator<Tuple> {
        protected int u = 0, v = 0;
        @Override
        public boolean hasNext() { return u < uStep && v < vStep; }
        @Override
        public Tuple next() {
            if (!hasNext()) throw new NoSuchElementException();
            final var sample = sample(u, v);
            u++;
            if (u >= uStep) {
                u = 0;
                v++;
            }
            return sample;
        }
    }

    @Override
    public Iterator<Tuple> sampler() { return new AreaLightIterator(); }

    /**
     * Create a new area light. Autogenerates unit vectors and position.
     *
     * @param color
     *   The color of the area light, magnitude of the color doubles as the light intensity.
     * @param corner
     *   The world space location of the area light corner.
     *   The uv vectors define which corner of the area light rectangle the corner belongs to.
     * @param uVector
     *   The direction and magnitude of the u edge of the area light rectangle.
     * @param vVector
     *   The direction and magnitude of the v edge of the area light rectangle.
     * @param uStep
     *   The number of segments the u vector is divided into.
     * @param vStep
     *   The number of segments the v vector is divided into.
     * @param generator
     *   Supplier of sample point offsets. Supplied floats between 0 and 1.
     */
    public AreaLight(Color color, Tuple corner, Tuple uVector, Tuple vVector, int uStep, int vStep, Supplier<Double> generator) {
        this(color, corner, uVector, vVector, uStep, vStep, vVector.divide(vStep), uVector.divide(uStep), 
          corner.add(vVector.multiply((0.5)).add(uVector.multiply(0.5))), generator
        );
    }

    /**
     * Canonical constructor. Call a convience constructor instead.
     * 
     * @param color
     *   The color of the area light, magnitude of the color doubles as the light intensity.
     * @param corner
     *   The world space location of the area light corner.
     *   The uv vectors define which corner of the area light rectangle the corner belongs to.
     * @param uVector
     *   The direction and magnitude of the u edge of the area light rectangle.
     * @param vVector
     *   The direction and magnitude of the v edge of the area light rectangle.
     * @param uStep
     *   The number of segments the u vector is divided into.
     * @param vStep
     *   The number of segments the v vector is divided into.
     * @param vUnit
     *   A unit vector of length vVector.magnitue / vStep to move one segment along the v edge.
     * @param uUnit
     *   A unit vector of length uVector.magnitue / uStep to move one segment along the u edge.
     * @param generator
     *   Supplier of sample point offsets. Supplied floats between 0 and 1.
     */
    public AreaLight(Color color, Tuple corner, Tuple uVector, Tuple vVector, int uStep, int vStep, Tuple vUnit, Tuple uUnit, Tuple position, Supplier<Double> generator) {
        if (uStep == 0) throw new IllegalArgumentException("area light cannot contain 0 u segments");
        if (vStep == 0) throw new IllegalArgumentException("area light cannot contain 0 v segments");
        this.color = color; this.corner = corner; this.uVector = uVector; this.vVector = vVector;
        this.uStep = uStep; this.vStep = vStep; this.vUnit = vUnit; this.uUnit = uUnit; this.position = position; this.generator = generator;
    }

    /**
     * The resolution of the area that is sampled for shadow tests by the area light.
     * @return
     *   The resolution of the area light.
     */
    public int resolution() { return uStep * vStep; }

    /**
     * Get a point on the surface of the area light.
     *
     * @param u
     *   The distance along the u edge of the light.
     * @param v
     *   The distance along the v edge of the light.
     * @return
     *   The world space position of the point located at uv on the light surface.
     */
    public Tuple sample(double u, double v) {
        return corner.add(uUnit.multiply(generator.get() + u)).add(vUnit.multiply(generator.get() + v));
    }

    @Override
    public double intensityAt(Tuple point, World world, double time) {
        float accumulator = 0f;
        for (int u = 0; u < uStep; u++) {
            for (int v = 0; v < vStep; v++) {
                final var sample = sample(u, v);
                final var isShadowed = world.isOccluded(point, sample, World.shadowCasters, time);
                accumulator += isShadowed ? 0f : 1f;
            }
        }
        return accumulator / (double) resolution();
    }
    
}
