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

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * Lighting data container that accompanies intersection information to be used in shading calculations.
 * 
 * @author BudgiePanic
 */
public record ShadingInfo(double a, Shape shape, Tuple point, Tuple eyeVector, Tuple normalVector, boolean intersectInside, Tuple reflectVector, double n1, double n2, double time) {

    /**
     * Canonical constructor. Information needed by the lighting model to determine the color of a point in space.
     * @param a
     *   the distance along the ray that intersected to reach 'point'
     * @param shape
     *   the object that the ray hit
     * @param point
     *   the point in 3D space where the ray hit the shape
     * @param eyeVector
     *   a vector looking in the opposite direction as the ray
     * @param normalVector
     *   the surface normal on the shape at the point where the ray hit the shape
     * @param intersectInside
     *   flag indicating that the ray-shape intersection occured inside of the shape
     * @param reflectVector
     *   the direction the light ray bounced to after hitting this shape.
     * @param n1
     *   refractive index of material that was exited during refraction
     * @param n2
     *   refractive index of material that was entered during refraction
     * @param time
     *   the time during image exposure that this shading info was generated
     */
    public ShadingInfo {}

    /**
     * Calculate a point slightly above the surface that is being shaded, to avoid floating point precision errors.
     *
     * @return
     *   A point above the surface slightly along the normal direction.
     */
    public Tuple overPoint() {
        return point.add(normalVector.multiply(FloatHelp.epsilon));
    }

    /**
     * Calculate a point slightly below the surface that is being shaded.
     *
     * @return
     *   A point below the surface slightly along the normal direction.
     */
    public Tuple underPoint() {
        return point.subtract(normalVector.multiply(FloatHelp.epsilon));
    }

    /**
     * Determine what fraction of light is reflected given the shading information.
     * 
     * @return
     *   The reflectance of this point given the shading information. A value between 0 and 1.
     */
    public double schlick() {
        // when theta eye:surface is large -> reflected light is small  _|
        // when theta eye:surface is small -> reflected light is larger _\
        var cos = this.eyeVector.dot(this.normalVector);
        if (this.n1 > this.n2) {
            final var n = this.n1 / this.n2; // snell's law, this has been repeated twice (world.java), could refactor out into snells helper class?
            final var sinThetaSquared = (n*n) * (1.0-(cos*cos));
            if (sinThetaSquared > 1) {
                // total internal reflection occured, return maximum reflectance.
                return 1.0f;
            }
            final var cosTheta = Math.sqrt(1.0 - sinThetaSquared);
            cos = cosTheta;
        }
        // book author doesn't explain the math here: refers to 
        // (https://graphics.stanford.edu/courses/cs148-10-summer/docs/2006--degreve--reflection_refraction.pdf)
        // which explains the maths behind schilck approximation for reflectance
        final var x = 1.0 - cos; 
        var r0 = ((this.n1 - this.n2) / (this.n1 + this.n2));
        r0 *= r0;
        return r0 + (1.0 - r0) * (x * x * x * x * x); 
    }
}
