package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * A parent is a composite shape that is comprised of more shapes.
 * 
 * @author BudgiePanic
 */
public interface Parent extends Shape {

    /**
     * Shape::contains(Shape) behave's differently in composite shapes, it defers the outcome to the children.
     */
    default boolean contains(Shape shape) { return childrenContains(shape); }
    /**
     * Check if any of the composite shape's children contain the shape.
     */
    boolean childrenContains(Shape shape);

    /**
     * Determine the distance between the ray origin and intersection points with this shape, if any.
     * 
     * @param ray
     *   The ray to test against
     * @param inclusionCondition
     *   A condition that is used to discard children shapes from the intersection test for one reason or another.
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    Optional<List<Intersection>> intersect(Ray ray, Predicate<Shape> inclusionCondition);

}
