package com.BudgiePanic.rendering.util.light;

import java.util.Iterator;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Directions;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.transform.Shear;

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
    protected final float coneAngle;
    protected final float areaRadius;
    protected final int samples;


    public AreaSpotLight(Tuple position, Tuple direction, Color color, float innerAngle, float coneAngle, float areaRadius, int samples) {
        this.position = position; this.color = color; this.innerAngle = innerAngle; this.coneAngle = coneAngle; 
        this.areaRadius = areaRadius; this.samples = samples;
        this.transform = lookAt(direction, position);
        this.localPosition = createLocalPosition(areaRadius, coneAngle);

    }

    /**
     * Get the light direction at a point on the light emitting surface.
     * @param sample
     *   The point on the light emitting surface in local space
     * @return
     *   The direction on the light emissiont at the point on the emission surface.
     */
    protected Tuple localDirectionAtSample(Tuple sample) { return localPosition.subtract(sample); }

    protected static Tuple createLocalPosition(float areaRadius, float coneAngle) {
        final var tanAngle = Math.tan(coneAngle);
        final var distance = areaRadius / tanAngle;
        return Tuple.makePoint(0, (float)-distance, 0);
    }

    protected static Matrix4 lookAt(Tuple direction, Tuple position) {
        final var angle = localDirection.angleBetween(direction);
        final var rotationAxis = localDirection.normalize().cross(position.normalize()); // rotation axis
        final var k = rotationAxis;
        final var sinAngle = (float) Math.sin(angle);
        final var oneMinusCosAngle = (float) (1.0 - Math.cos(angle));
        final var skewSymmetric = Matrix4.buildMatrix(
            0f, (-k.z), (k.y), 0f,
            (k.z), 0f, (-k.x), 0f, 
            (-k.y), (k.x), 0f, 0f,
            0f, 0f, 0f, 0f
        );
        final var skewSymmetricSquared = skewSymmetric.multiply(skewSymmetric);
        final var m = skewSymmetricSquared.matrix;
        final var skewSymmetrixSquaredOneCosTheta = Matrix4.buildMatrix(
            m[0][0] * oneMinusCosAngle, m[0][1] * oneMinusCosAngle, m[0][2] * oneMinusCosAngle, m[0][3] * oneMinusCosAngle,
            m[1][0] * oneMinusCosAngle, m[1][1] * oneMinusCosAngle, m[1][2] * oneMinusCosAngle, m[1][3] * oneMinusCosAngle,
            m[2][0] * oneMinusCosAngle, m[2][1] * oneMinusCosAngle, m[2][2] * oneMinusCosAngle, m[2][3] * oneMinusCosAngle,
            m[3][0] * oneMinusCosAngle, m[3][1] * oneMinusCosAngle, m[3][2] * oneMinusCosAngle, m[3][3] * oneMinusCosAngle
        );
        final var sinK = Shear.buildShearMatrix(
            (sinAngle * -k.z),
            (sinAngle * k.y),
            (sinAngle * k.z),
            (sinAngle * -k.x),
            (sinAngle * -k.y),
            (sinAngle * k.x)
        );
        final var a = sinK.matrix;
        final var b = skewSymmetrixSquaredOneCosTheta.matrix;
        final var result = Matrix4.buildMatrix(
            a[0][0] + b[0][0], a[0][1] + b[0][1], a[0][2] + b[0][2], a[0][3] + b[0][3],
            a[1][0] + b[1][0], a[1][1] + b[1][1], a[1][2] + b[1][2], a[1][3] + b[1][3],
            a[2][0] + b[2][0], a[2][1] + b[2][1], a[2][2] + b[2][2], a[2][3] + b[2][3],
            a[3][0] + b[3][0], a[3][1] + b[3][1], a[3][2] + b[3][2], a[3][3] + b[3][3]
        );
        return result;
    }

    @Override
    public Tuple position() { return this.position; }

    @Override
    public Color color() { return this.color; }

    @Override
    public float intensityAt(Tuple point, World world, float time) {
        // convert the point to local space (localPoint)
        // check angle between (localPosition to localPoint) and (localPosition)
        // if angle is good, find the point on the light surface where (localPosition to localPoint) intersects
        // check for obstruction between transform.inverse(lightSurfacePoint) and (point) within the world
        // if no obstruction 
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'intensityAt'");
    }


    protected float localIntensityAt(Tuple localPoint, World world, float time) {
        return 0f;
    }

    @Override
    public Iterator<Tuple> sampler() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sampler'");
    }

    @Override
    public int resolution() { return this.samples; }
    
}
