package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Common functionality that all shapes have.
 * 
 * @author BudgiePanic
 */
public interface Shape {
    
    /**
     * Converts a tuple from world space to object space.
     *
     * @return
     *   The object's transform
     */
    Matrix4 transform();

    /**
     * The information needed to shade the shape.
     * 
     * @return
     *   The shape's material
     */
    Material material();

    /**
     * Determine the distance between the ray origin and intersection points with this shape, if any.
     * There shouldn't be any need to override this method.
     * 
     * @param ray
     *   The ray to test against
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    Optional<List<Intersection>> intersect(Ray ray);
    
    /**
     * Determine the normal vector of a point on the shape.
     *
     * @param point
     *   The point on the shape
     * @return
     *   The normal vector at the point
     */
    Tuple normal(Tuple point);

    /**
     * Get the parent of this shape.
     * @return
     *   Empty if this shape is not part of a group. The group that this shape belongs to.
     */
    default Optional<Group> parent() { return Optional.empty(); }

    /**
     * Set the parent of this shape.
     *
     * @param parent
     *   The parent of the shape.
     */
    default void setParent(Group parent) {}
}
