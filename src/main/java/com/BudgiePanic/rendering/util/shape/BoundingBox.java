package com.BudgiePanic.rendering.util.shape;

import com.BudgiePanic.rendering.util.Tuple;

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

}
    
