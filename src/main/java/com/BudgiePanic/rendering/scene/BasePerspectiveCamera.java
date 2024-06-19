package com.BudgiePanic.rendering.scene;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.Arrays;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Rotation;

/**
 * Base perspective camera contains the fields used by perspective concrete cameras.
 * @author BudgiePanic
 */
public abstract class BasePerspectiveCamera implements Camera {
    /**
     * The number of columns on the camera's imaging plane.
     */
    public final int width;
    /**
     * The number of rows on the camera's imaging plane.
     */
    public final int height;

    /**
     * The field of view of the camera in radians.
     */
    protected final double fov;

    /**
     * Transform to go to camera space.
     * Can move an object in the world relative to the camera which lies at [0,0,0]
     */
    protected final Matrix4 transform;

    /**
     * The world space size of the pixel. 
     * Pixel width == pixel height because pixels are square.
     */
    protected final double pixelSize;
    protected final double halfHeight;
    protected final double halfWidth;

    /**
     * The distance of the camera's imaging plane to the aperture. 
     * Affects the final image FOV as more distance constrains which rays can fit through the apeture, narrowing the image.
     */
    protected final double focalDistance;

    /**
     * The near clipping plane. Located 'focal distance' units away from the camera origin (0,0,0) in the -ve z direction.
     * Lies flat against the XY plane.
     */
    final ClippingPlane near;
    /**
     * The left clipping plane.
     */
    final ClippingPlane left;
    /**
     * The right clipping plane.
     */
    final ClippingPlane right;
    /**
     * The top clipping plane.
     */
    final ClippingPlane top;
    /**
     * The bottom clipping plane.
     */
    final ClippingPlane bottom;
    /**
     * All planes collected together to allow iteration over the planes.
     */
    final ClippingPlane[] planes;

    /**
     * Create a new Base perspective camera.
     * @param width
     *   The horizontal size of the camera in pixels.
     * @param height
     *   The vertical size of the camera in pixes.
     * @param fov
     *   The field of view of the camera in radians.
     * @param transform
     *   The camera transform.
     */
    public BasePerspectiveCamera(int width, int height, double fov, double focalDistance, Matrix4 transform) {
        this.width = width;
        this.height = height;
        this.fov = fov;
        this.focalDistance = focalDistance;
        this.transform = transform;
        // determine pixel size
        double halfView = Math.tan(fov/2.0);
        double aspect = ((double)width / (double)height);
        // assuming square pixels, so pixel width == pixel height
        this.halfWidth = (aspect >= 1.0) ? halfView : halfView * aspect; 
        this.halfHeight = (aspect >= 1.0) ? halfView / aspect : halfView;
        this.pixelSize = (halfWidth * 2.0) / width;
        final var zero = makePoint();
        final var forward = Directions.backward;
        this.near = new ClippingPlane(makePoint(0,0,-focalDistance), forward);
        this.left = new ClippingPlane(zero, Rotation.buildYRotationMatrix(fov * 0.5).multiply(forward).normalize());
        this.right = new ClippingPlane(zero, Rotation.buildYRotationMatrix(-fov * 0.5).multiply(forward).normalize());
        this.top = new ClippingPlane(zero, Rotation.buildXRotationMatrix(-fov * 0.5).multiply(forward).normalize());
        this.bottom = new ClippingPlane(zero, Rotation.buildXRotationMatrix(fov * 0.5).multiply(forward).normalize());
        planes = new ClippingPlane[] {near, left, right, top, bottom};
    }

    @Override
    public int width() { return this.width; }

    @Override
    public int height() { return this.height; }

    /**
     * project a 3D point in local camera space to a pixel on the camera screen
     * @param localPoint
     *   The point in camera space
     * @return
     *   An array of size two. The [column:x, row:y] of the pixel the point was projected onto. 
     */
    public final int[] project(Tuple localPoint) {
        // sources: 
        //     + https://gabrielgambetta.com/computer-graphics-from-scratch/01-common-concepts.html
        //     + https://gabrielgambetta.com/computer-graphics-from-scratch/09-perspective-projection.html 
        // "The Ray Tracer Challenge" camera looks in the -ve z direction in camera space
        // but the rasterizer camera in "Computer Graphics From Scratch" faces the +ve z direction.
        // To account for this fact, we need to negate the focal distance.
        final double projx = (localPoint.x * -focalDistance) / localPoint.z;
        final double projy = (localPoint.y * -focalDistance) / localPoint.z;
        final double screenx = (projx * width) / (2 * halfWidth);
        final double screeny = (projy * height) / (2 * halfHeight);
        // projected points are currently in normalized screen space. Convert them to pixel space.
        final int col = (width / 2) + (int) Math.round(screenx);
        final int row = (height / 2) + (int) Math.round(screeny);
        return new int[] {col, row};
    }

