package com.BudgiePanic.rendering.util.pattern;

import java.util.function.Function;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

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
     *   The point in pattern space to sample
     * @return
     *   The sampled color from the pattern
     */
    Color colorAt(Tuple point);

    /**
     * Get the pattern space transform.
     * 
     * @return
     *   The transform to get to pattern space.
     */
    Matrix4 transform();

    /**
     * Sample the pattern for a color. The sample point is converted from world space to local object space, then to the pattern space via this pattern's transform.
     * 
     * @param point
     *   A point in world space to sample the pattern
     * @param toObjectSpace
     *   A function supplied by this pattern's parent (typically a shape's, or another pattern's transform inverse multiply) that converts the point to local space.
     * @return
     *   The color that was sampled from the pattern
     */
    default Color colorAt(Tuple point, Function<Tuple, Tuple> toObjectSpace) {
        final var worldSpaceToObjectSpace = toObjectSpace.apply(point);
        final var objectSpaceToPatternSpace = this.transform().inverse().multiply(worldSpaceToObjectSpace);
        return colorAt(objectSpaceToPatternSpace);
    }
    
}
