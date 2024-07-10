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
 * Common light operations needed by the phong lighting model.
 * 
 * @author BudgiePanic
 */
public interface Light {
    /**
     * Get the position of the light.
     * @return
     *   The position of the light.
     */
    Tuple position();

    /**
     * Get the color of the light.
     *
     * @return
     *   The color of the light.
     */
    Color color();

    /**
     * How much light from this light is reaching the point in the world.
     *
     * @param point
     *     The point being tested.
     * @param world
     *     The world the point is within.
     * @param time
     *     The time in the world when the intensity if calculated
     * @return
     *     The amount of light reaching the point.
     */
    double intensityAt(Tuple point, World world, double time);

    /**
     * An iterator that returns sample points that cover the entire light.
     * @return
     *   A new iterator over the light's volume.
     */
    Iterator<Tuple> sampler();

    /**
     * Get the number of segments in the light.
     *
     * @return
     *   The number of segments that can be sampled from this light.
     */
    int resolution();
}
