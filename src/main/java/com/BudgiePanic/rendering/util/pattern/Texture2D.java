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
