package com.BudgiePanic.rendering.util.shape;

import java.util.List;

import com.BudgiePanic.rendering.util.Ray;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Information container for sphere objects.
 * Spheres can be uniquely identified by their memory reference.
 * 
 * @author BudgiePanic
 */
public record Sphere(Tuple origin, float radius) {
    
    public Sphere {
        if (origin == null) throw new IllegalArgumentException("origin cannot be null");
        if (origin.isVector()) throw new IllegalArgumentException("origin should be point");
    }

    /**
     * Determines the distance to intersection points between a ray and this sphere, if any.
     * TODO in the future, if needed, this could be refactored to return a record, that holds two Optional<Float> for intersect A and intersect B
     * 
     * @param ray
     *   The ray to test against.
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    public List<Float> intersect(Ray ray) {
        // Compute discriminant, if 0, then there is no intersection
          // A vector going from the sphere origin to the ray origin
        var sphereToRay = ray.origin().subtract(origin); 
          // some dot products
        var dotA = ray.direction().dot(ray.direction());
        var dotB = 2.0f * ray.direction().dot(sphereToRay);
        var dotC = sphereToRay.dot(sphereToRay) - 1.0f;
          // This looks like the discriminant from the quadratic equation solution forumla
        var discriminant = (dotB * dotB) - 4.0f * dotA * dotC; 
        if (discriminant < 0f) {
            return List.of();
        }
        var sqrtDiscriminant = (float) Math.sqrt(discriminant);
        var intersectA = (-dotB - sqrtDiscriminant) / (2f * dotA);
        var intersectB = (-dotB + sqrtDiscriminant) / (2f * dotA);

        return List.of(Float.valueOf(intersectA), Float.valueOf(intersectB));
    }

}
