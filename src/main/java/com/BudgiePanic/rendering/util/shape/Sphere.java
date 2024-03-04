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
 * 
 * @author BudgiePanic
 */
public class Sphere extends BaseShape {
    /**
     * A sphere with default properties. Useful for testing.
     * 
     * @return
     *     a new sphere of radius 1 at [0,0,0] with the default material.
     */
    public static Sphere defaultSphere() {
      return new Sphere(Matrix4.identity(), Material.defaultMaterial());
    }
    
    /**
     * A sphere that has glass like material properties.
     * @return
     *   A new sphere of radius 1 at [0,0,0] with a glass material.
     */
    public static Sphere defaultGlassSphere() {
      return new Sphere(Matrix4.identity(), Material.defaultMaterial().setTransparency(1f).setRefractiveIndex(1.5f));
    }

    public Sphere(Matrix4 transform, Material material) {
      super(transform, material);
    }

    public Sphere(Matrix4 transform) {
      super(transform);
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
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

    /**
     * Determine the normal of at 'point' on this sphere.
     * 
     * @param point
     *   A point on the sphere, in world space.
     * @return
     *   A new vector representing the normal of the sphere at the given point.
     */
    @Override
    protected Tuple localNormal(Tuple point) {
      return point.subtract(origin).normalize(); // the normal in object space
    }

    /**
     * The unit sphere has a constant AABB.
     */
    protected static final BoundingBox AABB = new BoundingBox(Tuple.makePoint(-1, -1, -1), Tuple.makePoint(1, 1, 1));

    @Override
    public BoundingBox bounds() {
      return AABB;
    }
  
}
