package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.intersect.ShadingInfo;
import com.BudgiePanic.rendering.util.light.Phong;
import com.BudgiePanic.rendering.util.light.PointLight;
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
     * The shapes in the scene.
     * 
     * NOTE: in the future, sphere may need to be abstracted behind a Shape interface
     */
    protected List<Shape> shapes;
    /**
     * The lights in the scene.
     * 
     * NOTE: in the future, PointLight may need to be abtracted behind a Light interface.
     */
    protected List<PointLight> lights;

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
    public List<PointLight> getLights() {
        return this.lights;
    }

    /**
     * Add a new shape to the world.
     * 
     * @param shape
     *   The shape to add. Cannot be null.
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
    public void addLight(PointLight light) {
        // precondition checks, don't add null lights
        if (light == null) throw new IllegalArgumentException("light cannot be null.");
        this.lights.add(light);
    }

    /**
     * Perform a ray intersection test against the shapes in the world.
     * Allows for easy intersection tests against multiple shapes.
     * 
     * NOTE: this method could be sped up in the future with a parrallel stream? 
     * 
     * @param ray
     *   The ray to test with.
     * @return
     *   EMPTY if no intersections occured. List of intersections if any.
     */
    public Optional<List<Intersection>> intersect(Ray ray) {
        var intersections = this.shapes.stream().
            map((shape) -> shape.intersect(ray)). // NOTE: if shape becomes an interface, then this can be replaced with Shape::intersect ?
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
     * @return
     *   The color of the point in the world given the shading information.
     */
    public Color shadeHit(ShadingInfo info) {
        if (info == null) throw new IllegalArgumentException("shading info should not be null");
        return this.lights.stream().
            map((light) -> Phong.compute(info, light, inShadow(info.overPoint()))).
            map((color) -> this.shadeReflection(info).add(color)).
            reduce(Color::add). // NOTE: should this be ColorMul?
            orElse(Colors.black);
    }

    /**
     * Determine the color produced by a ray intersecting with the world.
     *
     * @param ray
     *   The ray
     * @return
     *   The color resulting from shading the ray intersection point within the world.
     */
    public Color computeColor(Ray ray) {
        var intersections = intersect(ray); 
        if (intersections.isPresent()) {
            var hit = Intersection.Hit(intersections.get());
            if (hit.isPresent()) {
                var info = hit.get().computeShadingInfo(ray);
                return shadeHit(info);
            }
        }
        return Colors.black;
    }

    /**
     * Find the color of the reflection vector in a shading info.
     * @param info
     *   Shading information
     * @return
     *   The color that lies along the shading info's reflection vector
     */
    public Color shadeReflection(ShadingInfo info) {
        final float reflectivity = info.shape().material().reflectivity();
        if (reflectivity <= 0f) {
            return Colors.black;
        }
        final var ray = new Ray(info.overPoint(), info.reflectVector());
        final var color = this.computeColor(ray);
        return color.multiply(reflectivity);
    }

    /**
     * Check if a point in the world is in shadow from another object
     *
     * @param point
     *   The location to check for illumination.
     * @return
     *   Whether the point can see a light in the scene without intersecting a closer object.
     */
    public boolean inShadow(Tuple point) {
        for (PointLight light : lights) {
            var pointToLight = light.position().subtract(point);
            var distance = pointToLight.magnitude();
            var ray = new Ray(point, pointToLight.normalize());
            var intersections = this.intersect(ray);
            if (intersections.isEmpty()) return false;
            var hit = Intersection.Hit(intersections.get());
            if (hit.isPresent()) {
                // distance to hit is smaller than distance to light, so it must be blocking the point's view to the light
                if (FloatHelp.compareFloat(hit.get().a(), distance) < 0) {
                    return true;
                }
            }
        } 
        return false;
    }
}
