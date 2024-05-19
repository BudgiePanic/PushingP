package com.BudgiePanic.rendering.util.pattern;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Coordinate system remapping functions.
 * 
 * @author BudgiePanic
 */
public final class CoordinateMapper {
    private CoordinateMapper() {}

    /**
     * Get the u coordinate of the sphere mapping for a point.
     * @param x
     *   The x coordinate of the point.
     * @param y
     *   The y coordinate of the point.
     * @param z
     *   The z coordinate of the point.
     * @return
     *   The u coordinate of the sphere mapping for the sample point.
     */
    public static final double uSphereMap(double x, double y, double z) {
        final double theta = Math.atan2(x, z); // azimuth
        final double u = theta / (2 * Math.PI); // remap from [-pi to pi] to [-0.5 to 0.5]
        return 1.0 - (u + 0.5); // remap to be in range [0 to 1] and flip direction
    }

    /**
     * Get the v coordinate of the sphere mapping for a point.
     * 
     * @param x
     *   The x coordinate of the point.
     * @param y
     *   The y coordinate of the point.
     * @param z
     *   The z coordinate of the point.
     * @return
     *   The v coordinate of the sphere mapping for the sample point.
     */
    public static final double vSphereMap(double x, double y, double z) {
        final Tuple vector = makeVector(x, y, z);
        final double radius = vector.magnitude();
        final double phi = Math.acos(y / radius); // polar angle
        return 1.0 - phi / Math.PI;
    }

    /**
     * Project a point on a sphere in 3D space onto a 2D plane.
     *
     * @param point
     *   The point in local space.
     * @return
     *   The v coordinate of the point projected onto a flat surface.
     */
    public static final double vSphereMap(Tuple point) { return vSphereMap(point.x, point.y, point.z); }

    /**
     * Project a point on a sphere in 3D space onto a 2D plane.
     *
     * @param point
     *   The point in local space.
     * @return
     *   The u coordinate of the point projected onto a flat surface.
     */
    public static final double uSphereMap(Tuple point) { return uSphereMap(point.x, point.y, point.z); }
}
