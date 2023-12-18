package com.BudgiePanic.rendering.util.intersect;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Ray type.
 * 
 * @author BudgiePanic
 */
public record Ray (Tuple origin, Tuple direction) {

    public Ray {
        if (origin == null || direction == null) throw new IllegalArgumentException("Ray cannot take null parameters");
        if (origin.isVector()) throw new IllegalArgumentException("origin should be point");
        if (direction.isPoint()) throw new IllegalArgumentException("direction should be vector");
    }

    /**
     * Calculates a point along a ray.
     *
     * @param delta
     *   The time to move along the ray
     * @return
     *   A point along the ray at distance delta.
     */
    public Tuple position(float delta) {
        return this.origin.add(this.direction.multiply(delta));
    }

    /**
     * Create a new ray, transformed by the matrix.
     *
     * @param matrix
     *   The new transform matrix.
     * @return
     *   A new ray after it was passed through the matrix transform
     */
    public Ray transform(Matrix4 matrix) {
        return new Ray(matrix.multiply(this.origin), matrix.multiply(this.direction()));
    }
}
