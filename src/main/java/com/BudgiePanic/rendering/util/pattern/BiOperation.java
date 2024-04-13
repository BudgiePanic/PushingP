package com.BudgiePanic.rendering.util.pattern;

import java.util.function.Function;

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

    /**
     * Creates a function that multiplies point's by the matrix's inverse.
     * @param transform
     *   The matrix to be inverted.
     * @return
     *   A function that multiplies the input tuple by the supplied matrix's inverse.
     */
    static Function<Tuple, Tuple> toLocalSpace(Matrix4 transform) { return (p)->transform.inverse().multiply(p); }

    // Premade BiOperations ready for use.

    /**
     * The stripe pattern alternates between two colors as the x coordinate moves along the pattern.
     */
    static final BiOperation stripe = (point, transform, a, b) -> {
        if (((int)Math.floor(point.x)) % 2 == 0) {
            return a.colorAt(point, toLocalSpace(transform));
        } else {
            return b.colorAt(point, toLocalSpace(transform));
        }
    };

    /**
     * Stripes between two colors along the xz dimensions
     */
    static final BiOperation ring = (point, transform, a, b) -> {
        if (Math.floor(Math.sqrt((point.x * point.x) + (point.z * point.z))) % 2 == 0) {
            return a.colorAt(point, toLocalSpace(transform));
        } else {
            return b.colorAt(point, toLocalSpace(transform));
        }
    };

    /**
     * Linearly interpolate between pattern a and b.
     */
    static final BiOperation gradient = (point, transform, a, b) -> {
        final var colorA = a.colorAt(point, toLocalSpace(transform));
        final var colorB = b.colorAt(point, toLocalSpace(transform));
        final var spectrum = colorB.subtract(colorA);
        final double amout = point.x - Math.floor(point.x); 
        return new Color(colorA.add(spectrum.multiply(amout)));
    };

    

    /**
     * Checker pattern alternates between two colors in the xyz dimensions ensuring no two adjacent squares are the same color.
     */
    static final BiOperation checker = (point, transform, a, b) -> {
        final var value = ((Math.floor(point.x) + Math.floor(point.y) + Math.floor(point.z)) % 2.0);
        if (FloatHelp.compareFloat((float) value, 0) == 0) {
            return a.colorAt(point, toLocalSpace(transform));
        } else {
            return b.colorAt(point, toLocalSpace(transform));
        }
    };

    /**
     * Gradient that radially spreads out from the origin in a circle.
     */
    static final BiOperation radialGradient = (point, transform, a, b) -> {
        final var colorA = a.colorAt(point, toLocalSpace(transform));
        final var colorB = b.colorAt(point, toLocalSpace(transform));
        final var spectrum = colorB.subtract(colorA);
        final var distance = Math.sqrt((point.x * point.x) + (point.z * point.z));
        final float amout = (float) distance - (float) Math.floor(distance); 
        return new Color(colorA.add(spectrum.multiply(amout)));
    };

    /**
     * Blends the output of two patterns together.
     */
    static final BiOperation blend = (point, transform, a, b) -> { 
        final var function = toLocalSpace(transform);
        return a.colorAt(point, function).colorMul(b.colorAt(point, function)); 
    };

    /**
     * Add the output of two patterns together.
     */
    static final BiOperation add = (point, transform, a, b) -> {
        final var function = toLocalSpace(transform);
        return a.colorAt(point, function).add(b.colorAt(point, function));
    };
}
