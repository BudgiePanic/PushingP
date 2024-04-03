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
    protected final int samples;
    // TODO store a random source to make random samples?

    public AreaSpotLight(Tuple position, Tuple direction, Color color, float innerAngle, float coneAngle, float areaRadius, int samples) {
        this.position = position; this.color = color; this.innerAngle = innerAngle; this.coneAngle = coneAngle; 
        this.areaRadius = areaRadius; this.samples = samples;
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
        return result.multiply(Translation.makeTranslationMatrix(position.x, position.y, position.z));
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
        if (!isInCone(localPoint)) {
            return 0f;
        }
        // TODO try use point spot light for intensity at, the sampling is only needed for Phong which uses the sampler() => issue, if ray is occluded, how to reduce the intensity? maybe reduce intensity by fraction of point-sample rays that are blocked?, could weight the rays by how close they are to light surface centre.
        // TODO try use point spot light anyway, maybe phong will hangle the soft shadows for us auto magically by virtue of the sampler???
        float accumulator = 0f; 
        for (int i = 0; i < samples; i++) {
            final var magnitude = 0f; // TODO randomize the local sample points
            final var sampleAngle = 0f;
            final var localSample = localSample(sampleAngle, magnitude); 
            final var globalSample = toGlobalSpace(localSample);
            if (world.isOccluded(point, globalSample, World.shadowCasters, time)) {
                continue;
            }
            // TODO if the sample lies within the circle defined by the innerCone then it contributes 1f illumation * 1 / angle_between_sample_direction_and_sample_to_point
            final var maxIllumination = illumination(magnitude);
            final var localPositionToSample = localSample.subtract(localPosition); 
            final var sampleToPoint = localPoint.subtract(localSample); 
            final var angle = localPositionToSample.angleBetween(sampleToPoint);
            assert angle < Math.PI;
            final var sampleIntensity = sampleIntensity(angle);
            accumulator += maxIllumination * sampleIntensity;
        }
        return accumulator / (float)samples;
    }

    /**
     * remaps the distance of a point on the light emission surface to a value between 0 and 1
     * if the point is close to the origin of the surface it has a value of 1
     * if the point lies between inner magnitude and area radius it is LERPed between 0 and 1
     * if the point lies beyond the areaRadius it should have been filtered out before this method was called
     * @param magnitude
     *   the sample point's distance form the light surface origin
     * @return
     */
    protected float illumination(float magnitude) {
        final var innerMag = (float) Math.tan(innerAngle) * -localPosition.y;
        assert innerMag <= areaRadius;
        if (compareFloat(innerMag, magnitude) == 1) {
            return 1;
        }
        final float oldMin = innerMag, oldMax = areaRadius, newMin = 0f, newMax = 1f;
        final float invIntensity = ((magnitude - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
        return 1f - invIntensity;
    }

    /**
     * remap the angle between a sample direction and sample to point vector to be between 0 and 1
     * @param angle
     * @return
     */
    protected float sampleIntensity(float angle) {
        final float oldMin = 0, oldMax = AngleHelp.toRadians(90f), newMin = 0f, newMax = 1f;
        final float remap = ((angle - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
        return (1f - remap);
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
