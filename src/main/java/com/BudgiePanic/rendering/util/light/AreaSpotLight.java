package com.BudgiePanic.rendering.util.light;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.RandomSuppliers;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Transforms;
import com.BudgiePanic.rendering.util.transform.Translation;

/**
 * The area spot light has a circular flat light emitting surface.
 *
 * @author BudgiePanic
 */
public class AreaSpotLight implements Light {

    /**
     * The recommended default number of samples.
     */
    public static final int defaultSamples = 20;
    /**
     * The default source of randomness.
     */
    public static final Supplier<Double> defaultRandomSource = RandomSuppliers.threadSafeRandomSupplier;
    /**
     * The position of the center of the light emitting surface in global space.
     */
    protected final Tuple position;
    /**
     * The position of the point of the cone in local space (will be behind the light emitting surface)
     */
    protected final Tuple localPosition;
    /**
     * The direction of the light in local space.
     */
    protected static final Tuple localDirection = Directions.up;
    /**
     * A transform to convert points and vectors from global space to local space.
     */
    protected final Matrix4 transform;
    /**
     * The color of the light
     */
    protected final Color color;
    /**
     * The half angle of the inner cone. Points within this cone recieve full illumination.
     */
    protected final double innerAngle;
    /**
     * The half angle of the cone defined by the area light
     */
    protected final double coneAngle;
    /**
     * Half the length of the light emitting surface.
     */
    protected final double areaRadius;
    /**
     * The maximum number of samples that can be taken of this area spot light.
     */
    protected final int samples;
    /**
     * Supplier of randomness for random sampling.
     * Provides uniform random floats between 0 and 1.
     */
    protected final Supplier<Double> randomSource;

    /**
     * Create a new area spot light. Canonincal constructor.
     * 
     * @param position
     *   The location of the spotlight.
     * @param direction
     *   The direction the spot light faces.
     * @param color
     *   The color and intensity of the spotlight.
     * @param innerAngle
     *   The half inner angle of the cone where the light provides full intensity in radians.
     * @param coneAngle
     *   The half inner angle of the cone where the light provides partial intensity in radians.
     * @param areaRadius
     *   The radius of the light emitting surface circle.
     * @param samples
     *   The number of times the light surface should be sampled when calculating a point's illumation.
     * @param randomSource
     *   Randomness source for the sample points.
     */
    public AreaSpotLight(Tuple position, Tuple direction, Color color, double innerAngle, double coneAngle, double areaRadius, int samples, Supplier<Double> randomSource) {
        this.position = position; this.color = color; this.innerAngle = innerAngle; this.coneAngle = coneAngle; 
        this.areaRadius = areaRadius; this.samples = samples; this.randomSource = randomSource;
        this.transform = lookAt(direction, position);
        this.localPosition = createLocalPosition(areaRadius, coneAngle);
        // make the inverse matrix now so it is cached for later
        // if the inverse fails, better to happen here to help track down the problem
        this.transform.inverse();
        if (coneAngle >= Math.PI || innerAngle >= Math.PI) {
            System.out.println("WARN: area spot light has excessively large cone angle " + coneAngle + " " + innerAngle);
        }
    }

    /**
     * Create a new area spot light. Uses the default randomness source.
     * 
     * @param position
     *   The location of the spotlight.
     * @param direction
     *   The direction the spot light faces.
     * @param color
     *   The color and intensity of the spotlight.
     * @param innerAngle
     *   The half inner angle of the cone where the light provides full intensity in radians.
     * @param coneAngle
     *   The half inner angle of the cone where the light provides partial intensity in radians.
     * @param areaRadius
     *   The radius of the light emitting surface circle.
     * @param samples
     *   The number of times the light surface should be sampled when calculating a point's illumation.
     */
    public AreaSpotLight(Tuple position, Tuple direction, Color color, double innerAngle, double coneAngle, double areaRadius, int samples) {
        this(position, direction, color, innerAngle, coneAngle, areaRadius, samples, AreaSpotLight.defaultRandomSource);
    }

    /**
     * Create a new area spot light. Uses the default randomness source and the default number of samples.
     * 
     * @param position
     *   The location of the spotlight.
     * @param direction
     *   The direction the spot light faces.
     * @param color
     *   The color and intensity of the spotlight.
     * @param innerAngle
     *   The half inner angle of the cone where the light provides full intensity in radians.
     * @param coneAngle
     *   The half inner angle of the cone where the light provides partial intensity in radians.
     * @param areaRadius
     *   The radius of the light emitting surface circle.
     */
    public AreaSpotLight(Tuple position, Tuple direction, Color color, double innerAngle, double coneAngle, double areaRadius) {
        this(position, direction, color, innerAngle, coneAngle, areaRadius, AreaSpotLight.defaultSamples);
    }
    /**
     * Derives the local space position of the tip of the spot light cone.
     *
     * @param areaRadius
     *   The radius of the light emitting surface circle.
     * @param coneAngle
     *   The half angle of the cone.
     * @return
     *   The position of the tip of the spot light cone in local space.
     */
    protected static Tuple createLocalPosition(double areaRadius, double coneAngle) {
        final var tanAngle = Math.tan(coneAngle);
        final var distance = areaRadius / tanAngle;
        return Tuple.makePoint(0, -distance, 0);
    }
    /**
     * Derives the area spot light transform.
     * The transform maps tuples in global space to local space.
     * For example the global direction will be mapped to [0,1,0,0] and the global position will be mapped to [0,0,0,1].
     *
     * @param direction
     *   The direction vector the spot light points in global space.
     * @param position
     *   The global space position of the spot light.
     * @return
     *   A transform to go from global space to local space.
     */
    protected static Matrix4 lookAt(Tuple direction, Tuple position) {
        final var angle = localDirection.angleBetween(direction);
        if (FloatHelp.compareFloat(0, angle) == 0) {
            return Transforms.identity().translate(-position.x, -position.y, -position.z).assemble();
        }
        if (FloatHelp.compareFloat((double)Math.PI, angle) == 0) {
            return Transforms.identity().rotateX((double)Math.PI).translate(-position.x, -position.y, -position.z).assemble();
        }
        final var c = Math.cos(angle);
        final var s = Math.sin(angle);
        final var t = 1.0 - c;
        final var axisOfRotation = direction.cross(localDirection).normalize();
        final double x = axisOfRotation.x, y = axisOfRotation.y, z = axisOfRotation.z;
        final var result = Matrix4.buildMatrix( // I think this is Rodrigues' rotation formula crammed into a 4 by 4 matrix
            t*x*x + c, t*x*y - z*s, t*x*z + y*s, 0,
            t*x*y + z*s, t*y*y + c, t*y*z - x*s, 0,
            t*x*z - y*s, t*y*z + x*s, t*z*z + c, 0,
            0, 0, 0, 1
        );
        return result.multiply(Translation.makeTranslationMatrix(-position.x, -position.y, -position.z));
    }

