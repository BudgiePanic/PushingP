package com.BudgiePanic.rendering.util.light;

import java.util.Iterator;

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
     * @param time
     *     The time in the world when the intensity if calculated
     * @return
     *     The amount of light reaching the point.
     */
    float intensityAt(Tuple point, World world, float time);

    /**
     * An iterator that returns sample points that cover the entire light.
     * @return
     *   A new iterator over the light's volume.
     */
    Iterator<Tuple> sampler();

    /**
     * Get the number of segments in the light.
     *
     * @return
     *   The number of segments that can be sampled from this light.
     */
    int resolution();
}
