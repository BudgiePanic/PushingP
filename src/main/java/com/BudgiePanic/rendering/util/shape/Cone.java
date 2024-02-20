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

    private static boolean capRayIntersect(Ray ray, float t) {
        final var x = ray.origin().x + t * ray.direction().x;
        final var z = ray.origin().z + t * ray.direction().z;
        return FloatHelp.compareFloat((x*x) + (z*z), 1) != 1; // (x^2 + z^2 <= 1)
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
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
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
