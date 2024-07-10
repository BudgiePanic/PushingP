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
package com.BudgiePanic.rendering.util.intersect;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Ray type.
 * 
 * @author BudgiePanic
 */
public record Ray (Tuple origin, Tuple direction, Double time) {

    /**
     * The default time the ray is cast, used if the convenience constructor is called.
     */
    protected static final Double defaultTime = Double.valueOf(0);

    /**
     * Canonical ray constructor. Create a new ray.
     * @param origin
     *   The origin point of the ray.
     * @param direction
     *   The direction vector of the ray.
     * @param time
     *   The time when the ray was cast, measured from the start of the camera exposure.
     */
    public Ray {
        if (origin == null || direction == null) throw new IllegalArgumentException("Ray cannot take null parameters");
        if (origin.isVector()) throw new IllegalArgumentException("origin should be point");
        if (direction.isPoint()) throw new IllegalArgumentException("direction should be vector");
    }

    /**
     * Convenience constructor. Create a new ray cast at time zero.
     *
     * @param origin
     *   The origin point of the ray.
     * @param direction
     *   The direction vector of the ray.
     */
    public Ray(Tuple origin, Tuple direction) {
        this(origin, direction, defaultTime);
    }

    /**
     * Calculates a point along a ray.
     *
     * @param delta
     *   The time to move along the ray
     * @return
     *   A point along the ray at distance delta.
     */
    public Tuple position(double delta) {
        return this.origin.add(this.direction.multiply(delta));
    }

    /**
     * Create a new ray, transformed by the matrix.
     *
     * @param matrix
     *   The new transform matrix.
     * @return
     *   A new ray after it was passed through the matrix transform
     */
    public Ray transform(Matrix4 matrix) {
        return new Ray(matrix.multiply(this.origin), matrix.multiply(this.direction()), this.time());
    }
}
