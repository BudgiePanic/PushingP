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

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * A point light that casts light out in a cone with a direction.
 *
 * @author BudgiePanic
 */
public record PointSpotLight(Tuple position, Color color, Tuple direction, double innerAngle, double coneAngle) implements Light {

    /**
     * Create a new point spotlight. casts hard shadows.
     *
     * @param position
     *   The location of the spotlight in the world
     * @param color
     *   The color of the spotlight
     * @param direction
     *   A vector direction that the light points at
     * @param innerAngle
     *   The angle at which the light stops being full intensity in radians
     * @param coneAngle
     *   The angle of the cone of light emitted by the spotlight in radians
     */
    public PointSpotLight {}

    private final class SpotLightIterator implements Iterator<Tuple> {
        int count = 0;
        @Override
        public boolean hasNext() { return count < 1; }
        @Override
        public Tuple next() {
            if (!hasNext()) throw new NoSuchElementException();
            count++;
            return position;
        }
    }

    @Override
    public double intensityAt(Tuple point, World world, double time) {
        final var isShadowed = world.isOccluded(point, position, World.shadowCasters, time);
        if (isShadowed) { return 0.0; }
        final Tuple lightToPoint = point.subtract(position);
        final var normDot = (direction.dot(lightToPoint)) / (direction.magnitude() * lightToPoint.magnitude());
        final double angle = Math.acos(normDot);
        if (FloatHelp.compareFloat(angle, innerAngle) != 1) { return 1.0; } // angle <= innerAngle
        if (FloatHelp.compareFloat(angle, coneAngle) == 1) { return 0.0; } // angle > coneAngle
        // angle must be between inner angle and cone angle
        // need to LERP between 1 and zero based on how close the angle is to innerAngle
        final double oldMin = innerAngle, oldMax = coneAngle, newMin = 0.0, newMax = 1.0;
        final double invIntensity = ((angle - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
        return 1.0 - invIntensity;
    }

    @Override
    public Iterator<Tuple> sampler() { return new PointSpotLight.SpotLightIterator(); }

    @Override
    public int resolution() { return 1; }
}
