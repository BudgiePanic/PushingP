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

import java.util.Optional;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.ShadingInfo;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * Implementation of Phong reflection model.
 * 
 * @author BudgiePanic
 */
public final class Phong {

    private Phong() {}

    /**
     * Convience method to unwrap lighting information before passing off to Phong::compute implementation.
     *
     * @param info
     *   Information about the point being illuminated
     * @param light
     *   The light that is illuminating the point
     * @param intensity
     *   the intensity of the light source
     * @return
     *   The color of the point described in the shading information record.
     */
    public static Color compute(ShadingInfo info, Light light, double intensity) {
        return compute(info.shape().material(), light, info.overPoint(), info.eyeVector(), info.normalVector(), intensity, Optional.of(info.shape()));
    }

    /**
     * Uses the Phong lighting model to compute the color of a point using material properties.
     * 
     * NOTE: it may be beneficial to add a lightmodel interface abstraction, which Phong can implement
     *       to allow other lighting models to be hot swapped into a scene.
     * 
     * @param light
     *   The scene light
     * @param position
     *   The point being lit
     * @param eye
     *   The camera look vector
     * @param normal
     *   The normal of the surface at the position
     * @return
     *   The color at point 'position'
     */
    public static Color compute(Material material, Light light, Tuple position, Tuple eye, Tuple normal) {
        return compute(material, light, position, eye, normal, 1.0, Optional.empty());
    }

    /**
     * Uses the Phong lighting model to compute the color of a point using the material properties.
     * 
     * @param light
     *   The light that is illuminating the position
     * @param position
     *   The point being lit
     * @param eye
     *   the position of the observer
     * @param normal
     *   surface normal at 'position'
     * @param intensity
     *   the intensity of the light source
     * @param shape
     *   the shape that the surface of the point being lit belongs to, if any
     * @return
     *   The color at point 'position'
     */
    public static Color compute(Material material, Light light, Tuple position, Tuple eye, Tuple normal, double intensity,  Optional<Shape> shape) {
        final var pattern = material.pattern();
        final var color = shape.map(sh -> pattern.colorAt(position, sh::toObjectSpace)).orElseGet(()->pattern.colorAt(position));
        assert color != null;
        final var effective = color.colorMul(light.color());
        final var ambient = effective.multiply(material.ambient());
        if (FloatHelp.compareFloat(0, intensity) != -1) {
            return ambient;
        }   
        final var sampler = light.sampler();
        var accumulator = new Color();
        while (sampler.hasNext()) {
            final var sample = sampler.next();
            final var directionToLight = sample.subtract(position).normalize();
            final var lightNormalAngle = directionToLight.dot(normal);
            if (lightNormalAngle < 0.0) {
                continue;
            }
            final Color diffuse = effective.multiply(material.diffuse()).multiply(lightNormalAngle);
            final var reflection = directionToLight.negate().reflect(normal);
            final var eyeReflectAngle = reflection.dot(eye);
            if (eyeReflectAngle < 0.0) {
                accumulator = accumulator.add(diffuse);
                continue;
            }
            final var factor = Math.pow(eyeReflectAngle, material.shininess());
            final var specular = light.color().multiply(material.specular()).multiply(factor);
            accumulator = accumulator.add(specular);
            accumulator = accumulator.add(diffuse);
        }
        return ambient.add(accumulator.divide(light.resolution()).multiply(intensity));
    }

}
 