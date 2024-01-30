package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Checker pattern alternates between two colors in the xyz dimensions ensuring no two adjacent squares are the same color.
 * 
 * @author BudgiePanic
 */
public record Checker(Color a, Color b, Matrix4 transform) implements Pattern {

    public Checker(Color a, Color b) {
        this(a, b, Matrix4.identity());
    }

    @Override
    public Color colorAt(Tuple point) {
        final int value = (int) (Math.floor(point.x) + Math.floor(point.y) + Math.floor(point.z));
        if (value % 2 == 0) {
            return a();
        } else {
            return b();
        }
    }
    
}
