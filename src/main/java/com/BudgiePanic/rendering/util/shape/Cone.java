package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Double napped cone.
 * 
 * @author BudgiePanic
 */
public class Cone extends BaseShape {

    /*
     *   +y
     * -------
     *  \   /
     *   \ /
     *    *
     *   / \
     *  /   \
     * -------
     *    -y
     */

    /**
     * The lower bound of the cone height.
     */
    protected final float minimum;
    /**
     * The upper bound of the cone height.
     */
    protected final float maximum;
    /**
     * Does the cone have end caps?
     */
    protected final boolean closed;
    /**
     * The normal of the upper cap of the cone.
     */
    protected final Tuple capNormalUp = Cylinder.capNormalUp;
    /**
     * The normal of the lower cap of the cone.
     */
    protected final Tuple capNormalDown = Cylinder.capNormalDown;

    /**
     * Configuration constructor
     * @param transform
     * @param maximum
     * @param minimum
     * @param closed
     */
    public Cone(Matrix4 transform, float maximum, float minimum, boolean closed) {
        super(transform); this.closed = closed; this.maximum = maximum; this.minimum = minimum;
    }

    /**
     * Convienience constructor.
     * @param transform
     * @param maximum
     * @param minimum
     */
    public Cone(Matrix4 transform, float maximum, float minimum) {
        this(transform, maximum, minimum, false);
    }

    /**
     * Default Constructor
     * @param transform
     */
    public Cone(Matrix4 transform) {
        this(transform, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }

    /**
     * Configuration Constructor.
     * @param transform
     * @param material
     * @param maximum
     * @param minimum
     * @param closed
     */
    public Cone(Matrix4 transform, Material material, float maximum, float minimum, boolean closed) {
        super(transform, material); this.closed = closed; this.maximum = maximum; this.minimum = minimum;
    }

    /**
     * Convienience Constructor.
     * @param transform
     * @param material
     * @param maximum
     * @param minimum
     */
    public Cone(Matrix4 transform, Material material, float maximum, float minimum) {
        this(transform, material, maximum, minimum, false);
    }

    /**
     * Default Constructor.
     * @param transform
     * @param material
     */
    public Cone(Matrix4 transform, Material material) {
        this(transform, material, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }

    /**
     * 
     * @param ray
     * @param t
     * @param y
     *   The y coordinate of the plane which is being intersection tested against
     * @return
     */
    private static boolean capRayIntersect(Ray ray, float t, float y) {
        final var radius = Math.abs(y);
        final var x = ray.origin().x + t * ray.direction().x;
        final var z = ray.origin().z + t * ray.direction().z;
        return FloatHelp.compareFloat((x*x) + (z*z), radius) != 1; 
    }

    /**
     * Check if a ray intersects either of the cone caps, if they exist. Adds intersection objects to the result if intersections occur.
     * @param ray
     * @param intersections
     * @return
     *   Null if the ray misses the cylinder cap OR the cylinder has no caps.
     *   Returns intersections if the ray intersects and intersections is not null.
     *   Returns a new list if the ray intersects and intersections is null.
     */
    protected List<Intersection> capIntersections(Ray ray, List<Intersection> intersections) {
        if (!closed || FloatHelp.compareFloat(ray.direction().y, 0) == 0) {
            return null;
        }
        final var c0 = (minimum - ray.origin().y) / ray.direction().y;
        final boolean addC0 = capRayIntersect(ray, c0, minimum);
        final var c1 = (maximum - ray.origin().y) / ray.direction().y;
        final boolean addC1 = capRayIntersect(ray, c1, maximum);
        if (!addC0 && !addC1) {
            return null;
        }
        List<Intersection> result = intersections == null ? new ArrayList<>(2) : intersections;
        if (addC0) {
            result.add(new Intersection(c0, this));
        }
        if (addC1) {
            result.add(new Intersection(c1, this));
        }
        return result;
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        final var dir = ray.direction();
        final var or = ray.origin();
        final var a = (dir.x * dir.x) - (dir.y * dir.y) + (dir.z * dir.z);
        final var b = (2 * or.x * dir.x) - (2 * or.y * dir.y) + (2 * or.z * dir.z);
        final var c = (or.x * or.x) - (or.y * or.y) + (or.z * or.z);
        if (FloatHelp.compareFloat(a, 0) == 0) {
            // ray is parrallel to a cone surface
            if (FloatHelp.compareFloat(b, 0) == 0) {
                // ray missed the cone
                return Optional.empty();
            }
            // calculate ray-cone intersection
            final var distance = -c / (2 * b);
            final var intersection = new Intersection(distance, this);
            // and also check for cap intersections before returning the result
            final var intersections = capIntersections(ray, null);
            if (intersections != null) {
                intersections.add(intersection);
                return Optional.of(intersections);
            } else {
                return Optional.of(List.of(intersection));
            }
        }
        final var discriminant = (b*b) - 4 * a * c;
        if (FloatHelp.compareFloat(discriminant, 0) == -1) {
            return Optional.empty();
        }
        final float sqrtDisc = FloatHelp.compareFloat(discriminant, 0) == 0 ? 0 : (float) Math.sqrt(discriminant);
        var t0 = (-b - sqrtDisc) / (2*a);
        var t1 = (-b + sqrtDisc) / (2*a);
        if (t0 > t1) {
            var temp = t0;
            t0 = t1; 
            t1 = temp;
        }
        final var y0 = ray.origin().y + t0 * ray.direction().y;
        final var y1 = ray.origin().y + t1 * ray.direction().y;
        final var addt0 = this.minimum < y0 && y0 < this.maximum;
        final var addt1 = this.minimum < y1 && y1 < this.maximum;
        if (!addt0 && !addt1) {
            final var intersections = capIntersections(ray, null);
            return intersections == null ? Optional.empty() : Optional.of(intersections);
        }
        final List<Intersection> result = new ArrayList<>(2);
        if (addt0) {
            result.add(new Intersection(t0, this));
        }
        if (addt1) {
            result.add(new Intersection(t1, this));
        }
        capIntersections(ray, result);
        return Optional.of(result);
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        if (closed) {
            final var distance = (point.x * point.x) + (point.z * point.z);
            if (FloatHelp.compareFloat(distance, 1) == -1) {
                if (point.y >= maximum - FloatHelp.epsilon) {
                    return capNormalUp;
                }
                if (point.y <= minimum + FloatHelp.epsilon) {
                    return capNormalDown;
                }
            }
        }
        var y = (float) Math.sqrt((point.x * point.x) + (point.z * point.z));
        y = FloatHelp.compareFloat(point.y, 0) == 1 ? -y : y;
        return makeVector(point.x, y, point.z);
    }
}
