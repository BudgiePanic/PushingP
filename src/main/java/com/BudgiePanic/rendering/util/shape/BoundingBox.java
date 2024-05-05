package com.BudgiePanic.rendering.util.shape;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.List;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The axis aligned bounding box is used to speed up ray intersection tests with shape groups.
 * The AABB encompases all the shapes in a group, and a check against the AABB is used before testing the individual shapes.
 * 
 * @author BudgiePanic
 */
public record BoundingBox(Tuple minimum, Tuple maximum) {

    /**
     * Check if the point is inside of this bounding box.
     * @param point
     *   The point to check
     * @return
     *   True if the point is inside of the bounding box
     */
    public boolean contains(Tuple point) {
        return 
            point.x >= minimum.x && point.x <= maximum.x &&
            point.y >= minimum.y && point.y <= maximum.y &&
            point.z >= minimum.z && point.z <= maximum.z;
    }

    /**
     * Check if another bounding box is fully contained within the extents of this bounding box.
     * @param other
     *   The bounding box to test.
     * @return
     *   True if the other bounding box fits inside of this bounding box.
     */
    public boolean contains(BoundingBox other) {
        return contains(other.maximum) && contains(other.minimum);
    }
    /**
     * Calculate new AABB extents needed to contain the given point.
     * 
     * @param point
     *   The point
     * @param minimum
     *   The minimum extent of the AABB
     * @param maximum
     *   The maximum extent of the AABB
     * @return
     *   the new AABB bounds, a() => new minimum value, b() => new maximum value.
     */
    public BoundingBox grow(Tuple point) {
        assert !contains(point); // otherwise we're wasting our time.
        Tuple newMinimum = new Tuple(
            Math.min(point.x, minimum.x),
            Math.min(point.y, minimum.y),
            Math.min(point.z, minimum.z)
        );
        Tuple newMaximum = new Tuple(
            Math.max(point.x, maximum.x),
            Math.max(point.y, maximum.y),
            Math.max(point.z, maximum.z)
        );
        return new BoundingBox(newMinimum, newMaximum);
    }

    /**
     * Lifted the code from Cube::intersect but with support for variable plane offsets.
     * @param origin
     * @param direction
     * @param axisMin
     * @param axisMax
     * @return
     */
    private Pair<Double, Double> checkAxis(double origin, double direction, double axisMin, double axisMax) {
        final var closePlane = (axisMin - origin);
        final var farPlane = (axisMax - origin);
        final var min = closePlane / direction;
        final var max = farPlane / direction;
        if (min > max) {
            return new Pair<>(max, min);
        }
        return new Pair<>(min, max);
    }

    /**
     * Test if a ray intersects with a AABB.
     * @param ray
     *   The ray to test against.
     * @return
     *   true if the ray intersects with the AABB, false if the ray misses the AABB.
     */
    public boolean intersect(Ray ray) {
        final var origin = ray.origin();
        final var direction = ray.direction();
        final var x = checkAxis(origin.x, direction.x, minimum.x, maximum.x);
        final var y = checkAxis(origin.y, direction.y, minimum.y, maximum.y);
        final var z = checkAxis(origin.z, direction.z, minimum.z, maximum.z);
        final var min = Math.max(x.a(), Math.max(y.a(), z.a()));
        final var max = Math.min(x.b(), Math.min(y.b(), z.b()));
        if (min > max) {
            return false;
        }       
        return true;
    }

    /**
     * Merge two bounding boxes.
     *
     * @param other
     *   The other bounding box
     * @return
     *   A new bounding box with extents min(this.min, other.min), max(this.max, other.max)
     */
    protected BoundingBox grow(BoundingBox other) {
        // instead of creating two intermediary bounding boxes, we could determine the min and max inline?
        var a = this.grow(other.minimum);
        a = a.grow(other.maximum);
        return a;
    }

    /**
     * Passes the eight corners of the bounding box through the transform, creating a new bounding box.
     * @param transform
     * @return
     *   A new bounding box
     */
    public BoundingBox transform(Matrix4 transform) {
        Tuple _000 = transform.multiply(new Tuple(maximum.x, minimum.y, minimum.z)); // MAX MIN 
        Tuple _100 = transform.multiply(minimum); // MIN                                MIN MIN
        Tuple _001 = transform.multiply(new Tuple(maximum.x, minimum.y, maximum.z)); // MAX MAX
        Tuple _101 = transform.multiply(new Tuple(minimum.x, minimum.y, maximum.z)); // MIN MAX

        Tuple _010 = transform.multiply(new Tuple(maximum.x, maximum.y, minimum.z)); // MAX MIN
        Tuple _110 = transform.multiply(new Tuple(minimum.x, maximum.y, minimum.z)); // MIN MIN
        Tuple _011 = transform.multiply(maximum); // MAX                                MAX MAX
        Tuple _111 = transform.multiply(new Tuple(minimum.x, maximum.y, maximum.z)); // MIN MAX
        var points = List.of(_000, _001, _010, _011, _100, _101, _110, _111);
        var result = new BoundingBox(_000, _000);
        for (var point : points) {
            if (!result.contains(point)) {
                result = result.grow(point);
            }
        }
        return result;
    }

}
    
