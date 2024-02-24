package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * An xz infinite plane.
 * 
 * @author BudgiePanic
 */
public final class Plane extends BaseShape {

    /**
     * On a flat surface the normal is constant and never changes.
     * Because we work in object space, the normal simply points up.
     */
    private static final Tuple normal = Tuple.makeVector(0,1,0);

    /**
     * Construct a new Plane with default material.
     *
     * @param transform
     *   Transform to get to this plane's local object space.
     */
    public Plane(Matrix4 transform) {
        super(transform);
    }

    /**
     * Construct a new Plane.
     *
     * @param transform
     *   Transform to get to this plane's local object space.
     * @param material
     *   The plane's material for shading calculations.
     */
    public Plane(Matrix4 transform, Material material) {
        super(transform, material);
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // precondition check
        if (ray == null) {throw new IllegalArgumentException("ray is null");}
        if (checkParallelCoplanar(ray)) {
            return Optional.empty();
        }
        // this algorithm assumes an xz plane with normal = [0,1,0]
        float distanceToIntersection = -ray.origin().y / ray.direction().y;
        return Optional.of(List.of(new Intersection(distanceToIntersection, this)));
    }

    /**
     * A parallel ray will never intersect with the plane.
     * A coplanar ray will intersect an infinite amount of times with the plane, but the plane is infinitely thin, so an observer would never see the intersections,
     * so it is logically equivalent to having no intersections.
     * 
     * @param ray
     *   The ray to check against
     * @return
     *   True if the ray is parallel or coplanar with the plane in local space
     */
    private boolean checkParallelCoplanar(Ray ray) {
        return FloatHelp.compareFloat(ray.direction().y, 0) == 0;
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        return normal;
    }

    /**
     * The infinite plane has a constant AABB.
     */
    protected static final BoundingBox AABB = new BoundingBox(
        Tuple.makePoint(Float.NEGATIVE_INFINITY, 0, Float.NEGATIVE_INFINITY), 
        Tuple.makePoint(Float.POSITIVE_INFINITY, 0, Float.POSITIVE_INFINITY)
    );

    @Override
    public BoundingBox bounds() {
        return AABB;
    }
    
}