    /**
     * Converts a point in world space into camera local space.
     * @param worldPoint
     *   A point in world space.
     * @return
     *   The point transformed into local camera space.
     */
    public final Tuple transform(Tuple worldPoint) { return this.transform.multiply(worldPoint); }

    protected final record ClippingPlane(Tuple point, Tuple normal) {
        /**
         * Calculate the signed straightline distance between a point and this clipping plane.
         * @param point
         *   A point in local camera space.
         * @return
         *   The signed distance between the point and the plane.
         */
        double distanceTo(Tuple point) {
            final double a = normal.x, b = normal.y, c = normal.z;
            final double x0 = this.point.x, y0 = this.point.y, z0 = this.point.z;
            final double d = -(a * x0 + b * y0 + c * z0);
            final double x1 = point.x, y1 = point.y, z1 = point.z;
            final double numerator = a * x1 + b * y1 + c * z1 + d;
            final double denominator = Math.sqrt(a*a + b*b + c*c);
            return numerator / denominator;
        }
        /**
         * Determine whether a point is considered 'inside' a plane.
         * @param localPoint
         *   A point in local camera space.
         * @return
         *   True of the point is 'inside' of the clipping plane.
         */
        boolean pointInside(Tuple localPoint) { return distanceTo(localPoint) > 0.0; }
        /**
         * Clip a line segment against the clipping plane. 
         * This method should only be called when one point is inside the plane and the other is outside the plane.
         * Doing so otherwise is a waste of effort 
         * (both points inside = line segment is completely within the camera's FOV, both points outside = camera can't see the line segment)
         *
         * @param inside
         *   A point that is inside the clipping plane in local camera space.
         * @param outside
         *   A point that is outside the clipping plane in local camera space.
         * @return
         *   A new point that is the intersection point between the line segment and the clipping plane.
         */
        Tuple clip(Tuple inside, Tuple outside) { 
            Tuple direction = outside.subtract(inside);
            double denominator = normal.dot(direction);
            if (FloatHelp.compareFloat(0.0, denominator) == 0) {
                throw new RuntimeException("Could not intersect line " + Arrays.toString(new Tuple[] {inside, outside}) + " with plane " + this.toString());
            }
            double numerator = normal.dot(point.subtract(inside));
            double distance = numerator / denominator;
            Tuple intersection = inside.add(direction.multiply(distance));
            return intersection; 
        }
    }

    /**
     * Clips the points defining the start and end of a line segment against the camera's view frustrum.
     *
     * @param pointA
     *   The start of the line segment. A point in local camera space.
     * @param pointB
     *   The end of the line segment. A point in local camera space.
     * @return
     *   The start and end of the line segment that can be seen by the camera. 
     *   An empty optional is returned if no part of the line segment can be seen by the camera.
     */
    public final Optional<Pair<Tuple, Tuple>> clip(Tuple pointA, Tuple pointB) {
        for (final ClippingPlane plane : planes) {
            double distanceToPlaneA = plane.distanceTo(pointA);
            boolean isAInside = distanceToPlaneA > 0;
            double distanceToPlaneB = plane.distanceTo(pointB);
            boolean isBInside = distanceToPlaneB > 0;
            if (!isAInside && !isBInside) { return Optional.empty(); }
            if (isAInside && isBInside) { continue; }
            if (isAInside) {
                Tuple intersection = plane.clip(pointA, pointB);
                pointB = intersection;
            } else {
                assert isBInside;
                Tuple intersection = plane.clip(pointB, pointA);
                pointA = intersection;
            }
        }
        return Optional.of(new Pair<>(pointA, pointB));
    }
}