    /**
     * Produces samples on the light emitting surface in global space.
     */
    private class AreaSpotLightIterator implements Iterator<Tuple> {

        int samples = 0;

        @Override
        public boolean hasNext() { return samples < AreaSpotLight.this.samples; }

        @Override
        public Tuple next() {
            if (!hasNext()) throw new NoSuchElementException();
            samples++;
            final var angle = Math.PI * 2 * AreaSpotLight.this.randomSource.get();
            // @see: https://stackoverflow.com/questions/5837572/generate-a-random-point-within-a-circle-uniformly/50746409#50746409
            final var radius = areaRadius * Math.sqrt(AreaSpotLight.this.randomSource.get());
            final var localSample = AreaSpotLight.this.localSample(angle, radius);
            final var sample = AreaSpotLight.this.transform.inverse().multiply(localSample);
            return sample;
        }
    }

    /**
     * Create a sample on the light emission surface in local space.
     *
     * @param angle
     *   The angular displacement of the point from [1,0,0].
     * @param magnitude
     *   The distance of the point from the centre of the light surface.
     * @return
     *   A point on the light emitting surface in local space.
     */
    protected Tuple localSample(double angle, double magnitude) {
        final double x = Math.cos(angle) * magnitude;
        final double y = 0.0;
        final double z = Math.sin(angle) * magnitude;
        return makePoint(x, y, z);
    }

    /**
     * Convert a local space tuple to global space.
     * @param localSample
     *   The local space tuple.
     * @return
     *   The tuple in global space.
     */
    protected Tuple toGlobalSpace(Tuple localSample) {
        return transform.inverse().multiply(localSample);
    }

    @Override
    public Tuple position() { return this.position; }

    @Override
    public Color color() { return this.color; }

    @Override
    public double intensityAt(Tuple point, World world, double time) {
        final var localPoint = transform.multiply(point);
        if (!isInCone(localPoint)) { return 0.0; }
        // get the illumination that would have been recieved by a point spot light = max_possible_illumination
        // the intensity at the point will be [max_possible_illumination * (non_blocked_rays / samples_cast)]
        final Tuple lightToPoint = localPoint.subtract(localPosition);
        final var angle = localDirection.angleBetween(lightToPoint);
        final var maxIntensity = illumination(angle);
        final var sampler = sampler();
        var samplesBlocked = 0;
        while (sampler.hasNext()) {
            final var globalSample = sampler.next();
            if (world.isOccluded(point, globalSample, World.shadowCasters, time)) {
                samplesBlocked++;
            }
        }
        final double proportion = ((samples - samplesBlocked)) / (samples);
        return maxIntensity * proportion;
    }

    /**
     * remaps the angle between the light direction and the vector to the illuminated point to an illumination intensity value.
     * @param angle
     *   the angle between the light direction and the vector to the point being illuminated
     * @return
     */
    protected double illumination(double angle) {
        if (FloatHelp.compareFloat(angle, innerAngle) != 1) { return 1.0; } // angle <= innerAngle
        // NOTE: this should never happen because points outside the cone should have been filtered before this method was called
        if (FloatHelp.compareFloat(angle, coneAngle) == 1) { return 0.0; } // angle > coneAngle 
        // angle must be between inner angle and cone angle
        // need to LERP between 1 and zero based on how close the angle is to innerAngle
        final double oldMin = innerAngle, oldMax = coneAngle, newMin = 0.0, newMax = 1.0;
        final double invIntensity = ((angle - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
        return 1.0 - invIntensity;
    }
    
    /**
     * Checks if a point in local space lies within the area spot light's illumination cone.
     * @param localPoint
     *   The point to check in local space.
     * @return
     *   True if the point is within the spot light's illumination cone.
     */
    protected boolean isInCone(Tuple localPoint) {
        // check if point is behind light emitting surface?
        if (FloatHelp.compareFloat(0f, localPoint.y) == 1) { return false; } 
        // is the angle between (local direction) and (position to local point) smaller than the cone angle?
        final var lightToLocalPoint = localPoint.subtract(localPosition);
        final var angle = localDirection.angleBetween(lightToLocalPoint);
        return FloatHelp.compareFloat(coneAngle, angle) == 1;
    }

    @Override
    public Iterator<Tuple> sampler() { return new AreaSpotLightIterator(); }

    @Override
    public int resolution() { return this.samples; }
    
}
