package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * functional interface to isolate the pattern logic from the pattern state.
 * 
 * @author BudgiePanic
 */
public interface BiOperation {
    
    /**
     * Combine two pattern's output together.
     * 
     * @param point
     *   A point in local Pattern space, it has already been passed through the pattern transform
     * @param transform
     *   the transform of the pattern that is being invoked on the sub patterns. (identity transform most of the time)
     * @param a
     *   the first pattern 
     * @param b
     *   the second pattern
     * @return
     *   The combination of pattern a and b when sampled at location 'point'
     */
    Color colorAt(Tuple point, Matrix4 transform, Pattern a, Pattern b);

    // Premade BiOperations ready for use.

    /**
     * The stripe pattern alternates between two colors as the x coordinate moves along the pattern.
     */
    static BiOperation stripe = (point, transform, a, b) -> {
        if (((int)Math.floor(point.x)) % 2 == 0) {
            return a.colorAt(point, transform);
        } else {
            return b.colorAt(point, transform);
        }
    };

    /**
     * Stripes between two colors along the xz dimensions
     */
    static BiOperation ring = (point, transform, a, b) -> {
        if (Math.floor(Math.sqrt((point.x * point.x) + (point.z * point.z))) % 2 == 0) {
            return a.colorAt(point, transform);
        } else {
            return b.colorAt(point, transform);
        }
    };

    /**
     * Linearly interpolate between pattern a and b.
     */
    static BiOperation gradient = (point, transform, a, b) -> {
        final var colorA = a.colorAt(point, transform);
        final var colorB = b.colorAt(point, transform);
        final var spectrum = colorB.subtract(colorA);
        final float amout = point.x - (float) Math.floor(point.x); 
        return new Color(colorA.add(spectrum.multiply(amout)));
    };

    /**
     * Checker pattern alternates between two colors in the xyz dimensions ensuring no two adjacent squares are the same color.
     */
    static BiOperation checker = (point, transform, a, b) -> {
        final var value = ((Math.floor(point.x) + Math.floor(point.y) + Math.floor(point.z)) % 2.0);
        if (FloatHelp.compareFloat((float) value, 0) == 0) {
            return a.colorAt(point, transform);
        } else {
            return b.colorAt(point, transform);
        }
    };
}
