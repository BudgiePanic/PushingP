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
 * BiPattern combines two sub patterns together.
 * 
 * @author BudgiePanic
 */
public record BiPattern(BiOperation operation, Pattern a, Pattern b, Matrix4 transform) implements Pattern {

    /**
     * Convience constructor, auto populates transform with the identity matrix.
     *
     * @param operation
     *   The pattern combination to use
     * @param a
     *   The first pattern
     * @param b
     *   The second pattern
     */
    public BiPattern(BiOperation operation, Pattern a, Pattern b) {
        this(operation, a, b, Matrix4.identity());
    }

    /**
     * Convienience constructor, auto wraps colors in a solid color pattern.
     *
     * @param operation
     *   The pattern combination to use
     * @param a
     *   The first pattern
     * @param b
     *   The second pattern
     * @param transform
     *   The modification to the pattern
     */
    public BiPattern(BiOperation operation, Color a, Color b, Matrix4 transform) {
        this(operation, new SolidColor(a), new SolidColor(b), transform);
    }

    /**
     * Convienience constructor, auto wraps colors in a solid color pattern. Autofills transform with identity.
     *
     * @param operation
     *   The pattern combination to use
     * @param a
     *   The first pattern
     * @param b
     *   The second pattern
     */
    public BiPattern(BiOperation operation, Color a, Color b) {
        this(operation, a, b, Matrix4.identity());
    }


    @Override
    public Color colorAt(Tuple point) {
        return operation.colorAt(point, transform, a, b);
    }
    
}
