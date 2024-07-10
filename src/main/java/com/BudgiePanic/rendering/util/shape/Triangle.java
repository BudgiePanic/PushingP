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

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Triangle shape primitive.
 * @author BudgiePanic
 */
public class Triangle extends BaseShape {

    /**
     * The first vertex
     */
    protected final Tuple p1;
    /**
     * The second vertex
     */
    protected final Tuple p2;
    /**
     * The third vertex
     */
    protected final Tuple p3;
    /**
     * p2 - p1
     */
    protected final Tuple edge1;
    /**
     * p3 - p1
     */
    protected final Tuple edge2;
    /**
     * e2 cross e1
     */
    protected final Tuple normal;
    /**
     * A static matrix to avoid excess matrix heap allocation
     */
    protected static final Matrix4 defaultTransform = Matrix4.identity();
    /**
     * A static material to avoid excess matrix heap allocation
     */
    protected static final Material defaultMaterial = Material.defaultMaterial();
    /**
     * Triangle AABB to satisfy the interface shape contract
     */
    protected final BoundingBox AABB;

    
    public Triangle(Tuple p1, Tuple p2, Tuple p3, Matrix4 transform, Material material) {
        super(transform, material);
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.edge1 = p2.subtract(p1);
        this.edge2 = p3.subtract(p1);
        this.normal = edge2.cross(edge1).normalize();
        this.AABB = new BoundingBox( // should this be lazilly initialized? 
            Tuple.makePoint(
                Math.min(Math.min(p1.x,p2.x),p3.x),
                Math.min(Math.min(p1.y,p2.y),p3.y),
                Math.min(Math.min(p1.z,p2.z),p3.z)), 
            Tuple.makePoint(
                Math.max(Math.max(p1.x,p2.x),p3.x),
                Math.max(Math.max(p1.y,p2.y),p3.y),
                Math.max(Math.max(p1.z,p2.z),p3.z)));
    }
    /**
     * Convienience constructor.
     *
     * @param p1
     * @param p2
     * @param p3
     * @param transform
     */
    public Triangle(Tuple p1, Tuple p2, Tuple p3, Matrix4 transform) {
        this(p1, p2, p3, transform, defaultMaterial);
    }

    /**
     * Convienience constructor.
     *
     * @param p1
     * @param p2
     * @param p3
     * @param material
     */
    public Triangle(Tuple p1, Tuple p2, Tuple p3, Material material) {
        this(p1, p2, p3, defaultTransform, material);
    }

    /**
     * Convience constructor.
     * @param p1
     * @param p2
     * @param p3
     */
    public Triangle(Tuple p1, Tuple p2, Tuple p3) {
        this(p1, p2, p3, defaultTransform);
    }

    @Override
    public synchronized BoundingBox bounds() { return AABB; }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // uses the Moller-Trumbore ray-triangle intersection algorithm
        final var dirCrossE2 = ray.direction().cross(edge2);
        final var determinant = edge1.dot(dirCrossE2);
        if (FloatHelp.compareFloat(0, Math.abs(determinant)) == 0) {
            // miss by parrallel ray
            return Optional.empty();
        }
        final var f = 1.0 / determinant;
        final var p1ToOrigin = ray.origin().subtract(p1);
        final var u = f * p1ToOrigin.dot(dirCrossE2); 
        if (FloatHelp.compareFloat(u, 0) == -1 || FloatHelp.compareFloat(u, 1) == 1) {
            // missed via the p1-p3 edge
            return Optional.empty();
        }
        final var originCrossEdge1 = p1ToOrigin.cross(edge1);
        final var v = f * ray.direction().dot(originCrossEdge1);
        if (FloatHelp.compareFloat(v, 0) == -1 || FloatHelp.compareFloat((u+v), 1) == 1) {
            return Optional.empty();
        }
        final var t = f * edge2.dot(originCrossEdge1);
        return Optional.of(List.of(new Intersection(t, this, Optional.of(new Pair<>(u, v)))));
    }

    @Override
    protected Tuple localNormal(Tuple point) { return normal; }
    
    public Tuple p1() { return p1; }

    public Tuple p2() { return p2; }

    public Tuple p3() { return p3; }
    
    @Override
    public boolean isSolid() { return false; }

}
