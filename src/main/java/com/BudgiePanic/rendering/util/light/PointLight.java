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

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Collection of data representing a point light, a sizeless light in space.
 * 
 * The color also represents the intensity of the point light.
 * 
 * @author BudgiePanic
 */
public record PointLight(Tuple position, Color color) implements Light {

    @Override
    public double intensityAt(Tuple point, World world, double time) {
        final boolean inShadow = world.isOccluded(point, position, World.shadowCasters, time);
        return inShadow ? 0.0 : 1.0;
    }

    @Override
    public Iterator<Tuple> sampler() {
        return new Iterator<Tuple>() {
            protected boolean once = true;
            @Override
            public boolean hasNext() { return once; }
            @Override
            public Tuple next() {
                once = false;
                return PointLight.this.position;
            }
            
        };
    }

    @Override
    public int resolution() { return 1; }
    
}
