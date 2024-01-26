package com.BudgiePanic.rendering.util.intersect;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Sphere;

/**
 * Lighting data container that accompanies intersection information to be used in shading calculations.
 * 
 * @author BudgiePanic
 */
public record ShadingInfo(float a, Sphere shape, Tuple point, Tuple eyeVector, Tuple normalVector, boolean intersectInside) {

    /**
     * Calculate a point slightly above the surface that is being shaded, to avoid floating point precision errors.
     *
     * @return
     *   A point above the surface slightly along the normal direction.
     */
    public Tuple overPoint() {
        return point.add(normalVector.multiply(3f*FloatHelp.epsilon));
    }
}
