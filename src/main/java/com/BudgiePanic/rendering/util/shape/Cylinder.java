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

    public Cylinder(Matrix4 transform, Material material) { super(transform, material); }

    public Cylinder(Matrix4 transform) { super(transform); }

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
        final var t0 = (-b - sqrtDisc) / (2*a);
        final var t1 = (-b + sqrtDisc) / (2*a);
        return Optional.of(List.of(new Intersection(Float.valueOf(t0), this), new Intersection(Float.valueOf(t1), this)));
    }

    @Override
    protected Tuple localNormal(Tuple point) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localNormal'");
    }
    
}
