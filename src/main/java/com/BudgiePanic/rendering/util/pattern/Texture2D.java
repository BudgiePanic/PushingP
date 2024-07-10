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

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;

/**
 * Image based 2D pattern. Supplied to TextureMap and CubeTextureMap patterns for sampling.
 * @author BudgiePanic
 */
public final record Texture2D(Canvas texture) implements Pattern2D {

    @Override
    public Color sample(double u, double v) {
        v = 1.0 - v;
        int x = (int) Math.round(u * (texture.getWidth() - 1));
        int y = (int) Math.round(v * (texture.getHeight() - 1));
        return texture.getPixel(x, y);
    }
    
}
