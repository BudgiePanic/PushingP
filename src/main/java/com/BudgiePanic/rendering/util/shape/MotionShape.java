package com.BudgiePanic.rendering.util.shape;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * A motion shape is a shape that contains one internal shape.
 * The shape contains a motion vector that moves the location of the internal shape based on the time that intersection rays arrive.
 * The motion shapes AABB fits the entire volume that the shape moves through during the shot exposure.
 * 
 * @author BudgiePanic
 */
public class MotionShape extends BaseShape implements Parent {

    /**
     * Force mode describes how the velocity should change during the exposure, if at all.
     * For example, gravity will pull the object downwards, affecting the velocity of the shape.
     */
    public static interface ForceMode {}

    /**
     * Applies a downwards acceleration to the motion shape over the course of the exposure
     */
    public static final ForceMode gravity = new ForceMode() {};
    /**
     * Applies no acceleration force to the motion shape over the course of the exposure
     */
    public static final ForceMode none = new ForceMode() {};

    /**
     * The time at which the object's motion starts.
     */
    protected static final float motionStartTime = 0f;

    /**
     * If no motion end time is provided, this supplier is used to get the default end time.
     */
    protected static final Supplier<Float> noEndTime = () -> 0f;

    /**
     * Internal shape that is moved around by the motion shape
     */
    protected final Shape shape;

    /**
     * The velocity of the shape at the start of the exposure.
     */
    protected final Tuple initialVelocity;

    /**
     * The force that acts on the motion shape over the course of the exposure, applying an acceleration.
     */
    protected final ForceMode force;

    /**
     * Cached AABB the encompases the shape for the duration of the motion.
     */
    protected BoundingBox AABB;

    /**
     * The time at which the motion ends.
     */
    protected Optional<Float> motionEndTime;

    /**
     * Canonical constructor.
     * Creates a new motion shape.
     * 
     * @param transform
     *   The transform to get to object local space at time = 0s of the exposure
     * @param internal 
     *   The internal shape that is being moved by the motion
     * @param initialVelocity
     *   A vector that describes the direction the shape moves in units per second
     * @param force
     *   The forces, if any, that cause accelerations upon the shape, changing the velocity over time
     */
    public MotionShape(Matrix4 transform, Shape internal, Tuple initialVelocity, ForceMode force) { 
        super(transform); 
        this.shape = internal;
        this.initialVelocity = initialVelocity;
        this.force = force;
        this.AABB = null;
        this.motionEndTime = Optional.empty();
    }

    /**
     * Creates a new motion shape with no forces acting upon it.
     * 
     * @param transform
     *   The transform to get to object local space at time = 0s of the exposure
     * @param internal 
     *   The internal shape that is being moved by the motion
     * @param initialVelocity
     *   A vector that describes the direction the shape moves in units per second
     */
    public MotionShape(Matrix4 transform, Shape internal, Tuple initialVelocity) { 
        super(transform); 
        this.shape = internal;
        this.initialVelocity = initialVelocity;
        this.force = none;
        this.AABB = null;
        this.motionEndTime = Optional.empty();
    }

    /**
     * Perform local intersection test against child shape.
     * Transforms the rays position in the opposite direction of the shape motion to simulate the shape moving.
     * @param ray
     *   The ray to test with.
     * @param inclusionCondition
     *   Filtering condition to check if the shape should be tested.
     * @return
     *   Ray-shape intersections that occured, if any.
     */
    protected Optional<List<Intersection>> localIntersect(Ray ray, Predicate<Shape> inclusionCondition) {
        if (!bounds().intersect(ray)) {
            return Optional.empty();
        }
        // test if the shape meets the inclusion condition
        if (!inclusionCondition.test(shape)) { return Optional.empty(); }
        // create a new ray that was moved by the offset amount, this is logically equivalent to moving the shape in the direction of the motion
        // we do this instead of transforming the shape because our shape's are immutable. 
        final float time = ray.time();
        final Tuple offset = this.initialVelocity.multiply(time); // TODO defer this to the ForceMode, motion math won't be a straight line unless ForceMode is none
        final Ray transformedRay = new Ray(ray.origin().add(offset.negate()), ray.direction()); // TODO should this tranformation be done here, in local object space? or in world space?
        // intersect the transformed ray against the child shape.
        final var mapper = Intersection.buildIntersector(transformedRay, inclusionCondition);
        final var intersections = mapper.apply(shape);
        return intersections;
    }

    /**
     * Set the time at which this object stops moving.
     * This should be the end time of the camera exposure.
     * The motion end time is used to determine the size of the shapes AABB.
     * 
     * @param motionEndTime
     *   The time at which the object stops moving.
     */
    public void setMotionEndTime(Optional<Float> motionEndTime) {
        if (motionEndTime == null) { throw new IllegalArgumentException("motion end time cannot be null"); }
        this.motionEndTime = motionEndTime;
        this.AABB = null; // force AABB regeneration on next bounds() call
    }

    @Override
    public synchronized BoundingBox bounds() { 
        if (motionEndTime.isPresent()) {
            // TODO bake an AABB that contains the shape at all stages of the exposure
        }
        if (AABB != null) {
            return this.AABB;
        }
        return this.shape.bounds(); 
    }

    @Override
    public boolean isSolid() { return this.shape.isSolid(); }

    @Override
    public boolean childrenContains(Shape shape) { return this.shape.contains(shape); }

    @Override
    public Optional<List<Intersection>> intersect(Ray ray, Predicate<Shape> inclusionCondition) {
        var transformInverse = this.transform().inverse();
        var rayInObjectSpace = ray.transform(transformInverse);
        return localIntersect(rayInObjectSpace, inclusionCondition);
    }

    @Override
    protected Optional<List<Intersection>> localIntersect(Ray ray) { return localIntersect(ray, s->true); }

    @Override
    protected Tuple localNormal(Tuple point) { throw new UnsupportedOperationException("Motion shape does not support local normal operation"); }
    
}
