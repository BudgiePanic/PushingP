package com.BudgiePanic.rendering.util.light;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Common light operations needed by the phong lighting model.
 * 
 * @author BudgiePanic
 */
public interface Light {
    /**
     * Get the position of the light.
     * @return
     *   The position of the light.
     */
    Tuple position();

    /**
     * Get the color of the light.
     *
     * @return
     *   The color of the light.
     */
    Color color();

    /**
     * How much light from this light is reaching the point in the world.
     *
     * @param point
     *     The point being tested.
     * @param world
     *     The world the point is within.
     * @return
     *     The amount of light reaching the point.
     */
    float intensityAt(Tuple point, World world);
}
