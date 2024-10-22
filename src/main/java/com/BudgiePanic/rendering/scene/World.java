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
package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.intersect.ShadingInfo;
import com.BudgiePanic.rendering.util.light.Light;
import com.BudgiePanic.rendering.util.light.Phong;
import com.BudgiePanic.rendering.util.shape.Parent;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * A world is a collection of objects and light sources that rays can intersect with.
 * 
 * NOTE: world construct could be improved in the future with a world builder fluent API to easily add new lights and shapes to a world.
 * 
 * @author BudgiePanic
 */
public class World {
    
    /**
     * Compute a maximum of 4 light reflections for every ray cast into the world.
     */
    public static final int defaultRecursionDepth = 4;

    /**
     * Predicate to include all shapes in the word in intersection tests
     */
    public static final Predicate<Shape> allShapes = (s) -> true;

    /**
     * Predicate to include all shapes in the world that cast shadows.
     */
    public static final Predicate<Shape> shadowCasters = (s) -> s.material().shadow() || (s instanceof Parent);

    /**
     * The shapes in the scene.
     */
    protected List<Shape> shapes;
    /**
     * The lights in the scene.
     * 
     * NOTE: in the future, PointLight may need to be abtracted behind a Light interface.
     */
    protected List<Light> lights;

    /**
     * Construct a new empty world.
     */
    public World() {
        this.shapes = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    /**
     * Get the shapes in this world that rays can interect with.
     *
     * @return
     *   A list of shapes in the world.
     */
    public List<Shape> getShapes() {
        return this.shapes;
    }

    /**
     * Get the lights in the world.
     * 
     * @return
     *   A list of point lights in the world.
     */
    public List<Light> getLights() {
        return this.lights;
    }

    /**
     * Add a new shape to the world.
     * 
     * @param shape
     *   The shape to add. Cannot be null.
     * @param castsShadows
     *   Whether or not this shape casts shadows in the world  
     */
    public void addShape(Shape shape) {
        // precondition check, don't add null shapes
        if (shape == null) throw new IllegalArgumentException("shape cannot be null.");
        this.shapes.add(shape);
    }

    /**
     * Add a new light to the world.
     *
     * @param light
     *   The light to add. Cannot be null.
     */
    public void addLight(Light light) {
        // precondition checks, don't add null lights
        if (light == null) throw new IllegalArgumentException("light cannot be null.");
        this.lights.add(light);
    }

    /**
     * Perform a ray intersection test against the shapes in the world.
     * Allows for easy intersection tests against multiple shapes.
     * 
     * @param ray
     *   The ray to test with.
     * @return
     *   EMPTY if no intersections occured. List of intersections if any.
     */
    public Optional<List<Intersection>> intersect(Ray ray) {
        return intersect(ray, allShapes);
    }

    /**
     * Perform a ray intersection test against the shapes in the world.
     * Allows for easy intersection tests against multiple shapes.
     * @param ray
     *   The ray to test with.
     * @param inclusionCondition
     *   A predicate function that identifies whether a shape should be included in the intersection tests.
     * @return
     *   EMPTY if no intersections occured. List of intersections if any.
     */
    protected Optional<List<Intersection>> intersect(Ray ray, Predicate<Shape> inclusionCondition) {
        var intersections = this.shapes.stream().
            filter(inclusionCondition).
            map(Intersection.buildIntersector(ray, inclusionCondition)).
            filter(Optional::isPresent).
            map(Optional::get).
            flatMap(List::stream).
            collect(Collectors.toList());
        if (intersections.isEmpty()) return Optional.empty();
        intersections.sort(Comparator.comparing(Intersection::a));
        return Optional.of(intersections);
    }

    /**
     * Determine the color of a point in the world given some shading information.
     *
     * @param info
     *   Shading information derived from a ray-shape intersection test
     * @param depth
     *   recursion limit on light reflection calculations
     * @return
     *   The color of the point in the world given the shading information.
     */
    public Color shadeHit(ShadingInfo info, int depth) {
        if (info == null) throw new IllegalArgumentException("shading info should not be null");
        final var material = info.shape().material();
        final var hasReflectance = material.reflectivity() > 0 && material.transparency() > 0; // this expression could be extracted to Shading info 
        final Optional<Double> reflectance = hasReflectance ?  Optional.of(info.schlick()) : Optional.empty(); // this expression could be extracted to Shading info 
        return this.lights.stream().
            map((light) -> Phong.compute(info, light, light.intensityAt(info.overPoint(), this, info.time()))).
            reduce(Color::add).
            map(color -> color.add(this.shadeReflection(info, depth).multiply(reflectance.orElse(1.0)))).
            map(color -> color.add(this.shadeRefraction(info, depth).multiply(1f - reflectance.orElse(0.0)))).
            orElse(Colors.black);
    }

    /**
     * Determine the color of a pointin the world using the default reflection recursion limit.
     *
     * @param info
     *   Shading information derived from a ray-shape intersection test
     * @return
     *   The color of the point in the world given the shading information.
     */
    public Color shadeHit(ShadingInfo info) {
        return shadeHit(info, defaultRecursionDepth);
    }

    /**
     * Determine the color produced by a ray intersecting with the world.
     *
     * @param ray
     *   The ray
     * @param depth
     *   recursion limit on light reflection calculations
     * @return
     *   The color resulting from shading the ray intersection point within the world.
     */
    public Color computeColor(Ray ray, int depth) {
        var intersections = intersect(ray); 
        if (intersections.isPresent()) {
            var hit = Intersection.Hit(intersections.get());
            if (hit.isPresent()) {
                var info = hit.get().computeShadingInfo(ray, intersections);
                return shadeHit(info, depth);
            }
        }
        return Colors.black;
    }

    /**
     * Determine the color produced by a ray intersecting with the world, using the deafult recursion limit for reflections.
     *
     * @param ray
     *   The ray
     * @return
     *   The color resulting from shading the ray intersection point within the world.
     */
    public Color computeColor(Ray ray) {
        return computeColor(ray, defaultRecursionDepth);
    }

    /**
     * Find the color of the reflection vector in a shading info.
     * @param info
     *   Shading information
     * @param depth
     *   the number of reflection bounces allowed
     * @return
     *   The color that lies along the shading info's reflection vector
     */
    public Color shadeReflection(ShadingInfo info, int depth) {
        final double reflectivity = info.shape().material().reflectivity();
        if (depth < 1 || reflectivity <= 0.0) {
            return Colors.black;
        }
        final var ray = new Ray(info.overPoint(), info.reflectVector(), info.time());
        final var color = this.computeColor(ray, --depth);
        return color.multiply(reflectivity);
    }

    /**
     * Find the color of the reflection vector in a shading info with a reflection depth of 1.
     * @param info
     *   Shading information
     * @return
     *   The color that lies along the shading info's reflection vector
     */
    public Color shadeReflection(ShadingInfo info) {
        return shadeReflection(info, 1);
    }

    /**
     * Test if any shapes block the view from one point to another.
     * @param from
     *   The first point.
     * @param to
     *   The second point.
     * @param condition
     *   A predicate to decide if a shape should be used in the occulusion test.
     * @param time
     *   The time when the occlusion check occurs
     * @return
     *   True if any shapes block the line traced by 'from' to 'to'.
     */
    public boolean isOccluded(Tuple from, Tuple to, Predicate<Shape> condition, final double time) {
        // book chapter 8: section: testing for shadows
        final var trace = to.subtract(from);
        final var distance = trace.magnitude();
        final var ray = new Ray(from, trace.normalize(), time);
        final var intersections = this.intersect(ray, condition);
        if (intersections.isEmpty()) return false;
        var hit = Intersection.Hit(intersections.get());
        if (hit.isPresent()) {
            // distance to hit is smaller than distance to target point, so it must be blocking the point's view to the target
            if (FloatHelp.compareFloat(hit.get().a(), distance) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculate the average light intensity at a point in the world. 
     *
     * @param point
     *   The location to check for illumination.
     * @param time
     *   The time when the intensity is calculated
     * @return
     *   The average light intensity at the point, collected from all lights in the world.
     */
    public double averageIntensityAt(Tuple point, double time) {
        double accumulator = 0.0;
        for (Light light : lights) {
            accumulator += light.intensityAt(point, this, time);
        } 
        accumulator /= lights.size();
        return accumulator;
    }

    /**
     * Determine the color at a point in the world as a result of refraction.
     * @param info
     *   information about the point where the refraction occured
     * @param depth
     *   ray trace recursion limit.
     * @return
     *   the color produced by refracting a ray through the world
     */
    public Color shadeRefraction(ShadingInfo info, int depth) {
        final double transparency = info.shape().material().transparency();
        if (depth < 1 || transparency <= 0.0) {
            return Colors.black;
        }
        // use snell's law to determine if total internal reflection has occured | t ~ theta
        // (sin(theta_i) / sin(theta_t)) = (n2 / n1)
        final var ratio = info.n1() / info.n2();
        final var cosI = info.eyeVector().dot(info.normalVector());
        final var sin2t = (ratio * ratio) * (1.0 - (cosI * cosI)); // via trig identity: sin(theta)^2 = 1 - cos(theta)^2
        if (sin2t > 1.0) {
            return Colors.black;
        }
        final var cosT = Math.sqrt(1.0 - sin2t); // via trig identity: cos(theta)^2 = 1 - sin(theta)^2
        final var refractionDirection = info.normalVector().multiply(ratio * cosI - cosT).subtract(info.eyeVector().multiply(ratio));
        final var refractionRay = new Ray(info.underPoint(), refractionDirection, info.time());
        // find the refraction color by casting the refraction ray back into the world
        final var refractedColor = this.computeColor(refractionRay, --depth);
        return refractedColor.multiply(transparency); // apply effect of transparency to the output
    }

    /**
     * Determine the color at a point in the world as a result of refraction.
     * @param info
     *   information about the point where the refraction occured
     * @return
     *   the color produced by refracting a ray through the world
     */
    public Color shadeRefraction(ShadingInfo info) {
        return shadeRefraction(info, defaultRecursionDepth);
    }

    /**
     * Update's all movable shapes in the world with the supplied motion end time.
     * Allows AABB optimization for ray-shape intersection tests on shapes that move.
     * 
     * @param time
     *   The time of the final ray that will be intersected with the world.
     */
    public void bakeEndTime(final double time) {
        // TODO this could be accomplished using the observer design pattern
        // TODO where the world emits events, shapes listen, and the shapes can decide 
        // TODO if they want to do anything with the time update information
        System.out.println("INFO: baking duration time of " + time + " to shapes in world " + this.toString());
        for (final var shape : this.getShapes()) {
            shape.bakeExposureDuration(time);
        }
    }
}
