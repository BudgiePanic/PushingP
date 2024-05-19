package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.FloatHelp;

/**
 * Two dimensional pattern variant for use in uv texture mapping.
 *
 * @author BudgiePanic
 */
public interface Pattern2D {

    /**
     * Create a checker pattern.
     * 
     * @param width
     *   The number of checker squares in the u axis.
     * @param height
     *   The number of checker squares in the v axis.
     * @param a
     *   The first pattern to sample.
     * @param b
     *   The second pattern to sample.
     * @return
     *   A pattern the alternates between a and b.
     */
    public static Pattern2D checker(final double width, final double height, final Pattern2D a, final Pattern2D b) {
        return (u, v) -> {
            double x = Math.floor(u * width);
            double y = Math.floor(v * height);
            return FloatHelp.compareFloat((x + y) % 2, 0.0) == 0 ? a.sample(u, v) : b.sample(u, v); 
        };
    }
    
    /**
     * Create a solid color pattern.
     * 
     * @param color
     *   The color of the pattern.
     * @return
     *   A pattern that always returns the color when sampled.
     */
    public static Pattern2D solidColor(final Color color) { return (u,v) -> { return color; }; }

    /**
     * Sample the 2D pattern.
     *
     * @param u
     *   The u coordinate in pattern space.
     * @param v
     *   The v coordinate in pattern space.
     * @return
     *   The color of the pattern at (u,v).
     */
    public Color sample(double u, double v);
    
}
