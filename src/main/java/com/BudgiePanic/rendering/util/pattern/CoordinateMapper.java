package com.BudgiePanic.rendering.util.pattern;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Coordinate system remapping functions.
 * 
 * @author BudgiePanic
 */
public sealed abstract class CoordinateMapper permits CoordinateMapper.Sphere {
    private CoordinateMapper() {}

    /**
     * Remaps points onto a sphere surface.
     */
    protected static final class Sphere extends CoordinateMapper {
        @Override
        public final double vSphereMap(final Tuple point) {
            final Tuple vector = makeVector(point.x, point.y, point.z);
            final double radius = vector.magnitude();
            final double phi = Math.acos(point.y / radius); // polar angle
            return 1.0 - phi / Math.PI;
        }
        @Override
        public final double uSphereMap(final Tuple point) {
            final double theta = Math.atan2(point.x, point.z); // azimuth
            final double u = theta / (2 * Math.PI); // remap from [-pi to pi] to [-0.5 to 0.5]
            return 1.0 - (u + 0.5); // remap to be in range [0 to 1] and flip direction
        }
    }

    /**
     * Sphere surface remapping functions.
     */
    public static final CoordinateMapper sphere = new Sphere();

    /**
     * Project a point on a sphere in 3D space onto a 2D plane.
     *
     * @param point
     *   The point in local space.
     * @return
     *   The v coordinate of the point projected onto a flat surface.
     */
    public abstract double vSphereMap(Tuple point);

    /**
     * Project a point on a sphere in 3D space onto a 2D plane.
     *
     * @param point
     *   The point in local space.
     * @return
     *   The u coordinate of the point projected onto a flat surface.
     */
    public abstract double uSphereMap(Tuple point);
}
