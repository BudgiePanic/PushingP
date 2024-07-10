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

import java.util.Collection;
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

    /**
     * Get the children shapes contained within this Parent shape.
     * @return
     *   The subshapes of this parent shape.
     */
    Collection<Shape> children();

}
