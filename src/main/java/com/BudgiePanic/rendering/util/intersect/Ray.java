package com.BudgiePanic.rendering.util.intersect;

import com.BudgiePanic.rendering.util.Tuple;

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
}