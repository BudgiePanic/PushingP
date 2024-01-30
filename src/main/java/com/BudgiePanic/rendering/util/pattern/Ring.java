package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Stripes between two colors along the xz dimensions
 * 
 * @author BudgiePanic
 */
public record Ring(Color a, Color b, Matrix4 transform) implements Pattern {

    public Ring(Color a, Color b) {
        this(a, b, Matrix4.identity());
    }

    @Override
    public Color colorAt(Tuple point) {
        if (Math.floor(Math.sqrt((point.x * point.x) + (point.z * point.z))) % 2 == 0) {
            return a();
        } else {
            return b();
        }
    }
    
}
