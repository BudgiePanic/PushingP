package com.BudgiePanic.rendering.util.shape;

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
    protected final float minimum;
    /**
     * The upper y bounds distance that causes the cylinder to be truncated.
     */
    protected final float maximum;

    /**
     * The Cylinder.
     *
     * @param transform
     *   The cylinder transform
     * @param material
     *   The cylinder material 
     * @param maximum
     *   The uppper length of the cylinder
     * @param minimum
     *   The lower length of the cylinder
     */
    public Cylinder(Matrix4 transform, Material material, float maximum, float minimum) { super(transform, material); this.maximum = maximum; this.minimum = minimum; }

    /**
     * 
     * @param transform
     * @param material
     */
    public Cylinder(Matrix4 transform, Material material) { this(transform, material, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY); }

    /**
     * 
     * @param transform
     * @param maximum
     * @param minimum
     */
    public Cylinder(Matrix4 transform, float maximum, float minimum) { super(transform); this.maximum = maximum; this.minimum = minimum; }

    /**
     * 
     * @param transform
     */
    public Cylinder(Matrix4 transform) { this(transform, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY); }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) { 
        final var a = (ray.direction().x * ray.direction().x) + (ray.direction().z * ray.direction().z);
        if (FloatHelp.compareFloat(a, 0) == 0) {
            return Optional.empty();
        }
        final var b = (2 * ray.origin().x * ray.direction().x) + (2 * ray.origin().z * ray.direction().z);
        final var c = (ray.origin().x * ray.origin().x) + (ray.origin().z * ray.origin().z) - 1;
        final var discriminant = (b*b) - 4 * a * c;
        if (discriminant < 0) {
            return Optional.empty();
        }
        final float sqrtDisc = (float) Math.sqrt(discriminant);
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
        if (!addt0 && !addt1) return Optional.empty();
        if (addt0 && addt1) return Optional.of(List.of(new Intersection(Float.valueOf(t0), this), new Intersection(Float.valueOf(t1), this)));
        if (addt0) {
            assert !addt1;
            return Optional.of(List.of(new Intersection(Float.valueOf(t0), this)));
        } else {
            assert addt1 && !addt0;
            return Optional.of(List.of(new Intersection(Float.valueOf(t1), this)));
        }
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        return Tuple.makeVector(point.x, 0, point.z);
    }
    
}
