package com.BudgiePanic.rendering.util.intersect;

import java.util.Optional;

import com.BudgiePanic.rendering.util.shape.Sphere;

/**
 * Ray intersection information container.
 * 
 * @param a 
 *   The distance from the ray origin to the first point of intersection
 * @param b
 *   The distance from the ray origin to the second point of intersection, if any.
 * @param sphere
 *   The sphere that was intersected with
 * 
 * @author BudgiePanic
 */
public record Intersection(Float a, Optional<Float> b, Sphere sphere) {
    
}
