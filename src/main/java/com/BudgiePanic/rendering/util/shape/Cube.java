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
        // NOTE: in graphics gems, this algorithm is optimized by first determining which 3 of the 6 cube faces
        //       the ray would hit first based on the rays direction vector, and then only testing the intersection values on those three planes
        //       lastly, the max value found is checked to be inside the cube.
        //       @see: https://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms 
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
        // the face of the cube that the point lies on can be determined by finding which component of the point is the largest
        final float x = Math.abs(point.x), y = Math.abs(point.y), z = Math.abs(point.z);
        final var max = Math.max(x, Math.max(y, z));
        final var isX = max == x; // points on cube corner will map to normals on the x faces of the cube
        final var isY = max == y; // points on the cube edges will map to normals on the y faces of the cube
        return Tuple.makeVector(isX ? point.x : 0, !isX && isY ? point.y : 0, !isX && !isY && max == z ? point.z : 0);
    }
    
}
