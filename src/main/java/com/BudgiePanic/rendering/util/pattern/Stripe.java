package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The stripe pattern alternates between two colors as the x coordinate moves along the pattern.
 * 
 * @param a 
 *   The first color
 * @param b 
 *   The second color
 * 
 * @author BudgiePanic
 */
public record Stripe(Color a, Color b, Matrix4 transform) implements Pattern {

    public Stripe(Color a, Color b) {
        this(a, b, Matrix4.identity());
    }

    @Override
    public final Color colorAt(Tuple point) {
        if (((int)Math.floor(point.x)) % 2 == 0) {
            return a();
        } else {
            return b();
        }
    }
    
}
