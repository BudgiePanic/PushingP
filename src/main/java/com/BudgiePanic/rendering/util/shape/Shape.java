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
     * Determine the normal vector of a point on the shape, with extra information from an intersection.
     * @param point
     *   The point on the shape.
     * @param intersection
     *   Extra information about the point.
     * 
     *   TODO I think intersection could be replaced with just the UVs (Pair<Float, Float>)
     *   TODO since Intersection::computeShadingInfo always calls this method over Shape::normal(Tuple)
     *   TODO I think we can remove Shape::normal(Tuple) and just have this method instead [refactor]
     * @return
     *   The normal vector at the point.
     */
    Tuple normal(Tuple point, Intersection intersection);

    /**
     * Get the parent of this shape.
     * @return
     *   Empty if this shape is not part of a group. The group that this shape belongs to.
     */
    Optional<Group> parent();

    /**
     * Set the parent of this shape.
     *
     * @param parent
     *   The parent of the shape.
     */
    void setParent(Group parent);

    /**
     * Convert a world space point to object space.
     * @param point
     *   The point to convert
     * @return
     *   A new transformed point relative to the shape.
     */
    default Tuple toObjectSpace(Tuple point) {
        final var localPoint = parent().map(parent -> parent.toObjectSpace(point)).orElse(point);
        return transform().inverse().multiply(localPoint);
    }

    /**
     * Convert a normal vector in local space to a vector in global space.
     * @param normal
     *   The local normal.
     * @return
     *   A new tuple containing the normal in world space.
     */
    default Tuple normalToWorldSpace(Tuple normal) {
        final var temp = transform().inverse().transpose().multiply(normal);
        final var toParent = Tuple.makeVector(temp.x, temp.y, temp.z).normalize();
        return parent().map(parent -> parent.normalToWorldSpace(toParent)).orElse(toParent);
    }

    /**
     * Get the local bounding box for the shape.
     * @return
     *   A bounding for the shape in local space.
     */
    BoundingBox bounds();
}
