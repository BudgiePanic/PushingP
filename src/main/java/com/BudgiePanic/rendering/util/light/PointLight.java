package com.BudgiePanic.rendering.util.light;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Collection of data representing a point light, a sizeless light in space.
 * 
 * The color also represents the intensity of the point light.
 * 
 * @author BudgiePanic
 */
public record PointLight(Tuple position, Color color) implements Light {

    @Override
    public float intensityAt(Tuple point, World world) {
        final boolean inShadow = world.isOccluded(point, position, World.shadowCasters);
        return inShadow ? 0f : 1f;
    }
    
}
