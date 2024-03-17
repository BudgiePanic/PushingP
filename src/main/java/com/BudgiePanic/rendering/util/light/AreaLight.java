package com.BudgiePanic.rendering.util.light;

import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * An area light is a flat retangular panel light source.
 * 
 * @author BudgiePanic
 */
public record AreaLight(Color color, Tuple corner, Tuple uVector, Tuple vVector, int uStep, int vStep, Tuple vUnit, Tuple uUnit, Tuple position) implements Light {

    /**
     * Create a new area light. Autogenerates unit vectors and position.
     *
     * @param color
     *   The color of the area light, magnitude of the color doubles as the light intensity.
     * @param corner
     *   The world space location of the area light corner.
     *   The uv vectors define which corner of the area light rectangle the corner belongs to.
     * @param uVector
     *   The direction and magnitude of the u edge of the area light rectangle.
     * @param vVector
     *   The direction and magnitude of the v edge of the area light rectangle.
     * @param uStep
     *   The number of segments the u vector is divided into.
     * @param vStep
     *   The number of segments the v vector is divided into.
     */
    public AreaLight(Color color, Tuple corner, Tuple uVector, Tuple vVector, int uStep, int vStep) {
        this(color, corner, uVector, vVector, uStep, vStep, vVector.divide(vStep), uVector.divide(uStep), 
          corner.add(vVector.multiply((0.5f)).add(uVector.multiply(0.5f)))
        );
    }

    /**
     * Canonical constructor. Call a convience constructor instead.
     * 
     * @param color
     *   The color of the area light, magnitude of the color doubles as the light intensity.
     * @param corner
     *   The world space location of the area light corner.
     *   The uv vectors define which corner of the area light rectangle the corner belongs to.
     * @param uVector
     *   The direction and magnitude of the u edge of the area light rectangle.
     * @param vVector
     *   The direction and magnitude of the v edge of the area light rectangle.
     * @param uStep
     *   The number of segments the u vector is divided into.
     * @param vStep
     *   The number of segments the v vector is divided into.
     * @param vUnit
     *   A unit vector of length vVector.magnitue / vStep to move one segment along the v edge.
     * @param uUnit
     *   A unit vector of length uVector.magnitue / uStep to move one segment along the u edge.
     */
    public AreaLight(Color color, Tuple corner, Tuple uVector, Tuple vVector, int uStep, int vStep, Tuple vUnit, Tuple uUnit, Tuple position) {
        if (uStep == 0) throw new IllegalArgumentException("area light cannot contain 0 u segments");
        if (vStep == 0) throw new IllegalArgumentException("area light cannot contain 0 v segments");
        this.color = color; this.corner = corner; this.uVector = uVector; this.vVector = vVector;
        this.uStep = uStep; this.vStep = vStep; this.vUnit = vUnit; this.uUnit = uUnit; this.position = position;
    }

    /**
     * The resolution of the area that is sampled for shadow tests by the area light.
     * @return
     *   The resolution of the area light.
     */
    public int resolution() { return uStep * vStep; }

    /**
     * Get a point on the surface of the area light.
     *
     * @param u
     *   The distance along the u edge of the light.
     * @param v
     *   The distance along the v edge of the light.
     * @return
     *   The world space position of the point located at uv on the light surface.
     */
    public Tuple sample(float u, float v) {
        return corner.add(uUnit.multiply(0.5f + u)).add(vUnit.multiply(0.5f + v));
    }

    @Override
    public float intensityAt(Tuple point, World world) {
        float accumulator = 0f;
        for (int u = 0; u < uStep; u++) {
            for (int v = 0; v < vStep; v++) {
                final var sample = sample(u, v);
                final var isShadowed = world.isOccluded(point, sample, World.shadowCasters);
                accumulator += isShadowed ? 0f : 1f;
            }
        }
        return accumulator / (float) resolution();
    }
    
}
