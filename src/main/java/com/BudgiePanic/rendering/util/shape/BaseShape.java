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
import java.util.Objects;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Base shape converts incoming locations from world space to object space before passing calculations off to shape implementations.
 * 
 * NOTE: in the future, if we want to have scene objects that are composed of components
 *       then the material property can be stripped from the sphere. but currently we are 
 *       going to enforce that all spheres (shapes) have to have a material.
 * 
 * @author BudgiePanic
 */
public abstract class BaseShape implements Shape {

    /**
     * All shapes have the same origin in OBJECT space.
     * Rays will be converted from world space to object space before intersection tests 
     * are performed. Making a singleton here to avoid allocating a new object everytime an intersection test
     * is performed.
     */
    protected static final Tuple origin = Tuple.makePoint();

    /**
     * Information needed to convert locations from world space to the object's local space
     */
    protected final Matrix4 transform;

    /**
     * information needed to light the shape in the scene
     */
    protected final Material material;

    /**
     * The shape tree that this shape belongs to, if any.
     * Mutable field because the group will set itself as the parent when this shape is added to a group,
     * which may happen after the object is constructed.
     */
    protected Optional<Parent> parent;

    /**
     * A base shape
     *
     * @param transform
     *   The shape's transform
     * @param material
     *   The shape's material
     */
    public BaseShape(Matrix4 transform, Material material) {
        if (transform == null) throw new IllegalArgumentException("sphere transform cannot be null");
        if (material == null) throw new IllegalArgumentException("sphere material cannot be null");
        this.transform = transform;
        this.material = material;
        this.parent = Optional.empty();
    }

    /**
     * Create a base shape with a default material
     * 
     * @param transform
     *   The shape's transform
     */
    public BaseShape(Matrix4 transform) {
        this(transform, Material.defaultMaterial());
    }

    
    @Override
    public Optional<List<Intersection>> intersect(Ray ray) {
        if (ray == null) throw new IllegalArgumentException("ray is null");
        // pass the ray through the shape transform then call local intersect
        var transformInverse = this.transform().inverse();
        var rayInObjectSpace = ray.transform(transformInverse);
        return localIntersect(rayInObjectSpace);
    }
    
    @Override
    public Tuple normal(Tuple point) {
        if (point == null) throw new IllegalArgumentException("point is null");
        final var localPoint = toObjectSpace(point);
        final var localNormal = localNormal(localPoint);
        final var bumpedNormal = material.normalBump().apply(localNormal, point);
        return normalToWorldSpace(bumpedNormal);
    }

    @Override
    public Tuple normal(Tuple point, Intersection intersection) {
        final var localPoint = toObjectSpace(point);
        final var localNormal = localNormal(localPoint, intersection);
        final var bumpedNormal = material.normalBump().apply(localNormal, point);
        return normalToWorldSpace(bumpedNormal);
    }
    
    @Override
    public Matrix4 transform() {
        return this.transform;
    }

    @Override
    public Material material() {
        return this.material;
    }
    
    @Override
    public Optional<Parent> parent() {
        return this.parent;
    }

    @Override
    public void setParent(Parent group) {
        this.parent = Optional.ofNullable(group);
    }
    
    @Override
    public boolean contains(Shape shape) { return this.equals(shape); }

    /**
     * Determine the distance between the ray origin and intersection points with this shape, if any.
     * 
     * @param ray
     *   A ray that has been transformed to object space.
     * @return
     *   A list of distances to intersection points along the ray, from the ray origin. May be empty.
     */
    protected abstract Optional<List<Intersection>> localIntersect(Ray ray);

    /**
     * Calcualte the normal of a point on the surface of the shape.
     * 
     * By default, we ignore the extra information. 
     * Sub classes can override this method if they wish to use the extra information to find their normal.
     * 
     * @param point
     *   the point on the surface of the shape to find the normal for
     * @param intersection
     *   extra information about the point to find the normal  
     * @return
     *   the vector normal of the shape at the point on the surface
     */
    protected Tuple localNormal(Tuple point, Intersection intersection) { return localNormal(point); }

    /**
     * Calcualte the normal of a point on the surface of the shape
     *
     * @param point
     *   the point on the surface of the shape to find the normal for
     * @return
     *   the vector normal of the shape at the point on the surface
     */
    protected abstract Tuple localNormal(Tuple point);

    @Override
    public String toString() {
        return String.format("%s[transform=%s, material=%s]", this.getClass().getSimpleName(), this.transform.toString(), this.material.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(transform, material);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseShape other = (BaseShape) obj;
        if (transform == null) {
            if (other.transform != null) {
                return false;
            }
        } else if (!transform.equals(other.transform)) {
            return false;
        }
        if (material == null) {
            if (other.material != null) {
                return false;
            }
        } else if (!material.equals(other.material)) {
            return false;
        }
        return true;
    }
}
