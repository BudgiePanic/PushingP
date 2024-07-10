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

/**
 * A pattern that uses texture mapping.
 *
 * @author BudgiePanic
 */
public record TextureMap(Pattern2D pattern, CoordinateMapper mapper, Matrix4 transform) implements Pattern {

    /**
     * Canonical constructor.
     * @param pattern
     *   The 2D pattern to sample.
     * @param mapper
     *   The coordinate system transfer function.
     * @param transform
     *   The pattern space transform. Should be the identity matrix because CoordinateMappers work in local object space.
     */
    public TextureMap {}

    /**
     * Convenience constructor. Uses the identity transform.
     * @param pattern
     *   The 2D pattern to sample.
     * @param mapper
     *   The pattern space transform.
     */
    public TextureMap(Pattern2D pattern, CoordinateMapper mapper) {
        this(pattern, mapper, Matrix4.identity());
    }

    @Override
    public Color colorAt(Tuple point) {
        final double u = mapper.uMap(point);
        final double v = mapper.vMap(point);
        return pattern.sample(u, v);
    }

}
