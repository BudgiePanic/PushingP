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

import java.util.function.Function;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Drop in pattern to have a solid color in a matertial.
 * Used as a wrapper in Material 
 * 
 * @author BudgiePanic
 */
public record SolidColor(Color color) implements Pattern {

    private static final Matrix4 transform = Matrix4.identity();

    @Override
    public Color colorAt(Tuple point) { return color(); }

    // Solid Color doesn't care about any transforms...
    @Override
    public Color colorAt(Tuple point, Function<Tuple, Tuple> toObjectSapce) { return colorAt(point); }

    @Override
    public Matrix4 transform() { return transform; }
    
}
