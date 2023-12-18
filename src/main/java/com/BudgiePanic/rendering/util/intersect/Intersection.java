package com.BudgiePanic.rendering.util.intersect;

import java.util.Collection;
import java.util.Optional;

import com.BudgiePanic.rendering.util.shape.Sphere;

/**
 * Ray intersection information container.
 * 
 * @param a 
 *   The distance from the ray origin to the point of intersection
 * @param sphere
 *   The sphere that was intersected with
 * 
 * @author BudgiePanic
 */
public record Intersection(Float a, Sphere sphere) {
    
    /**
     * Helper method to find hits among collections of intersections.
     * Some intersections may be behind the ray, and are thus not hits.
     * 
     * @param intersections
     *   The intersections from a ray-object intersection test
     * @return
     *   The first visible intersection point, as viewed from the Ray's origin, if any.
     */
    public static Optional<Intersection> Hit(Collection<Intersection> intersections) {
        final var zero = Float.valueOf(0f);
        var result = intersections.stream().
        filter((intersect)->{
            return !(intersect.a() <= zero);
        }).sorted((i1, i2)->{
            return i1.a().compareTo(i2.a());
        }).findFirst();
        return result;
    }

}
