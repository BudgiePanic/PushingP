/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.util.shape;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Translation;

/**
 * A motion shape is a shape that contains one internal shape.
 * The shape contains a motion vector that moves the location of the internal shape based on the time that intersection rays arrive.
 * The motion shapes AABB fits the entire volume that the shape moves through during the shot exposure.
 * 
 * @author BudgiePanic
 */
public class LinearMotionShape extends BaseShape implements Parent {

    /**
     * The time at which the object's motion starts.
     */
    protected static final double motionStartTime = 0.0;

    /**
     * If no motion end time is provided, this supplier is used to get the default end time.
     */
    protected static final Supplier<Double> noEndTime = () -> 0.0;

    /**
     * Internal shape that is moved around by the motion shape
     */
    protected final Shape shape;

    /**
     * The velocity of the shape at the start of the exposure.
     */
    protected final Tuple initialVelocity;

    /**
     * Cached AABB the encompases the shape for the duration of the motion.
     */
    protected BoundingBox AABB;

    /**
     * The time at which the motion ends.
     */
    protected Optional<Double> motionEndTime;

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
     */
    public LinearMotionShape(Matrix4 transform, Shape internal, Tuple initialVelocity) { 
        super(transform); 
        this.shape = internal;
        this.initialVelocity = initialVelocity;
        this.AABB = null;
        this.motionEndTime = Optional.empty();
        this.shape.setParent(this);
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
        if (motionEndTime.isPresent() && !bounds().intersect(ray)) {
            // only use AABB optimization if we know the time when the motion ends
            return Optional.empty();
        }
        // test if the shape meets the inclusion condition
        if (!inclusionCondition.test(shape)) { return Optional.empty(); }
        // create a new ray that was moved by the offset amount, this is logically equivalent to moving the shape in the direction of the motion
        // we do this instead of transforming the shape because our shape's are immutable. 
        final double time = ray.time();
        final Tuple offset = this.initialVelocity.multiply(time); 
        final Ray transformedRay = new Ray(ray.origin().add(offset.negate()), ray.direction()); 
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
    public void setMotionEndTime(Optional<Double> motionEndTime) {
        if (motionEndTime == null) { throw new IllegalArgumentException("motion end time cannot be null"); }
        this.motionEndTime = motionEndTime;
        this.AABB = null; // force AABB regeneration on next bounds() call
    }

    @Override
    public synchronized BoundingBox bounds() { 
        if (AABB == null) {
            // the cube has 8 points [000,100,001,101,010,110,011,111]
            final BoundingBox localAABB = shape.bounds();
            final var aabbMin = localAABB.minimum();
            final var aabbMax = localAABB.maximum();
            final var transform = shape.transform();
            
            Tuple _000 = transform.multiply(new Tuple(aabbMax.x, aabbMin.y, aabbMin.z)); // MAX MIN 
            Tuple _100 = transform.multiply(aabbMin); // MIN                                MIN MIN
            Tuple _001 = transform.multiply(new Tuple(aabbMax.x, aabbMin.y, aabbMax.z)); // MAX MAX
            Tuple _101 = transform.multiply(new Tuple(aabbMin.x, aabbMin.y, aabbMax.z)); // MIN MAX
            
            Tuple _010 = transform.multiply(new Tuple(aabbMax.x, aabbMax.y, aabbMin.z)); // MAX MIN
            Tuple _110 = transform.multiply(new Tuple(aabbMin.x, aabbMax.y, aabbMin.z)); // MIN MIN
            Tuple _011 = transform.multiply(aabbMax); // MAX                                MAX MAX
            Tuple _111 = transform.multiply(new Tuple(aabbMin.x, aabbMax.y, aabbMax.z)); // MIN MAX
            var points = List.of(_000, _001, _010, _011, _100, _101, _110, _111);
            
            BoundingBox box = new BoundingBox(_000, _000);
            // check if we need to grow the AABB extents to contain the points
            for (var point : points) {
                if (!box.contains(point)) {
                    box = box.grow(point);
                }
            }
            if (motionEndTime.isEmpty()) {
                System.out.println("WARN: motion shape has no end time set, assuming shape does not move for AABB generation");
            } else {
                // move the points by velocity * exposure time and grow the AABB again
                // in the future, for non linear motion, 
                // we would have to get the max and minimum of the movement function in the bounds 0 to motion end time
                // here, the motion function is implicity a linear function [y = vel*time + initial_position]
                final var offset = initialVelocity.multiply(motionEndTime.get());
                final var velocityOffset = Translation.makeTranslationMatrix(offset.x, offset.y, offset.z);
                for (var point : points) {
                    final var pointAtEndOfMotion = velocityOffset.multiply(point);
                    if (!box.contains(pointAtEndOfMotion)) {
                        box = box.grow(pointAtEndOfMotion);
                    }
                }
            }
            AABB = box;
        } 
        return this.AABB;
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

    @Override
    public void bakeExposureDuration(double duration) { this.setMotionEndTime(Optional.of(duration)); }

    /**
     * Get the velocity of this shape.
     * @param time
     *   The time the velocity is sampled
     * @return
     *   The velocity of the shape when sampled at time 'time'.
     */
    public Tuple velocity(double time) {
        // linear motion shape with no forces acting upon it
        // NOTE: if more types of motion shapes are added, this method should become part of the interface
        return this.initialVelocity;
    }

    @Override
    public Shape divide(int threshold) {
        this.shape.divide(threshold);
        return this;
    }
    
    @Override
    public Collection<Shape> children() { return Collections.singleton(this.shape); }

}
