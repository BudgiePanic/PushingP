package com.BudgiePanic.rendering.util.shape;

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

}
