package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The gradient pattern linearly interpolates between two colors.
 * 
 * @author BudgiePanic
 */
public record Gradient(Color a, Color b, Matrix4 transform) implements Pattern {

    // every 'pattern' is going to follow this pattern constructor, violating DRY principles, oh well...
    public Gradient(Color a, Color b) {
        this(a, b, Matrix4.identity());
    }

    @Override
    public Color colorAt(Tuple point) {
        final var spectrum = b.subtract(a);
        final float amout = point.x - (float) Math.floor(point.x); 
        return new Color(a.add(spectrum.multiply(amout)));
    }
}
