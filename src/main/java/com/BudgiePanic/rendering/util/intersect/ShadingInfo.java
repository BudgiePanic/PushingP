package com.BudgiePanic.rendering.util.intersect;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * Lighting data container that accompanies intersection information to be used in shading calculations.
 * 
 * @author BudgiePanic
 */
public record ShadingInfo(float a, Shape shape, Tuple point, Tuple eyeVector, Tuple normalVector, boolean intersectInside, Tuple reflectVector) {

    // a => the distance along the ray that intersected to reach point
    // shape => the object that the ray hit
    // point => the point in 3D space where the ray hit the shape
    // eyeVector => a vector looking in the opposite direction as the ray
    // normalVector => the surface normal on the shape at the point where the ray hit the shape
    // reflectVector => the direction the light ray bounced to after hitting this shape.

    /**
     * Calculate a point slightly above the surface that is being shaded, to avoid floating point precision errors.
     *
     * @return
     *   A point above the surface slightly along the normal direction.
     */
    public Tuple overPoint() {
        return point.add(normalVector.multiply(FloatHelp.bigEpsilon));
    }
}
