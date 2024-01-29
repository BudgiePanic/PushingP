package com.BudgiePanic.rendering.util.pattern;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * A pattern is used to augment the coloring of a shape by providing logic to change a shapes color such as a stripe pattern, gradient or grid.
 * It allows a single shape to have more than one color.
 * 
 * @author BudgiePanic
 */
public interface Pattern {

    /**
     * Sample the pattern for a color.
     *
     * @param point
     *   The point on the pattern to sample
     * @return
     *   The color at a point along the pattern
     */
    Color colorAt(Tuple point);
    
}
