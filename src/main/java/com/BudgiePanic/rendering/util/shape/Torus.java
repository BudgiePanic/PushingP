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

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.QuarticHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * The Torus is a donut shape. It lies flat on the xy plane.
 * 
 * @see "intersecting a ray with a torus"
 *     http://cosinekitty.com/raytrace/chapter13_torus.html
 * 
 * @author BudgiePanic
 */
public class Torus extends BaseShape {

    /**
     * The distance from the toris origin to the torus defining circle.
     */
    protected final double radius;

    /**
     * The distance from the torus defining circle to the torus surface.
     */
    protected final double thickness;

    /**
     * Cached bounding box.
     */
    protected final BoundingBox aabb;

    /**
     * Canonical constructor. Create a new Torus on the xy plane.
     * @param transform
     *   World space to object space transform.
     * @param material
     *   Material the torus is made from.
     * @param radius
     *   The radius of the circle that passes through the center of the torus.
     * @param thickness
     *   The distance from the torus inner circle to the torus surface.
     */
    public Torus(Matrix4 transform, Material material, double radius, double thickness) { 
        super(transform, material); this.radius = radius; this.thickness = thickness; 
        if (thickness >= radius) {
            System.out.println("WARN: torus has thickness is larger than its radius, it won't have a hole");
        }
        final var toEdge = radius + thickness;
        // The torus lies on the xy plane
        this.aabb = new BoundingBox(
            makePoint(-toEdge, -toEdge, -thickness),
            makePoint(toEdge, toEdge, thickness)
        );
    }

    @Override
    public BoundingBox bounds() { return aabb; }

    @Override
    public boolean isSolid() { return true; }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // see: http://cosinekitty.com/raytrace/chapter13_torus.html
        final var D = ray.origin();
        final var E = ray.direction();
        final var A = radius;
        final var B = thickness;

        final double g = 4 * (A * A) * ((E.x * E.x) + (E.y * E.y));
        final double h = 8 * (A * A) * (D.x * E.x + D.y * E.y);
        final double i = 4 * (A * A) * ((D.x * D.x) + (D.y * D.y));
        final double j = (E.x * E.x) + (E.y * E.y) + (E.z * E.z);
        final double k = 2 * ((D.x * E.x)+(D.y * E.y)+(D.z * E.z));
        final double l = ((D.x * D.x)+(D.y * D.y)+(D.z * D.z)) + ((A * A) - (B * B));
        // ax^4 + bx^3 + cx^2 + dx + e = 0
        final double a = j*j;
        final double b = 2*j*k;
        final double c = ((2*j*l)+(k*k)+(-g));
        final double d = ((2*k*l)-(h));
        final double e = (l*l) - i;

        final var roots = QuarticHelp.solveQuartic(a,b,c,d,e);

        if (roots.isEmpty()) {
            return Optional.empty();
        }
        var answer = new ArrayList<Intersection>(roots.size());
        for (var root : roots) {
            answer.add(new Intersection(root, this));
        }
        return Optional.of(answer);
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        // see: http://cosinekitty.com/raytrace/chapter13_torus.html
        double denominator = Math.sqrt((point.x * point.x) + (point.y * point.y));
        if (FloatHelp.compareFloat(0, denominator) == 0) {
            // the point is not on the torus surface
            System.out.println("WARN: point " + point + " is not on the torus surface, adding small offset to avoid divide by zero");
            denominator = FloatHelp.epsilon;
        }
        final var alpha = radius / denominator;
        final var normal = makeVector((1 - alpha) * point.x, (1 - alpha) * point.y, point.z);
        return normal.normalize();
    }
    
}
