/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
    Optional<Parent> parent();

    /**
     * Set the parent of this shape.
     *
     * @param parent
     *   The parent of the shape.
     */
    void setParent(Parent parent);

    /**
     * Perform internal optimizations based on the exposure duration of the camera that generates rays to intersect with the shape.
     * TODO see comment in world regarding architecture improvement...
     * @param endTime
     *   The end time of the exposure.
     */
    default void bakeExposureDuration(double endTime) {}

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
     * Convert a point in the object's local space to a point in world space.
     * @param localPoint
     *   The point in object local space.
     * @return
     *   A new point in world space.
     */
    default Tuple pointToWorldSpace(Tuple localPoint) {
        final var toParent = transform().multiply(localPoint);
        return parent().map(parent -> parent.pointToWorldSpace(toParent)).orElse(toParent);
    }

    /**
     * Get the local bounding box for the shape.
     * @return
     *   A bounding for the shape in local space.
     */
    BoundingBox bounds();

     /**
     * Check if this shape contains another shape.
     * @param shape
     *   The shape to check for containment.
     * @return
     *   True if the shape is contained within this shape.
     */
    boolean contains(Shape shape);

    /**
     * Does this shape enclose a volume?
     * 
     * @return
     *   Returns true if the shape encloses a volume with no holes.
     */
    boolean isSolid();

    /**
     * Divide this shape into subgroups.
     * Returns the shape itself when called on concrete shape implementations like Cube and Sphere.
     * @param threshold
     *   The minimum number of subshapes within this shape to trigger a subdivision.
     * @return
     *   The subdivision of this shape.
     */
    default Shape divide(int threshold) { return this; }
}
