package com.BudgiePanic.rendering.util.intersect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.shape.Shape;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

/**
 * Ray intersection information container.
 * 
 * @param a 
 *   The distance from the ray origin to the point of intersection
 * @param shape
 *   The sphere that was intersected with
 * 
 * @author BudgiePanic
 */
public record Intersection(Float a, Shape shape) {
    
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
     * @param
     *   The intersections this ray made, if any.
     * @return
     *   Precomputed lighting information about this intersection.
     */
    public ShadingInfo computeShadingInfo(Ray ray, Optional<List<Intersection>> intersections) {
        var point = ray.position(this.a);
        var eye = ray.direction().negate();
        var normal = this.shape.normal(point);
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

    }

}
