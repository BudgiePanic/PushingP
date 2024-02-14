package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Axis Aligned Bounding Box cube.
 * 
 * @author BudgiePanic
 */
public class Cube extends BaseShape {

    public Cube(Matrix4 transform) { super(transform); }

    public Cube(Matrix4 transform, Material material) { super(transform, material); }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) {
        // along each plane pair on the cube surfaces
        // there will be a intersection point that is the smallest maximum intersection point
        // and the largest minimum intersection point, these are the cube intersection points
        final var origin = ray.origin();
        final var direction = ray.direction();
        final var x = checkAxis(origin.x, direction.x);
        final var y = checkAxis(origin.y, direction.y);
        final var z = checkAxis(origin.z, direction.z);
        final var min = Math.max(x.a(), Math.max(y.a(), z.a()));
        final var max = Math.min(x.b(), Math.min(y.b(), z.b()));
        if (min > max) {
            return Optional.empty();
        }       
        return Optional.of(List.of(new Intersection(min, this), new Intersection(max, this)));
    }

    /**
     * Determine where the ray intersects a cube plane pair. 
     * @param origin
     * @param direction
     * @return
     */
    private Pair<Float, Float> checkAxis(float origin, float direction) {
        // the first plane is originated at -1
        // the second plane is originated at +1
        final var closePlane = (-1 - origin);
        final var farPlane = (1 - origin);
        final var min = closePlane / direction;
        final var max = farPlane / direction;
        if (min > max) {
            return new Pair<>(max, min);
        }
        return new Pair<>(min, max);
    }


    @Override
    protected Tuple localNormal(Tuple point) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localNormal'");
    }
    
}
