package com.BudgiePanic.rendering.util.light;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.AngleHelp;
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

    protected final Tuple position;
    protected final Tuple localPosition;
    protected static final Tuple localDirection = Directions.up;
    protected final Matrix4 transform;
    protected final Color color;
    protected final float innerAngle;
    /**
     * The half angle of the cone defined by the area light
     */
    protected final float coneAngle;
    protected final float areaRadius;
    /**
     * The maximum number of samples that can be taken of this area spot light.
     */
    protected final int samples;
    protected final Supplier<Float> randomSource;

    public AreaSpotLight(Tuple position, Tuple direction, Color color, float innerAngle, float coneAngle, float areaRadius, int samples, Supplier<Float> randomSource) {
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

    public AreaSpotLight(Tuple position, Tuple direction, Color color, float innerAngle, float coneAngle, float areaRadius, int samples) {
        this.position = position; this.color = color; this.innerAngle = innerAngle; this.coneAngle = coneAngle; 
        this.areaRadius = areaRadius; this.samples = samples; this.randomSource = RandomSuppliers.threadSafeRandomSupplier;
        this.transform = lookAt(direction, position);
        this.localPosition = createLocalPosition(areaRadius, coneAngle);
        // make the inverse matrix now so it is cached for later
        // if the inverse fails, better to happen here to help track down the problem
        this.transform.inverse();
    }
    
    protected static Tuple createLocalPosition(float areaRadius, float coneAngle) {
        final var tanAngle = Math.tan(coneAngle);
        final var distance = areaRadius / tanAngle;
        return Tuple.makePoint(0, (float)-distance, 0);
    }

    protected static Matrix4 lookAt(Tuple direction, Tuple position) {
        final var angle = localDirection.angleBetween(direction);
        if (FloatHelp.compareFloat(0, angle) == 0) {
            return Transforms.identity().translate(position.x, position.y, position.z).assemble();
        }
        if (FloatHelp.compareFloat((float)Math.PI, angle) == 0) {
            return Transforms.identity().rotateX((float)Math.PI).translate(position.x, position.y, position.z).assemble();
        }
        final var c = (float)Math.cos(angle);
        final var s = (float)Math.sin(angle);
        final var t = 1f - c;
        final var axisOfRotation = direction.cross(localDirection).normalize();
        final float x = axisOfRotation.x, y = axisOfRotation.y, z = axisOfRotation.z;
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
            final var radius = areaRadius * AreaSpotLight.this.randomSource.get();
            final var localSample = AreaSpotLight.this.localSample((float) angle, radius);
            final var sample = AreaSpotLight.this.transform.inverse().multiply(localSample);
            return sample;
        }
    }

    /**
     * Get the light direction at a point on the light emitting surface.
     * @param sample
     *   The point on the light emitting surface in local space
     * @return
     *   The direction on the light emissiont at the point on the emission surface.
     */
    protected Tuple localDirectionAtSample(Tuple sample) { return localPosition.subtract(sample); }

    /**
     * Sample a point on the light emission surface in local space.
     *
     * @param angle
     *   The angular displacement of the point from [1,0,0]
     * @param magnitude
     *   The distance of the point from the centre of the light surface
     * @return
     */
    protected Tuple localSample(float angle, float magnitude) {
        final float x = (float) Math.cos(angle) * magnitude;
        final float y = 0f;
        final float z = (float) Math.sin(angle) * magnitude;
        return makePoint(x, y, z);
    }

    /**
     * Convert a local sample point on the light emission surface to global space.
     * @param localSample
     *   The local light surface sample point
     * @return
     */
    protected Tuple toGlobalSpace(Tuple localSample) {
        return transform.inverse().multiply(localSample);
    }

    @Override
    public Tuple position() { return this.position; }

    @Override
    public Color color() { return this.color; }

    @Override
    public float intensityAt(Tuple point, World world, float time) {
        final var localPoint = transform.multiply(point);
        if (!isInCone(localPoint)) { return 0f; }
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
        final float proportion = ((float)(samples - samplesBlocked)) / ((float)samples);
        return maxIntensity * proportion;
    }

    /**
     * remaps the angle between the light direction and the vector to the illuminated point to an illumination intensity value.
     * @param angle
     *   the angle between the light direction and the vector to the point being illuminated
     * @return
     */
    protected float illumination(float angle) {
        if (FloatHelp.compareFloat(angle, innerAngle) != 1) { return 1f; } // angle <= innerAngle
        // NOTE: this should never happen because points outside the cone should have been filtered before this method was called
        if (FloatHelp.compareFloat(angle, coneAngle) == 1) { return 0f; } // angle > coneAngle 
        // angle must be between inner angle and cone angle
        // need to LERP between 1 and zero based on how close the angle is to innerAngle
        final float oldMin = innerAngle, oldMax = coneAngle, newMin = 0f, newMax = 1f;
        final float invIntensity = ((angle - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
        return 1f - invIntensity;
    }
    
    /**
     * checks if the angle between local position and position to local point is smaller than the cone angle
     * @param localPoint
     * @return
     */
    protected boolean isInCone(Tuple localPoint) {
        if (FloatHelp.compareFloat(0f, localPoint.y) == 1) { return false; } // point is behind light emitting surface
        final var lightToLocalPoint = localPoint.subtract(localPosition);
        final var angle = localDirection.angleBetween(lightToLocalPoint);
        return FloatHelp.compareFloat(coneAngle, angle) == 1;
    }

    @Override
    public Iterator<Tuple> sampler() { return new AreaSpotLightIterator(); }

    @Override
    public int resolution() { return this.samples; }
    
}
