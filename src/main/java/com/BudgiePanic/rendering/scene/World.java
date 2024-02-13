package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
     * Compute a maximum of 4 light reflections for every ray cast into the world.
     */
    public static final int defaultRecursionDepth = 4;

    /**
     * The shapes in the scene.
     */
    protected List<Shape> shapes;
    /**
     * The lights in the scene.
     * 
     * NOTE: in the future, PointLight may need to be abtracted behind a Light interface.
     */
    protected List<PointLight> lights;

    /**
     * The shapes in the world that do not cast shadows, such as glass panes and water.
     */
    protected Set<Shape> noShadow;

    /**
     * Construct a new empty world.
     */
    public World() {
        this.shapes = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.noShadow = new HashSet<>();
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
     * @param castsShadows
     *   Whether or not this shape casts shadows in the world  
     */
    public void addShape(Shape shape, boolean castsShadows) {
        // precondition check, don't add null shapes
        if (shape == null) throw new IllegalArgumentException("shape cannot be null.");
        if (!castsShadows) {
            this.noShadow.add(shape);
        }
        this.shapes.add(shape);
    }

    /**
     * Add a new shape to the world that casts shadows.
     *
     * @param shape
     *   The shape to add.
     */
    public void addShape(Shape shape) {
        addShape(shape, true);
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
     * @param ray
     *   The ray to test with.
     * @return
     *   EMPTY if no intersections occured. List of intersections if any.
     */
    public Optional<List<Intersection>> intersect(Ray ray) {
        return intersect(ray, Collections.emptySet());
    }

    /**
     * Perform a ray intersection test against the shapes in the world.
     * Allows for easy intersection tests against multiple shapes.
     * @param ray
     *   The ray to test with.
     * @param ignoredShapes
     *   Shapes to exclude from the intersection test, for one reason or another.
     * @return
     *   EMPTY if no intersections occured. List of intersections if any.
     */
    protected Optional<List<Intersection>> intersect(Ray ray, Set<Shape> ignoredShapes) {
        var intersections = this.shapes.stream().
            filter(s -> !ignoredShapes.contains(s)).
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
     * @param depth
     *   recursion limit on light reflection calculations
     * @return
     *   The color of the point in the world given the shading information.
     */
    public Color shadeHit(ShadingInfo info, int depth) {
        if (info == null) throw new IllegalArgumentException("shading info should not be null");
        final var material = info.shape().material();
        final var hasReflectance = material.reflectivity() > 0 && material.transparency() > 0; // this expression could be extracted to Shading info 
        final Optional<Float> reflectance = hasReflectance ?  Optional.of(info.schlick()) : Optional.empty(); // this expression could be extracted to Shading info 
        return this.lights.stream().
            map((light) -> Phong.compute(info, light, inShadow(info.overPoint()))).
            map((color) -> color.add(this.shadeReflection(info, depth).multiply(reflectance.orElse(1.0f)))).
            map((color) -> color.add(this.shadeRefraction(info, depth).multiply(1f - reflectance.orElse(0f)))).
            reduce(Color::add).
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
        final float reflectivity = info.shape().material().reflectivity();
        if (depth < 1 || reflectivity <= 0f) {
            return Colors.black;
        }
        final var ray = new Ray(info.overPoint(), info.reflectVector());
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
            var intersections = this.intersect(ray, this.noShadow);
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
        final float transparency = info.shape().material().transparency();
        if (depth < 1 || transparency <= 0f) {
            return Colors.black;
        }
        // use snell's law to determine if total internal reflection has occured | t ~ theta
        // (sin(theta_i) / sin(theta_t)) = (n2 / n1)
        final var ratio = info.n1() / info.n2();
        final var cosI = info.eyeVector().dot(info.normalVector());
        final var sin2t = (ratio * ratio) * (1.0f - (cosI * cosI)); // via trig identity: sin(theta)^2 = 1 - cos(theta)^2
        if (sin2t > 1f) {
            return Colors.black;
        }
        final var cosT = (float) Math.sqrt(1.0 - sin2t); // via trig identity: cos(theta)^2 = 1 - sin(theta)^2
        final var refractionDirection = info.normalVector().multiply(ratio * cosI - cosT).subtract(info.eyeVector().multiply(ratio));
        final var refractionRay = new Ray(info.underPoint(), refractionDirection);
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
}
