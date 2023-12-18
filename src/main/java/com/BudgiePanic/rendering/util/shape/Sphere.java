package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Information container for sphere objects.
 * Spheres can be uniquely identified by their memory reference.
 * 
 * @author BudgiePanic
 */
public record Sphere(Matrix4 transform) {
    
    public static Sphere defaultSphere() {
      return new Sphere(Matrix4.identity());
    }

    public Sphere {
        if (transform == null) throw new IllegalArgumentException("sphere transform cannot be null");
    }

    /**
     * All spheres have the same origin in OBJECT space.
     * Rays will be converted from world space to object space before intersection tests 
     * are performed. Making a singleton here to avoid allocating a new object everytime an intersection test
     * is performed.
     */
    private static final Tuple origin = Tuple.makePoint();

    /**
     * Determines the distance to intersection points between a ray and this sphere, if any.
     * 
     * @param ray
     *   The ray to test against.
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    public Optional<List<Intersection>> intersect(Ray ray) {
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
            return Optional.empty();
        }
        var sqrtDiscriminant = (float) Math.sqrt(discriminant);
        var intersectA = (-dotB - sqrtDiscriminant) / (2f * dotA);
        var intersectB = (-dotB + sqrtDiscriminant) / (2f * dotA);

        return Optional.of(
                  List.of(
                    new Intersection(Float.valueOf(intersectA), this),
                    new Intersection(Float.valueOf(intersectB), this)
                  )
                ); 
    }

    public Sphere setTransform(Matrix4 transform) {

    }
  
}
