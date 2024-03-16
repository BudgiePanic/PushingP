package com.BudgiePanic.rendering.util.light;

import java.util.Optional;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.ShadingInfo;
import com.BudgiePanic.rendering.util.shape.Shape;

/**
 * Implementation of Phong reflection model.
 * 
 * @author BudgiePanic
 */
public final class Phong {

    private Phong() {}

    /**
     * Convience method to unwrap lighting information before passing off to Phong::compute implementation.
     *
     * @param info
     *   Information about the point being illuminated
     * @param light
     *   The light that is illuminating the point
     * @param intensity
     *   the intensity of the light source
     * @return
     *   The color of the point described in the shading information record.
     */
    public static Color compute(ShadingInfo info, Light light, float intensity) {
        return compute(info.shape().material(), light, info.overPoint(), info.eyeVector(), info.normalVector(), intensity, Optional.of(info.shape()));
    }

    /**
     * Uses the Phong lighting model to compute the color of a point using material properties.
     * 
     * NOTE: it may be beneficial to add a lightmodel interface abstraction, which Phong can implement
     *       to allow other lighting models to be hot swapped into a scene.
     * 
     * @param light
     *   The scene light
     * @param position
     *   The point being lit
     * @param eye
     *   The camera look vector
     * @param normal
     *   The normal of the surface at the position
     * @return
     *   The color at point 'position'
     */
    public static Color compute(Material material, Light light, Tuple position, Tuple eye, Tuple normal) {
        return compute(material, light, position, eye, normal, 1f, Optional.empty());
    }

    /**
     * Uses the Phong lighting model to compute the color of a point using the material properties.
     * 
     * @param light
     *   The light that is illuminating the position
     * @param position
     *   The point being lit
     * @param eye
     *   the position of the observer
     * @param normal
     *   surface normal at 'position'
     * @param intensity
     *   the intensity of the light source
     * @param shape
     *   the shape that the surface of the point being lit belongs to, if any
     * @return
     *   The color at point 'position'
     */
    public static Color compute(Material material, Light light, Tuple position, Tuple eye, Tuple normal, float intensity,  Optional<Shape> shape) {
        final var pattern = material.pattern();
        final var color = shape.map(sh -> pattern.colorAt(position, sh::toObjectSpace)).orElse(pattern.colorAt(position));
        assert color != null;
        final var effective = color.colorMul(light.color());
        final var directionToLight = light.position().subtract(position).normalize();
        final var ambient = effective.multiply(material.ambient());
        final var lightNormalAngle = directionToLight.dot(normal);
        if (FloatHelp.compareFloat(0, intensity) != -1 || lightNormalAngle < 0f) {
            return ambient;
        }
        final Color diffuse = effective.multiply(material.diffuse()).multiply(lightNormalAngle).multiply(intensity);
        final var reflection = directionToLight.negate().reflect(normal);
        final var eyeReflectAngle = reflection.dot(eye);
        if (eyeReflectAngle < 0f) {
            return diffuse.add(ambient);
        }
        final var factor = (float)Math.pow(eyeReflectAngle, material.shininess());
        final var specular = light.color().multiply(material.specular()).multiply(factor).multiply(intensity);
        return ambient.add(diffuse).add(specular);
    }

}
 