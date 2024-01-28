package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Information container for sphere objects.
 * Spheres can be uniquely identified by their memory reference.
 * NOTE: in the future, if you wanted to speed the ray intersection test up, we could convert
 *       this from a record to a class, and cache the inverse transform, we only need to recalculate it
 *       if the transform is updated (dirty flag), because right now, it recalculates the inverse with every 
 *       call to ray. (Or maybe the matrix itself can cache the inverse?)
 * 
 * NOTE: in the future, if we want to have scene objects that are composed of components
 *       then the material property can be stripped from the sphere. but currently we are 
 *       going to enforce that all spheres (shapes) have to have a material.
 * 
 * @author BudgiePanic
 */
public record Sphere(Matrix4 transform, Material material) implements Shape {
    
    public static Sphere defaultSphere() {
      return new Sphere(Matrix4.identity(), Material.defaultMaterial());
    }

    public Sphere {
        if (transform == null) throw new IllegalArgumentException("sphere transform cannot be null");
        if (material == null) throw new IllegalArgumentException("sphere material cannot be null");
    }

    public Sphere(Matrix4 transform) {
      this(transform, Material.defaultMaterial());
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
    @Override
    public Optional<List<Intersection>> intersect(Ray ray) {
        // Compute discriminant, if 0, then there is no intersection
          // A vector going from the sphere origin to the ray origin
        var transformInverse = this.transform.inverse();
        var rayInObjectSpace = ray.transform(transformInverse);
        var sphereToRay = rayInObjectSpace.origin().subtract(origin); 
          // some dot products
        var dotA = rayInObjectSpace.direction().dot(rayInObjectSpace.direction());
        var dotB = 2.0f * rayInObjectSpace.direction().dot(sphereToRay);
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

    /**
     * Determine the normal of at 'point' on this sphere.
     * 
     * @param point
     *   A point on the sphere, in world space.
     * @return
     *   A new vector representing the normal of the sphere at the given point.
     */
    @Override
    public Tuple normal(Tuple point) {
      if (point == null) throw new IllegalArgumentException("point is null");
      // optimized technique described in in Jamis Buck's book The Ray Tracer Challenge
      var inverse = this.transform.inverse();
      var pointObject = inverse.multiply(point); // the point in object space
      var normalObject = pointObject.subtract(Tuple.makePoint()).normalize(); // the normal in object space
      var normalWorld = inverse.transpose().multiply(normalObject); // the normal in world space (optimization here)
      normalWorld = Tuple.makeVector(normalWorld.x, normalWorld.y, normalWorld.z); // hacky step here due to the optimization
      return normalWorld.normalize();
    }
  
}
