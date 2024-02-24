package com.BudgiePanic.rendering.util.shape;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Ray;

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

    public boolean intersect(Ray ray) {
        throw new UnsupportedOperationException("method not implemented yet");
    }

}
    
