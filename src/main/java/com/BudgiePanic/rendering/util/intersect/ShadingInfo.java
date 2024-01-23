package com.BudgiePanic.rendering.util.intersect;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.shape.Sphere;

/**
 * Lighting data container that accompanies intersection information to be used in shading calculations.
 * 
 * @author BudgiePanic
 */
public record ShadingInfo(float a, Sphere shape, Tuple point, Tuple eyeVector, Tuple normalVector) {}
