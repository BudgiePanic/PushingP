package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * A pattern that uses texture mapping.
 *
 * @author BudgiePanic
 */
public record TextureMap(Pattern2D pattern, CoordinateMapper mapper, Matrix4 transform) implements Pattern {

    /**
     * Canonical constructor.
     * @param pattern
     *   The 2D pattern to sample.
     * @param mapper
     *   The coordinate system transfer function.
     * @param transform
     *   The pattern space transform.
     */
    public TextureMap {}

    /**
     * Convenience constructor. Uses the identity transform.
     * @param pattern
     *   The 2D pattern to sample.
     * @param mapper
     *   The pattern space transform.
     */
    public TextureMap(Pattern2D pattern, CoordinateMapper mapper) {
        this(pattern, mapper, Matrix4.identity());
    }

    @Override
    public Color colorAt(Tuple point) {
        final double u = mapper.uMap(point);
        final double v = mapper.vMap(point);
        return pattern.sample(u, v);
    }

}
