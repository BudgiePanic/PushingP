package com.BudgiePanic.rendering.util.shape;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * The axis aligned bounding box is used to speed up ray intersection tests with shape groups.
 * The AABB encompases all the shapes in a group, and a check against the AABB is used before testing the individual shapes.
 * 
 * @author BudgiePanic
 */
public record BoundingBox(Tuple minimum, Tuple maximum) {

}
    
