package com.BudgiePanic.rendering.util.pattern;

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

    @Override
    public Matrix4 transform() { return transform; }
    
}
