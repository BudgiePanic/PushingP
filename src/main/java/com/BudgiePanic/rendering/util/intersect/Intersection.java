package com.BudgiePanic.rendering.util.intersect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.shape.Parent;
import com.BudgiePanic.rendering.util.shape.Shape;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

/**
 * Ray intersection information container.
 * 
 * @author BudgiePanic
 */
public record Intersection(Float a, Shape shape, Optional<Pair<Float, Float>> uv) {
    
    // u & v specify the point on the shape's surface where the intersection occured, relative to the shape's corners (unwrapped)
    // not all intersections supply uv coordinates

    /**
     * Create a Ray intersection information container.
     * @param a
     *   The distance from the ray origin to the point of intersection
     * @param shape
     *   The shape that was intersected with
     * @param uv
     *   The uv coordinates of the intersection
     */
    public Intersection(Float a, Shape shape, Pair<Float, Float> uv) { this(a, shape, Optional.ofNullable(uv)); }

    /**
     * Create a Ray intersection information container.
     * @param a
     *   The distance from the ray origin to the point of intersection
     * @param shape
     *   The shape that was intersected with
     */
    public Intersection(Float a, Shape shape) { this(a, shape, Optional.empty()); }

    /**
     * Helper method to find hits among collections of intersections.
     * Some intersections may be behind the ray, and are thus not hits.
     * 
     * @param intersections
     *   The intersections from a ray-object intersection test
     * @return
     *   The first visible intersection point, as viewed from the Ray's origin, if any.
     */
    public static Optional<Intersection> Hit(Collection<Intersection> intersections) {
        final var zero = Float.valueOf(0f);
        var result = intersections.stream().
        filter((intersect)->{
            return compareFloat(intersect.a(), zero) > 0; // !(intersect.a() <= zero);
        }).sorted((i1, i2)->{
            return i1.a().compareTo(i2.a());
        }).findFirst();
        return result;
    }

    /**
     * Precompute information required by the lighting model.
     *
     * @param ray
     *   The intersecting ray.
     * @param intersections
     *   The intersections this ray made, if any.
     * @return
     *   Precomputed lighting information about this intersection.
     */
    public ShadingInfo computeShadingInfo(Ray ray, Optional<List<Intersection>> intersections) {
        var point = ray.position(this.a);
        var eye = ray.direction().negate();
        var normal = this.shape.normal(point, this);
        var inside = false;
        if (normal.dot(eye) < 0f) {
            inside = true;
            normal = normal.negate();
        }
        
        var reflection = ray.direction().reflect(normal);
        var n1 = 1.0f;
        var n2 = 1.0f;
        if (intersections.isPresent()) { 
            // naive algorithm implementation, could be optimized later.
            // determines the incoming and outgoing refractive index at the point the ray intersected with the object as defined in 'this' intersection by traversing the ray's sorted intersections
            // NOTE: was unable to refactor this out to its own method because it mutates both n1 and n2, while a method can only return one value.
            var hit = this;
            List<Shape> shapes = new ArrayList<>();
            for(Intersection intersection : intersections.get()) {
                if (intersection.equals(hit)) {
                    if (shapes.isEmpty()) {
                        n1 = 1.0f;
                    } else {
                        n1 = shapes.getLast().material().refractiveIndex();
                    }
                }
                if (shapes.contains(intersection.shape())) {
                    shapes.remove(intersection.shape());
                } else {
                    shapes.add(intersection.shape());
                }
                if (intersection.equals(hit)) {
                    if (shapes.isEmpty()) {
                        n2 = 1.0f;
                    } else {
                        n2 = shapes.getLast().material().refractiveIndex();
                    }
                    break;
                }
            }
        }
        return new ShadingInfo(this.a, this.shape, point, eye, normal, inside, reflection, n1, n2);
    }

    /**
     * Precompute information required by the lighting model.
     * Convience method for supplying no extra intersections to test.
     *
     * @param ray
     *   The intersecting ray.
     * @return
     *   Precomputed lighting information about this intersection.
     */
    public ShadingInfo computeShadingInfo(Ray ray) {
        return computeShadingInfo(ray, Optional.empty());
    }

    /**
     * Create a mapping function from ray and shape inclusion condition to shape-ray Intersection objects.
     * @param ray
     *     The ray to use in the intersection tests
     * @param inclusionCondition
     *     The condition to include shapes in the intersection test.
     * @return
     *   A function that intersects the ray with a shape, passing extra information to parent shapes for their intersection tests.
     */
    public static Function<Shape, Optional<List<Intersection>>> buildIntersector(Ray ray, Predicate<Shape> inclusionCondition) {
        // TODO maybe we won't make so many garbage collected Function objects if we pass 'this' to the parent
        // TODO instead of the predicate, since the concrete Parent shapes just call this function again anyway?
        return (s) -> { 
            if (s instanceof Parent) { 
                return ((Parent)s).intersect(ray, inclusionCondition);
            } else {
                return s.intersect(ray);
            }
        };
    }

}
