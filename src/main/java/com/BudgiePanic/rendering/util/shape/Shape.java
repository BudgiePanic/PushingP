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
    public Matrix4 transform();

    /**
     * The information needed to shade the shape.
     * 
     * @return
     *   The shape's material
     */
    public Material material();

    /**
     * Determine the distance between the ray origin and intersection points with this shape, if any.
     * 
     * @param ray
     *   The ray to test against
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    public Optional<List<Intersection>> intersect(Ray ray);

    /**
     * Determine the normal vector of a point on the shape.
     *
     * @param point
     *   The point on the shape
     * @return
     *   The normal vector at the point
     */
    public Tuple normal(Tuple point);
}
