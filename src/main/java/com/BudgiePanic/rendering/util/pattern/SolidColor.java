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
