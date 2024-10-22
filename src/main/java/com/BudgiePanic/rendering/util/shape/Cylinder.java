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

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Cylinder shape.
 * 
 * @author BudgiePanic
 */
public class Cylinder extends BaseShape {

    /**
     * The lower y bounds distance that causes the cylinder to be truncated.
     */
    protected final double minimum;
    /**
     * The upper y bounds distance that causes the cylinder to be truncated.
     */
    protected final double maximum;

    /**
     * Whether the cylinder has end caps or not.
     */
    protected final boolean closed;

    /**
     * The normal of the upper cap of the cylinder
     */
    protected static final Tuple capNormalUp = makeVector(0, 1, 0);
    /**
     * The normal of the lower cap of the cylinder
     */
    protected static final Tuple capNormalDown = makeVector(0, -1, 0);

    /**
     * The Cylinder.
     *
     * @param transform
     * @param material
     * @param maximum
     * @param minimum
     * @param closed
     */
    public Cylinder(Matrix4 transform, Material material, double maximum, double minimum, boolean closed) {
        super(transform, material); this.maximum = maximum; this.minimum = minimum; this.closed = closed;
    }

    /**
     * 
     * @param transform
     * @param material
     * @param maximum
     * @param minimum
     */
    public Cylinder(Matrix4 transform, Material material, double maximum, double minimum) { this(transform, material, maximum, minimum, false); }

    
    /**
     * 
     * @param transform
     * @param material
     */
    public Cylinder(Matrix4 transform, Material material) { this(transform, material, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY); }
    
    /**
     * 
     * @param transform
     * @param maximum
     * @param minimum
     * @param closed
     */
    public Cylinder(Matrix4 transform, double maximum, double minimum, boolean closed) {
        super(transform); this.maximum = maximum; this.minimum = minimum; this.closed = closed;
    } 

    /**
     * 
     * @param transform
     * @param maximum
     * @param minimum
     */
    public Cylinder(Matrix4 transform, double maximum, double minimum) { this(transform, maximum, minimum, false); }

    /**
     * 
     * @param transform
     */
    public Cylinder(Matrix4 transform) { this(transform, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY); }

    /**
     * Check if a ray intersects a plane that caps off the cylinder.
     * @param ray
     * @param t
     * @return
     */
    private static boolean capRayIntersect(Ray ray, double t) {
        final var x = ray.origin().x + t * ray.direction().x;
        final var z = ray.origin().z + t * ray.direction().z;
        return FloatHelp.compareFloat((x*x) + (z*z), 1) != 1; // (x^2 + z^2 <= 1)
    }

    /**
     * Check if a ray intersects either of the cylinder caps, if they exist. Adds intersection objects to the result if intersections occur.
     * @param ray
     * @param intersections
     * @return
     *   Null if the ray misses the cylinder cap OR the cylinder has no caps.
     *   Returns intersections if the ray intersects and intersections is not null.
     *   Returns a new list if the ray intersects and intersections is null.
     */
    private List<Intersection> capIntersections(Ray ray, List<Intersection> intersections) {
        if (!closed || FloatHelp.compareFloat(ray.direction().y, 0) == 0) {
            return null;
        }
        final var c0 = (minimum - ray.origin().y) / ray.direction().y;
        final boolean addC0 = capRayIntersect(ray, c0);
        final var c1 = (maximum - ray.origin().y) / ray.direction().y;
        final boolean addC1 = capRayIntersect(ray, c1);
        if (!addC0 && !addC1) {
            return null;
        }
        List<Intersection> result = intersections == null ? new ArrayList<>(2) : intersections;
        if (addC0) {
            result.add(new Intersection(c0, this));
        }
        if (addC1) {
            result.add(new Intersection(c1, this));
        }
        return result;
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) { 
        final var a = (ray.direction().x * ray.direction().x) + (ray.direction().z * ray.direction().z);
        if (FloatHelp.compareFloat(a, 0) == 0) {
            final var intersections = capIntersections(ray, null);
            return intersections == null ? Optional.empty() : Optional.of(intersections);
        }
        final var b = (2 * ray.origin().x * ray.direction().x) + (2 * ray.origin().z * ray.direction().z);
        final var c = (ray.origin().x * ray.origin().x) + (ray.origin().z * ray.origin().z) - 1;
        final var discriminant = (b*b) - 4 * a * c;
        if (discriminant < 0) {
            return Optional.empty();
        }
        final double sqrtDisc = Math.sqrt(discriminant);
        var t0 = (-b - sqrtDisc) / (2*a);
        var t1 = (-b + sqrtDisc) / (2*a);
        if (t0 > t1) {
            var temp = t0;
            t0 = t1; 
            t1 = temp;
        }
        final var y0 = ray.origin().y + t0 * ray.direction().y;
        final var y1 = ray.origin().y + t1 * ray.direction().y;
        final var addt0 = this.minimum < y0 && y0 < this.maximum;
        final var addt1 = this.minimum < y1 && y1 < this.maximum;        
        if (!addt0 && !addt1) {
            final var intersections = capIntersections(ray, null);
            return intersections == null ? Optional.empty() : Optional.of(intersections);
        }
        final List<Intersection> result = new ArrayList<>(2);
        if (addt0) {
            result.add(new Intersection(t0, this));
        }
        if (addt1) {
            result.add(new Intersection(t1, this));
        }
        capIntersections(ray, result);
        return Optional.of(result);
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        if (closed) {
            final var distance = (point.x * point.x) + (point.z * point.z);
            if (FloatHelp.compareFloat(distance, 1) == -1) {
                if (point.y >= maximum - FloatHelp.epsilon) {
                    return capNormalUp;
                }
                if (point.y <= minimum + FloatHelp.epsilon) {
                    return capNormalDown;
                }
            }
        }
        return Tuple.makeVector(point.x, 0, point.z);
    }

    @Override
    public BoundingBox bounds() {
        return new BoundingBox(
            Tuple.makePoint(-1, this.minimum, -1),
            Tuple.makePoint(1, this.maximum, 1));
    }
    
    @Override
    public boolean isSolid() { return this.closed; }

}
